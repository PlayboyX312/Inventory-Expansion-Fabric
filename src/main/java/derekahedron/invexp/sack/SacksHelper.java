package derekahedron.invexp.sack;

import derekahedron.invexp.component.InvExpDataComponentTypes;
import derekahedron.invexp.component.types.SackInsertableComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.math.Fraction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Contains helper functions and default values for data components related to sacks.
 */
public class SacksHelper {
    public static final Fraction DEFAULT_SACK_WEIGHT = Fraction.ONE;
    public static final int DEFAULT_MAX_SACK_TYPES = 0;
    public static final Fraction DEFAULT_MAX_SACK_WEIGHT = Fraction.ZERO;
    public static final int DEFAULT_MAX_SACK_STACKS = 0;

    /**
     * Gets the maximum stack types an item can hold.
     *
     * @param stack     stack to test
     * @return          maximum sack types this item can hold
     */
    public static int getMaxSackTypes(@NotNull ItemStack stack) {
        return stack.getOrDefault(InvExpDataComponentTypes.MAX_SACK_TYPES, DEFAULT_MAX_SACK_TYPES);
    }

    /**
     * Return the maximum sack weight an item can hold.
     *
     * @param stack     stack to test
     * @return          maximum sack weight this item can hold
     */
    public static Fraction getMaxSackWeight(@NotNull ItemStack stack) {
        return stack.getOrDefault(InvExpDataComponentTypes.MAX_SACK_WEIGHT, DEFAULT_MAX_SACK_WEIGHT);
    }

    /**
     * Return the maximum number of stacks this item can hold as a sack.
     *
     * @param stack     stack to test
     * @return          maximum stacks this item can hold as a sack
     */
    public static int getMaxSackStacks(@NotNull ItemStack stack) {
        return stack.getOrDefault(InvExpDataComponentTypes.MAX_SACK_STACKS, DEFAULT_MAX_SACK_STACKS);
    }

    /**
     * Gets the registry entry of the sack type related to this item.
     *
     * @param stack     stack to test
     * @return          registry entry of the sack type; null if there is none
     */
    public static @Nullable RegistryKey<SackType> getSackType(@NotNull ItemStack stack) {
        // Use the data in the sack insertable component first
        SackInsertableComponent component = stack.get(InvExpDataComponentTypes.SACK_INSERTABLE);
        if (component != null) {
            return component.sackType().flatMap(RegistryEntry::getKey).orElse(null);
        }

        // If there is no insertable, try the sack type component
        // Use this when possible
        RegistryEntry<SackType> sackType = stack.get(InvExpDataComponentTypes.SACK_TYPE);
        if (sackType != null) {
            return sackType.getKey().orElse(null);
        }

        // If nothing exists, try to grab from the insertable manager.
        if (SackDefaultManager.getInstance() != null) {
            return SackDefaultManager.getInstance().getType(stack);
        }

        // Fail if manager is not created before running this
        throw new RuntimeException("Sack Loading Error");
    }

    /**
     * Gets the sack weight related to this item.
     *
     * @param stack     stack to test
     * @return          sack weight of the item
     */
    public static Fraction getSackWeight(@NotNull ItemStack stack) {
        // Use the data in the sack insertable component first
        SackInsertableComponent component = stack.get(InvExpDataComponentTypes.SACK_INSERTABLE);
        if (component != null) {
            return component.sackWeight().orElse(DEFAULT_SACK_WEIGHT)
                    .divideBy(Fraction.getFraction(stack.getMaxCount()));
        }

        // If there is no insertable, try the sack weight component
        // Use this when possible
        Fraction sackWeight = stack.get(InvExpDataComponentTypes.SACK_WEIGHT);
        if (sackWeight != null) {
            return sackWeight
                    .divideBy(Fraction.getFraction(stack.getMaxCount()));
        }

        // If nothing exists, try to grab from the insertable manager.
        if (SackDefaultManager.getInstance() != null) {
            return SackDefaultManager.getInstance().getWeight(stack)
                    .divideBy(Fraction.getFraction(stack.getMaxCount()));
        }

        // Fail if manager is not created before running this
        throw new RuntimeException("Sack Loading Error");
    }

    /**
     * Gets sack weight of entire stack.
     *
     * @param stack     stack to test
     * @return          weight this entire stack takes up
     */
    public static Fraction getSackWeightOfStack(@NotNull ItemStack stack) {
        return getSackWeight(stack).multiplyBy(Fraction.getFraction(stack.getCount()));
    }

    /**
     * Gets the Identifier of the sack type related to this item.
     *
     * @param stack     stack to test
     * @return          Identifier of the sack type related to this item; null if there is none
     */
    public static @Nullable Identifier getSackTypeIdentifier(@NotNull ItemStack stack) {
        return getSackTypeIdentifier(getSackType(stack));
    }

    /**
     * Gets the Identifier of the given sack type.
     * Because sack types can be decentralized, we often use the identifier to check for equality.
     *
     * @param sackType  type to get identifier from
     * @return          Identifier of the given sack type; null if there is none
     */
    public static @Nullable Identifier getSackTypeIdentifier(@Nullable RegistryKey<SackType> sackType) {
        if (sackType == null) {
            return null;
        }
        return sackType.getValue();
    }
}
