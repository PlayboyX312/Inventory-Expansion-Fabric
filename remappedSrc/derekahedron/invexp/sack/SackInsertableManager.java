package derekahedron.invexp.sack;

import derekahedron.invexp.InventoryExpansion;
import derekahedron.invexp.item.ItemDuck;
import derekahedron.invexp.registry.InvExpRegistryKeys;
import derekahedron.invexp.registry.DecentralizedReference;
import net.minecraft.item.Item;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Static manager for getting sack types and sack weights related to items. This is static so
 * we can easily grab the sack insertable without needing a world. The manager instance is created
 * when DataPacks are initialized. It uses Decentralized References that can be used on both client and
 * server instances.
 */
public class SackInsertableManager {
    private static SackInsertableManager INSTANCE;
    private final Map<Item, SackInsertable> explicitSackData;
    private final Map<TagKey<Item>, TaggedSackInsertable> taggedSackData;
    private int syncId;

    /**
     * Creates a new SackInsertableManager, pulling data from dynamic registries
     *
     * @param registryManager   manager to pull behavior from
     */
    public SackInsertableManager(@NotNull DynamicRegistryManager registryManager) {
        syncId = INSTANCE != null ? INSTANCE.syncId: 0;
        explicitSackData = new HashMap<>();
        taggedSackData = new HashMap<>();

        for (RegistryEntry<SackTest> entry : registryManager.getOrThrow(InvExpRegistryKeys.SACK_TEST).getIndexedEntries()) {
            System.out.println(entry.value().items().getMatchingItems().toList());
        }

        Registry<SackInsertable> explicitRegistry = registryManager.getOrThrow(InvExpRegistryKeys.SACK_INSERTABLE);
        Registry<TaggedSackInsertable> taggedRegistry = registryManager.getOrThrow(InvExpRegistryKeys.TAGGED_SACK_INSERTABLE);
        Registry<SackType> typeRegistry = registryManager.getOrThrow(InvExpRegistryKeys.SACK_TYPE);

        // Create map with explicitly defined sack behavior
        for (RegistryEntry<SackInsertable> entry : explicitRegistry.getIndexedEntries()) {
            SackInsertable insertable = entry.value();

            entry.getKey().ifPresent((key) -> {
                Item item = Registries.ITEM.get(key.getValue());
                Optional<RegistryEntry<SackType>> sackType = insertable.sackType();

                // Add mapping from item to sack type
                if (sackType.isPresent()) {
                    sackType = Optional.of(new DecentralizedReference<>(
                            (RegistryEntry.Reference<SackType>) insertable.sackType().get(),
                            typeRegistry
                    ));
                }

                if (Registries.ITEM.containsId(key.getValue())) {
                    explicitSackData.put(item, new SackInsertable(
                            sackType, insertable.sackWeight()
                    ));
                }
            });
        }

        for (RegistryEntry<TaggedSackInsertable> entry : taggedRegistry.getIndexedEntries()) {
            TaggedSackInsertable insertable = entry.value();

            entry.getKey().ifPresent((key) -> {
                TagKey<Item> tagKey = TagKey.of(Registries.ITEM.getKey(), key.getRegistry());
                Optional<RegistryEntry<SackType>> sackType = insertable.sackType();

                // Add mapping from item to sack type
                if (sackType.isPresent()) {
                    sackType = Optional.of(new DecentralizedReference<>(
                            (RegistryEntry.Reference<SackType>) insertable.sackType().get(),
                            typeRegistry
                    ));
                }

                if (Registries.ITEM.containsId(key.getValue())) {
                    taggedSackData.put(tagKey, new TaggedSackInsertable(
                            insertable.priority(), sackType, insertable.sackWeight()
                    ));
                }
            });
        }

        updateDefaultSackInsertables();
    }

