package derekahedron.invexp.sack;

import derekahedron.invexp.InventoryExpansion;
import derekahedron.invexp.registry.InvExpRegistryKeys;
import derekahedron.invexp.registry.DecentralizedReference;
import net.minecraft.item.Item;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
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
    private final Map<Item, InsertableData> explicitInsertables;
    private final List<TaggedInsertableData> taggedInsertables;
    private final Map<Item, PrioritizedSackType> taggedSackTypeMap;
    private final Map<Item, PrioritizedSackWeight> taggedSackWeightMap;

    /**
     * Creates a new SackInsertableManager, pulling data from dynamic registries
     *
     * @param registryManager   manager to pull behavior from
     */
    public SackInsertableManager(@NotNull DynamicRegistryManager registryManager) {
        Registry<SackInsertable> registry = registryManager.getOrThrow(InvExpRegistryKeys.SACK_INSERTABLE);
        Registry<TaggedSackInsertable> taggedRegistry = registryManager.getOrThrow(InvExpRegistryKeys.TAGGED_SACK_INSERTABLE);
        Registry<SackType> typeRegistry = registryManager.getOrThrow(InvExpRegistryKeys.SACK_TYPE);

        // Create dict with explicitly defined sack behavior
        explicitInsertables = new HashMap<>();
        for (RegistryEntry<SackInsertable> entry : registry.getIndexedEntries()) {
            entry.getKey().ifPresent((key) -> {
                SackInsertable insertable = entry.value();
                // Match up identifier of insertable with identifier of item
                if (Registries.ITEM.containsId(key.getValue())) {
                    Item item = Registries.ITEM.get(key.getValue());
                    DecentralizedReference<SackType> sackType = null;
                    int sackWeight = SacksHelper.DEFAULT_SACK_WEIGHT;

                    // Add mapping from item to sack type
                    if (insertable.sackType().isPresent()) {
                        sackType = new DecentralizedReference<>(
                                (RegistryEntry.Reference<SackType>) insertable.sackType().get(),
                                typeRegistry
                        );
                    }
                    if (insertable.sackWeight().isPresent()) {
                        sackWeight = insertable.sackWeight().get();
                    }

                    explicitInsertables.put(item, new InsertableData(sackType, sackWeight));
                }
            });
        }

        // Create list of sack behavior defined by tags
        taggedInsertables = new ArrayList<>(taggedRegistry.size());
        for (RegistryEntry<TaggedSackInsertable> entry : taggedRegistry.getIndexedEntries()) {
            if (entry.getKey().isPresent()) {
                TaggedSackInsertable insertable = entry.value();
                Optional<DecentralizedReference<SackType>> sackType;

                // Create decentralized reference if a sack type is passed in.
                if (insertable.sackType().isPresent()) {
                    sackType = Optional.of(new DecentralizedReference<>(
                            (RegistryEntry.Reference<SackType>) insertable.sackType().get(),
                            typeRegistry
                    ));
                }
                // Otherwise, create optional empty if there is a sack weight
                else if (insertable.sackWeight().isPresent()) {
                    sackType = Optional.empty();
                }
                // If there is no sack weight or sack type, ignore this entry
                else {
                    continue;
                }

                taggedInsertables.add(new TaggedInsertableData(
                        entry.getKey().get().getValue(), insertable.priority(), sackType, insertable.sackWeight())
                );
            }
        }

        // Create maps and calculate tagged insertables
        taggedSackTypeMap = new HashMap<>();
        taggedSackWeightMap = new HashMap<>();
        updateTaggedInsertables();
    }

    /**
     * Calculates tagged insertable behavior from the saved tags. Called on initial creation
     * and on tag reload.
     */
    public void updateTaggedInsertables() {
        // Start by resetting stored data
        taggedSackTypeMap.clear();
        taggedSackWeightMap.clear();

        for (TaggedInsertableData insertable : taggedInsertables) {
            // Get tag list from insertable tag name
            Registries.ITEM.getOptional(TagKey.of(Registries.ITEM.getKey(), insertable.tag)).ifPresent(named -> {
                for (Item item : named.stream().map(RegistryEntry::value).toList()) {
                    // Only override if the item is not already explicitly defined
                    if (!explicitInsertables.containsKey(item)) {
                        // Add sack type if it exists, and it has a higher priority than the previous sack type
                        insertable.sackType.ifPresent(sackType -> {
                            if (insertable.overrides(taggedSackTypeMap.get(item))) {
                                taggedSackTypeMap.put(item, new PrioritizedSackType(
                                        insertable.tag, insertable.priority, sackType
                                ));
                            }
                        });
                        // Add sack weight if it exists, and it has a higher priority than the previous sack weight
                        insertable.sackWeight.ifPresent(sackWeight -> {
                            if (insertable.overrides(taggedSackWeightMap.get(item))) {
                                taggedSackWeightMap.put(item, new PrioritizedSackWeight(
                                        insertable.tag, insertable.priority, sackWeight
                                ));
                            }
                        });
                    }
                }
            });
        }
    }

    /**
     * Gets the default sack type for a given item.
     *
     * @param item  item to get the default type for
     * @return      sack type related this item defaults to; null if there is none
     */
    public RegistryEntry.Reference<SackType> getType(@NotNull Item item) {
        if (explicitInsertables.containsKey(item)) {
           return explicitInsertables.get(item).sackType;
        }
        else if (taggedSackTypeMap.containsKey(item)) {
            return taggedSackTypeMap.get(item).sackType;
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
        if (explicitInsertables.containsKey(item)) {
            return explicitInsertables.get(item).sackWeight;
        }
        else if (taggedSackWeightMap.containsKey(item)) {
            return taggedSackWeightMap.get(item).sackWeight;
        }
        return SacksHelper.DEFAULT_SACK_WEIGHT;
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
    public static void updateInstanceTaggedInsertables() {
        if (INSTANCE != null) {
            INSTANCE.updateTaggedInsertables();
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
     * Record storing the sack type and weight to be stored in a map.
     *
     * @param sackType      sack type of the item
     * @param sackWeight    sack weight of the item
     */
    private record InsertableData(DecentralizedReference<SackType> sackType, int sackWeight) {}

    /**
     * Record storing the sack type and weight associated with an item tag. Contains a priority
     * that defines which values should be used when items are in multiple tags.
     *
     * @param tag           Identifier of the tag that is used
     * @param priority      the priority of the data relative to other tagged insertables
     * @param sackType      sack type of the tagged items
     * @param sackWeight    sack weight of the tagged items
     */
    private record TaggedInsertableData(
            Identifier tag, int priority,
            Optional<DecentralizedReference<SackType>> sackType, Optional<Integer> sackWeight
    ) {
        /**
         * Checks if this tagged insertable should override the given sack type by comparing
         * priority. If the priorities are the same, compare the tag alphabetically so
         * data is consistent.
         *
         * @param other     prioritized sack type to check
         * @return          true if this insertable should override the given sack type
         */
        public boolean overrides(@Nullable PrioritizedSackType other) {
            return other == null || priority > other.priority || tag.compareTo(other.tag) > 0;
        }

        /**
         * Checks if this tagged insertable should override the given sack weight by comparing
         * priority. If the priorities are the same, compare the tag alphabetically so
         * data is consistent.
         *
         * @param other     prioritized sack weight to check
         * @return          true if this insertable should override the given sack weight
         */
        public boolean overrides(@Nullable PrioritizedSackWeight other) {
            return other == null || priority > other.priority || tag.compareTo(other.tag) > 0;
        }
    }

    /**
     * Record storing a sack type alongside a priority and a tag identifier.
     * Used to compare if data should be over-ridden when calculating tagged insertables.
     */
    private record PrioritizedSackType(Identifier tag, int priority, DecentralizedReference<SackType> sackType) {}

    /**
     * Record storing a sack weight alongside a priority and a tag identifier.
     * Used to compare if data should be over-ridden when calculating tagged insertables.
     */
    private record PrioritizedSackWeight(Identifier tag, int priority, int sackWeight) {}
}
