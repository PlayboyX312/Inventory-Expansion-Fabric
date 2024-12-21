package derekahedron.invexp.quiver;

import derekahedron.invexp.util.ContainerItemContentsChecker;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import org.apache.commons.lang3.math.Fraction;
import org.jetbrains.annotations.NotNull;

/**
 * Checker for contents of a quiver.
 */
public interface QuiverContentsChecker extends ContainerItemContentsChecker {

    /**
     * Gets the quiver stack holding the contents.
     *
     * @return  quiver stack that holds the contents
     */
    @NotNull ItemStack getQuiverStack();

    /**
     * Gets the total occupancy of all items in the quiver.
     *
     * @return  total quiver occupancy that the contents hold
     */
    @NotNull Fraction getTotalOccupancy();

    /**
     * Checks that the item can be added by tag as well as if it can be nested.
     *
     * @param stack     stack to try to insert
     * @return          if the stack can be added based on item tags
     */
    @Override
    default boolean canTryInsert(@NotNull ItemStack stack) {
        return stack.isIn(ItemTags.ARROWS) && stack.getItem().canBeNested();
    }

    /**
     * Checks the stacks occupancy and available stacks to see how much of
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
        int maxAllowedByOccupancy = getMaxAllowedByOccupancy(stack);
        if (maxAllowedByOccupancy == 0) {
            return 0;
        }
        else if (canAddStack()) {
            return maxAllowedByOccupancy;
        }
        int maxAllowed = 0;
        for (ItemStack nestedStack : getStacks()) {
            if (ItemStack.areItemsAndComponentsEqual(stack, nestedStack)) {
                maxAllowed += nestedStack.getMaxCount() - nestedStack.getCount();
                if (maxAllowed >= maxAllowedByOccupancy) {
                    return maxAllowedByOccupancy;
                }
            }
        }
        return maxAllowed;
    }

    /**
     * Check how many items of the given stack can be added, only considering available
     * occupancy.
     *
     * @param stack     stack to test
     * @return          how many items can be added
     */
    default int getMaxAllowedByOccupancy(@NotNull ItemStack stack) {
        Fraction openOccupancy = getMaxQuiverOccupancy().subtract(getTotalOccupancy());
        return Math.max(openOccupancy.divideBy(QuiverHelper.getOccupancy(stack)).intValue(), 0);
    }

    /**
     * Check if there are fewer stacks than max stacks.
     *
     * @return  true if there is space for a new stack; false otherwise
     */
    @Override
    default boolean canAddStack() {
        return getStacks().size() < getMaxQuiverStacks();
    }

    /**
     * Gets max quiver stacks of the holding quiver stack.
     *
     * @return  max stacks the quiver can hold
     */
    default int getMaxQuiverStacks() {
        return QuiverHelper.getMaxQuiverStacks(getQuiverStack());
    }

    /**
     * Gets max occupancy of the holding quiver stack.
     *
     * @return  max occupancy the quiver can hold
     */
    default Fraction getMaxQuiverOccupancy() {
        return QuiverHelper.getMaxQuiverOccupancy(getQuiverStack());
    }
}
