package derekahedron.invexp.quiver;

import derekahedron.invexp.util.ContainerItemContentsReader;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import org.apache.commons.lang3.math.Fraction;
import org.jetbrains.annotations.NotNull;

/**
 * Checker for contents of a quiver.
 */
public interface QuiverContentsReader extends ContainerItemContentsReader {

    /**
     * Gets the quiver <code>ItemStack</code> holding this contents.
     *
     * @return The <code>ItemStack</code> that holds this contents.
     *         This is intended to be a quiver.
     */
    @NotNull ItemStack getQuiverStack();

    /**
     * Gets the total occupancy of all items in the quiver.
     *
     * @return the total quiver occupancy that this quiver holds
     */
    @NotNull Fraction getTotalOccupancy();

    /**
     * Checks if an item is an arrow and can be inserted into the quiver.
     *
     * @return <code>true</code> if the <code>ItemStack</code> is an arrow;
     *         <code>false</code> otherwise
     */
    @Override
    default boolean canTryInsert(@NotNull ItemStack stack) {
        return stack.isIn(ItemTags.ARROWS) && stack.getItem().canBeNested();
    }

    @Override
    default int getMaxAllowed(@NotNull ItemStack stack) {
        if (!canTryInsert(stack)) {
            return 0;
        }
        int maxAllowedByOccupancy = getMaxAllowedByOccupancy(stack);

        if (maxAllowedByOccupancy == 0) {
            return 0;
        } else if (canAddStack()) {
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

    @Override
    default boolean isFull() {
        return getTotalOccupancy().compareTo(getMaxQuiverOccupancy()) >= 0 || getStacks().size() >= getMaxQuiverStacks();
    }

    @Override
    default @NotNull Fraction getFillFraction() {
        if (isFull()) {
            return Fraction.ONE;
        } else {
            return getTotalOccupancy().divideBy(getMaxQuiverOccupancy());
        }
    }
}