    public void updateDefaultSackInsertables() {
        syncId++;

        explicitSackData.forEach((item, data) -> ((ItemDuck) item).invexp$setDefaultSackInsertable(
                new DefaultSackInsertable(
                        data.sackType().orElse(null),
                        data.sackWeight().orElse(SacksHelper.DEFAULT_SACK_WEIGHT),
                        syncId
                )
        ));

        HashMap<Item, PrioritizedSackType> typeMap = new HashMap<>();
        HashMap<Item, PrioritizedSackWeight> weightMap = new HashMap<>();

        taggedSackData.forEach((tagKey, data) -> Registries.ITEM.getOptional(tagKey).ifPresent(named -> {

            for (Item item : named.stream().map(RegistryEntry::value).toList()) {

                if (hasDefaultSackInsertable(item)) {
                    return;
                }

                data.sackType().ifPresent(sackType -> {

                    if (!typeMap.containsKey(item)
                            || typeMap.get(item).overriddenBy(tagKey.id(), data)) {
                        typeMap.put(item, new PrioritizedSackType(
                                tagKey.id(), data.priority(), sackType
                        ));
                    }
                });

                data.sackWeight().ifPresent(sackWeight -> {

                    if (!weightMap.containsKey(item)
                            || weightMap.get(item).overriddenBy(tagKey.id(), data)) {
                        weightMap.put(item, new PrioritizedSackWeight(
                                tagKey.id(), data.priority(), sackWeight
                        ));
                    }
                });
            }
        }));

        typeMap.forEach((item, sackType) -> {
            int sackWeight = SacksHelper.DEFAULT_SACK_WEIGHT;
            if (weightMap.containsKey(item)) {
                sackWeight = weightMap.get(item).sackWeight;
            }
            ((ItemDuck) item).invexp$setDefaultSackInsertable(new DefaultSackInsertable(
                    sackType.sackType, sackWeight, syncId
            ));
        });
    }

    /**
     * Gets the default sack type for a given item.
     *
     * @param item  item to get the default type for
     * @return      sack type related this item defaults to; null if there is none
     */
    public RegistryKey<SackType> getType(@NotNull Item item) {
        DefaultSackInsertable data = ((ItemDuck) item).invexp$getDefaultSackInsertable();
        if (data != null) {
            return data.sackType.getKey().orElse(null);
        }
        return null;
    }

    /**
     * Gets the default sack weight for a given item.
     *
     * @param item  item to get the default weight for
     * @return      default sack weight for the given item; returns a default if there is none
     */
    public int getWeight(@NotNull Item item) {
        DefaultSackInsertable data = ((ItemDuck) item).invexp$getDefaultSackInsertable();
        if (data != null) {
            return data.sackWeight;
        }
        return SacksHelper.DEFAULT_SACK_WEIGHT;
    }

    public boolean hasDefaultSackInsertable(@NotNull Item item) {
        DefaultSackInsertable data = ((ItemDuck) item).invexp$getDefaultSackInsertable();
        return data != null && data.syncId == syncId;
    }

    /**
     * Gets the static manager instance.
     *
     * @return  static instance of the SackInsertableManager
     */
    public static SackInsertableManager getInstance() {
        return INSTANCE;
    }

    /**
     * Updates the tagged insertables of the instance if it exists.
     */
    public static void updateInstanceDefaultSackInsertables() {
        if (INSTANCE != null) {
            INSTANCE.updateDefaultSackInsertables();
        }
        else {
            InventoryExpansion.LOGGER.warn("Tagged insertables was updated before insertable manager instance created!");
        }
    }

    /**
     * Sets the static manager instance.
     *
     * @param instance  instance to set the static manager to
     */
    private static void setInstance(@Nullable SackInsertableManager instance) {
        INSTANCE = instance;
    }

    /**
     * Creates a new static manager instance from the given registries.
     *
     * @param registryManager   dynamic registries to create the manager from
     */
    public static void createNewInstance(@NotNull DynamicRegistryManager registryManager) {
        setInstance(new SackInsertableManager(registryManager));
    }

    /**
     * Record storing a sack type alongside a priority and a tag identifier.
     * Used to compare if data should be over-ridden when calculating tagged insertables.
     */
    private record PrioritizedSackType(Identifier tag, int priority, RegistryEntry<SackType> sackType) {
        public boolean overriddenBy(Identifier tag, TaggedSackInsertable data) {
            return priority < data.priority() || this.tag.compareTo(tag) < 0;
        }
    }

    /**
     * Record storing a sack weight alongside a priority and a tag identifier.
     * Used to compare if data should be over-ridden when calculating tagged insertables.
     */
    private record PrioritizedSackWeight(Identifier tag, int priority, int sackWeight) {
        public boolean overriddenBy(Identifier tag, TaggedSackInsertable data) {
            return priority < data.priority() || this.tag.compareTo(tag) < 0;
        }
    }

    public record DefaultSackInsertable(RegistryEntry<SackType> sackType, int sackWeight, int syncId) {}
}
