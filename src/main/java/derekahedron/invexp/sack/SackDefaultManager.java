package derekahedron.invexp.sack;

import derekahedron.invexp.InventoryExpansion;
import derekahedron.invexp.item.ItemDuck;
import derekahedron.invexp.registry.InvExpRegistryKeys;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BeesComponent;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import org.apache.commons.lang3.math.Fraction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Static manager for getting sack types and sack weights related to items. This is static so
 * we can easily grab the sack insertable without needing a world. The manager instance is created
 * when DataPacks are initialized. It uses Decentralized References that can be used on both client and
 * server instances.
 */
public class SackDefaultManager {
    private static SackDefaultManager INSTANCE;
    private int syncId;
    private final List<RegistryEntry.Reference<SackTypeDefault>> typeDefaults;
    private final List<RegistryEntry.Reference<SackTypeDefault>> globalTypeDefaults;
    private final List<RegistryEntry.Reference<SackWeightDefault>> weightDefaults;
    private final List<RegistryEntry.Reference<SackWeightDefault>> globalWeightDefaults;

    /**
     * Creates a new SackInsertableManager, pulling data from dynamic registries
     *
     * @param registryManager   manager to pull behavior from
     */
    public SackDefaultManager(@NotNull DynamicRegistryManager registryManager) {
        syncId = INSTANCE != null ? INSTANCE.syncId: 0;

        List<RegistryEntry.Reference<SackTypeDefault>> types =
                registryManager.getOrThrow(InvExpRegistryKeys.SACK_TYPE_DEFINITION)
                        .streamEntries()
                        .sorted(SackDefaultManager::compareTypes)
                        .toList().reversed();

        List<RegistryEntry.Reference<SackWeightDefault>> weights =
                registryManager.getOrThrow(InvExpRegistryKeys.SACK_WEIGHT_DEFINITION)
                        .streamEntries()
                        .sorted(SackDefaultManager::compareWeights)
                        .toList().reversed();

        typeDefaults = types.stream().filter((type) -> type.value().items().isPresent()).toList();
        globalTypeDefaults = types.stream().filter((type) -> type.value().items().isEmpty()).toList();
        weightDefaults = weights.stream().filter((weight) -> weight.value().items().isPresent()).toList();
        globalWeightDefaults = weights.stream().filter((weight) -> weight.value().items().isEmpty()).toList();

        updateSackDefaults();
    }

    @SuppressWarnings("deprecation")
    public void updateSackDefaults() {
        syncId++;

        for (RegistryEntry.Reference<SackTypeDefault> typeDefault : typeDefaults) {
            typeDefault.value().items().ifPresent((items) -> items.getMatchingItems().forEach((item) ->
                    getOrCreateSackDefaults(item.value()).typeDefaults.add(typeDefault)));
        }

        for (RegistryEntry.Reference<SackWeightDefault> weightDefault : weightDefaults) {
            weightDefault.value().items().ifPresent((items) -> items.getMatchingItems().forEach((item) ->
                    getOrCreateSackDefaults(item.value()).weightDefaults.add(weightDefault)));
        }
    }

    public RegistryKey<SackType> getType(@NotNull ItemStack stack) {
        SackDefaults defaults = getSackDefaults(stack.getItem());
        List<RegistryEntry.Reference<SackTypeDefault>> itemTypeDefaults = defaults != null
                ? defaults.typeDefaults : List.of();

        int i = 0;
        int j = 0;
        while (i < globalTypeDefaults.size() || j < itemTypeDefaults.size()) {
            SackTypeDefault typeDefault;
            if (i < globalTypeDefaults.size()
                    && (j >= itemTypeDefaults.size()
                    || compareTypes(globalTypeDefaults.get(i), itemTypeDefaults.get(j)) > 0)) {
                typeDefault = globalTypeDefaults.get(i).value();
                i++;
            } else {
                typeDefault = itemTypeDefaults.get(j).value();
                j++;
            }
            if (typeDefault.test(stack)) {
                return typeDefault.sackType().isPresent()
                        ? typeDefault.sackType().get().getKey().orElse(null)
                        : null;
            }
        }

        if (stack.getItem() instanceof SpawnEggItem) {
            return SackTypes.SPAWN_EGG;
        } else if (stack.getItem() instanceof BucketItem) {
            return SackTypes.BUCKET;
        }
        return null;
    }

