package derekahedron.invexp.sack;

import derekahedron.invexp.registry.InvExpRegistryKeys;
import derekahedron.invexp.registry.DecentralizedReference;
import net.minecraft.item.Item;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Static manager for getting sack types and sack weights related to items. This is static so
 * we can easily grab the sack insertable without needing a world. The manager instance is created
 * when DataPacks are initialized. It uses Decentralized References that can be used on both client and
 * server instances.
 */
public class SackInsertableManager {
    private static SackInsertableManager INSTANCE;
    private final Map<Item, DecentralizedReference<SackType>> sackTypeMap;
    private final Map<Item, Integer> sackWeightMap;

    /**
     * Creates a new SackInsertableManager, pulling data from dynamic registries
     *
     * @param registryManager   manager to pull behavior from
     */
    public SackInsertableManager(@NotNull DynamicRegistryManager registryManager) {
        Registry<SackInsertable> registry = registryManager.getOrThrow(InvExpRegistryKeys.SACK_INSERTABLE);
        Registry<SackType> typeRegistry = registryManager.getOrThrow(InvExpRegistryKeys.SACK_TYPE);

        sackTypeMap = new HashMap<>();
        sackWeightMap = new HashMap<>();
        for (RegistryEntry<SackInsertable> entry : registry.getIndexedEntries()) {
            entry.getKey().ifPresent((key) -> {
                SackInsertable insertable = entry.value();
                // Match up identifier of insertable with identifier of item
                if (Registries.ITEM.containsId(key.getValue())) {
                    Item item = Registries.ITEM.get(key.getValue());
                    // Add mapping from item to sack type
                    insertable.sackType().ifPresent((sackType) ->
                            sackTypeMap.put(item, new DecentralizedReference<>(
                                    (RegistryEntry.Reference<SackType>) sackType,
                                    typeRegistry)
                            )
                    );
                    // Add mapping from item to sack weight
                    insertable.sackWeight().ifPresent((sackWeight) ->
                            sackWeightMap.put(item, sackWeight)
                    );
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
        if (sackTypeMap.containsKey(item)) {
           return sackTypeMap.get(item);
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
        if (sackWeightMap.containsKey(item)) {
            return sackWeightMap.get(item);
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
}
