package derekahedron.invexp.sack;

import derekahedron.invexp.registry.DecentralizedReference;
import derekahedron.invexp.util.ContainerItemContentsChecker;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Checker for contents of a sack.
 */
public interface SackContentsChecker extends ContainerItemContentsChecker {

    /**
     * Gets the sack stack holding the contents.
     *
     * @return  sack stack that holds the contents
     */
    @NotNull ItemStack getSackStack();

    /**
     * Gets all sack types that this sack can hold.
     *
     * @return  List of all sack types in the contents
     */
    @NotNull List<RegistryEntry<SackType>> getSackTypes();

    /**
     * Gets the total weight of all items in the sack.
     *
     * @return  total sack weight that the contents hold
     */
    int getTotalWeight();

    /**
     * Checks that the item can be added by type as well as if it can be nested.
     *
     * @param stack     stack to try to insert
     * @return          if the stack can be added based on type and item
     */
    @Override
    default boolean canTryInsert(@NotNull ItemStack stack) {
        if (!stack.getItem().canBeNested()) {
            return false;
        }
        RegistryEntry<SackType> sackType = SacksHelper.getSackType(stack);
        if (sackType == null) {
            return false;
        }
        return canAddType() || isInTypes(sackType);
    }

    /**
     * Checks the stacks weight, type, and available stacks to see how much of
     * the given stack can be added.
     *
     * @param stack     stack to test
     * @return          how much of the stack can be added
     */
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
        int itemWeight = SacksHelper.getSackWeight(stack);
        if (itemWeight > 0) {
            return Math.min(stack.getCount(), Math.max(getMaxSackWeight() - getTotalWeight(), 0) / itemWeight);
        }
        else {
            return stack.getCount();
        }
    }

    /**
     * Checks if the given sack type exists in the current types of the sack.
     *
     * @param sackType  sack type to test for
     * @return          true if the given type exists in the types; false otherwise
     */
    default boolean isInTypes(@Nullable RegistryEntry<SackType> sackType) {
        if (sackType == null) {
            return false;
        }
        for (RegistryEntry<SackType> nestedType : getSackTypes()) {
            if (DecentralizedReference.referencesEqual(sackType, nestedType)) {
                return true;
            }
        }
        return false;
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
    default int getMaxSackWeight() {
        return SacksHelper.getMaxSackWeight(getSackStack());
    }
}