    public Fraction getWeight(@NotNull ItemStack stack) {
        SackDefaults defaults = getSackDefaults(stack.getItem());
        List<RegistryEntry.Reference<SackWeightDefault>> itemWeightDefaults = defaults != null
                ? defaults.weightDefaults : List.of();

        int i = 0;
        int j = 0;
        while (i < globalWeightDefaults.size() || j < itemWeightDefaults.size()) {
            SackWeightDefault weightDefault;
            if (i < globalWeightDefaults.size()
                    && (j >= itemWeightDefaults.size()
                    || compareWeights(globalWeightDefaults.get(i), itemWeightDefaults.get(j)) > 0)) {
                weightDefault = globalWeightDefaults.get(i).value();
                i++;
            } else {
                weightDefault = itemWeightDefaults.get(j).value();
                j++;
            }
            if (weightDefault.test(stack)) {
                return weightDefault.sackWeight().orElse(SacksHelper.DEFAULT_SACK_WEIGHT);
            }
        }
        if (stack.getItem() instanceof BucketItem bucketItem && !bucketItem.fluid.matchesType(Fluids.EMPTY)) {
            return Fraction.ONE_QUARTER;
        }
        if (stack.hasChangedComponent(DataComponentTypes.BEES)) {
            BeesComponent beesComponent = stack.getOrDefault(DataComponentTypes.BEES, BeesComponent.DEFAULT);
            if (!beesComponent.bees().isEmpty()) {
                return SacksHelper.DEFAULT_SACK_WEIGHT.multiplyBy(Fraction.getFraction(64));
            }
        }
        return SacksHelper.DEFAULT_SACK_WEIGHT;
    }

    public SackDefaults getSackDefaults(Item item) {
        SackDefaults defaults = ((ItemDuck) item).invexp$getSackDefaults();
        return defaults != null && defaults.syncId == syncId ? defaults : null;
    }

    public SackDefaults getOrCreateSackDefaults(Item item) {
        SackDefaults defaults = getSackDefaults(item);
        if (defaults == null) {
            defaults = new SackDefaults(new ArrayList<>(), new ArrayList<>(), syncId);
            ((ItemDuck) item).invexp$setSackDefaults(defaults);
        }
        return defaults;
    }

    /**
     * Gets the static manager instance.
     *
     * @return  static instance of the SackInsertableManager
     */
    public static SackDefaultManager getInstance() {
        return INSTANCE;
    }

    /**
     * Updates the tagged insertables of the instance if it exists.
     */
    public static void updateInstanceSackDefaults() {
        if (INSTANCE != null) {
            INSTANCE.updateSackDefaults();
        } else {
            InventoryExpansion.LOGGER.warn("Sack Defaults were updated before insertable manager instance created!");
        }
    }

    /**
     * Sets the static manager instance.
     *
     * @param instance  instance to set the static manager to
     */
    private static void setInstance(@Nullable SackDefaultManager instance) {
        INSTANCE = instance;
    }

    /**
     * Creates a new static manager instance from the given registries.
     *
     * @param registryManager   dynamic registries to create the manager from
     */
    public static void createNewInstance(@NotNull DynamicRegistryManager registryManager) {
        setInstance(new SackDefaultManager(registryManager));
    }

    public record SackDefaults(
            List<RegistryEntry.Reference<SackTypeDefault>> typeDefaults,
            List<RegistryEntry.Reference<SackWeightDefault>> weightDefaults,
            int syncId) {}

    private static int compareTypes(
            @NotNull RegistryEntry.Reference<SackTypeDefault> left,
            @NotNull RegistryEntry.Reference<SackTypeDefault> right) {
        int compareResult = left.value().compareTo(right.value());
        return compareResult != 0
                ? compareResult
                : left.registryKey().getValue().compareTo(right.registryKey().getValue());
    }

    private static int compareWeights(
            @NotNull RegistryEntry.Reference<SackWeightDefault> left,
            @NotNull RegistryEntry.Reference<SackWeightDefault> right) {
        int compareResult = left.value().compareTo(right.value());
        return compareResult != 0
                ? compareResult
                : left.registryKey().getValue().compareTo(right.registryKey().getValue());
    }
}
