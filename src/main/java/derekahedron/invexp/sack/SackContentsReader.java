package derekahedron.invexp.sack;

import derekahedron.invexp.util.ContainerItemContentsReader;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import org.apache.commons.lang3.math.Fraction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * Checker for contents of a sack.
 */
public interface SackContentsReader extends ContainerItemContentsReader {

    /**
     * Gets the sack <code>ItemStack</code> holding this contents.
     *
     * @return The <code>ItemStack</code> that holds this contents.
     *         This is intended to be a sack.
     */
    @NotNull ItemStack getSackStack();

    @NotNull List<RegistryEntry<SackType>> getSackTypes();

    /**
     * Gets the total sack weight of all items in the sack.
     *
     * @return the total sack weight that this sack holds
     */
    Fraction getTotalWeight();

    /**
     * Checks if an item can potentially be inserted into the sack based on its sack type.
     *
     * @return <code>true</code> if the <code>ItemStack</code> matches the sack type;
     *         <code>false</code> otherwise
     */
    @Override
    default boolean canTryInsert(@NotNull ItemStack stack) {
        if (!stack.getItem().canBeNested()) {
            return false;
        }
        RegistryKey<SackType> sackType = SacksHelper.getSackType(stack);
        if (sackType == null) {
            return false;
        }
        return canAddType() || isInTypes(sackType);
    }

    @Override
    default int getMaxAllowed(@NotNull ItemStack stack) {
        if (!canTryInsert(stack)) {
            return 0;
        }

        int maxAllowedByWeight = getMaxAllowedByWeight(stack);
        if (maxAllowedByWeight == 0) {
            return 0;
        }
        else if (canAddStack()) {
            // If we can add a stack, we can always add as much as is allowed by weight
            return maxAllowedByWeight;
        }

        // Iterate through stacks and see how many items can be added by merging
        int maxAllowed = 0;
        for (ItemStack nestedStack : getStacks()) {
            if (ItemStack.areItemsAndComponentsEqual(stack, nestedStack)) {
                maxAllowed += nestedStack.getMaxCount() - nestedStack.getCount();
                if (maxAllowed >= maxAllowedByWeight) {
                    return maxAllowedByWeight;
                }
            }
        }
        return maxAllowed;
    }

    /**
     * Check how many items of the given stack can be added, only considering available
     * weight.
     *
     * @param stack     stack to test
     * @return          how many items can be added
     */
    default int getMaxAllowedByWeight(@NotNull ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        Fraction weight = SacksHelper.getSackWeight(stack);
        if (weight.compareTo(Fraction.ZERO) > 0) {
            Fraction openWeight = getMaxSackWeight().subtract(getTotalWeight());
            return Math.max(openWeight.divideBy(weight).intValue(), 0);
        } else {
            return stack.getCount();
        }
    }

    /**
     * Checks if the given sack type exists in the current types of the sack.
     *
     * @param sackType  sack type to test for
     * @return          true if the given type exists in the types; false otherwise
     */
    default boolean isInTypes(@Nullable RegistryKey<SackType> sackType) {
        if (sackType == null) {
            return false;
        }
        return getSackTypes()
                .stream()
                .map(RegistryEntry::getKey)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .anyMatch(sackType::equals);
    }

    /**
     * Check if there are fewer types than max types.
     *
     * @return  true if there is space for a new sack type; false otherwise
     */
    default boolean canAddType() {
        return getSackTypes().size() < getMaxSackTypes();
    }

    /**
     * Check if there are fewer stacks than max stacks.
     *
     * @return  true if there is space for a new stack; false otherwise
     */
    @Override
    default boolean canAddStack() {
        return getStacks().size() < getMaxSackStacks();
    }

    /**
     * Gets max sack stacks of the holding sack stack.
     *
     * @return  max stacks the sack can hold
     */
    default int getMaxSackStacks() {
        return SacksHelper.getMaxSackStacks(getSackStack());
    }

    /**
     * Gets max sack types of the holding sack stack.
     *
     * @return  max sack types the sack can hold
     */
    default int getMaxSackTypes() {
        return SacksHelper.getMaxSackTypes(getSackStack());
    }

    /**
     * Gets max sack weight of the holding sack stack.
     *
     * @return  max sack weight the sack can hold
     */
    default Fraction getMaxSackWeight() {
        return SacksHelper.getMaxSackWeight(getSackStack());
    }

    default boolean isFull() {
        return (getTotalWeight().compareTo(getMaxSackWeight()) >= 0) || (getStacks().size() >= getMaxSackStacks());
    }

    default @NotNull Fraction getFillFraction() {
        if (isFull()) {
            return Fraction.ONE;
        } else {
            return getTotalWeight().divideBy(getMaxSackWeight());
        }
    }
}
