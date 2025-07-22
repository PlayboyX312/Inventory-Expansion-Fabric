package derekahedron.invexp.util;

import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.math.Fraction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Interface that defines functions needed for checking contents and if an item can be
 * inserted into a container item
 */
public interface ContainerItemContentsReader {

    /**
     * Gets the item stack list inside the container item
     *
     * @return  List of stack contents
     */
    @NotNull List<ItemStack> getStacks();

    /**
     * Gets the selected index of the container item
     *
     * @return  selected index; -1 if there is none
     */
    int getSelectedIndex();

    /**
     * Checks if an item can potentially be inserted into the container based on the item itself.
     * General check based on the type of the item and not fullness of the contents.
     *
     * @param stack the <code>ItemStack</code> to test
     * @return <code>true</code> if the <code>ItemStack</code> matches contents criteria;
     *         <code>false</code> otherwise
     */
    boolean canTryInsert(@NotNull ItemStack stack);

    /**
     * Gets the maximum number of items from the given <code>ItemStack</code> that
     * can be added to these contents.
     *
     * @param stack the <code>ItemStack</code> to test
     * @return the maximum number of items that can be inserted
     */
    int getMaxAllowed(@NotNull ItemStack stack);

    /**
     * Checks if there is room to add a new stack in the container contents.
     *
     * @return <code>true</code> if there is space for a new stack; <code>false</code> otherwise
     */
    boolean canAddStack();

    /**
     * Checks if the container has contents.
     *
     * @return <code>true</code> if the container is empty; <code>false</code> otherwise
     */
    default boolean isEmpty() {
        return getStacks().isEmpty();
    }

    /**
     * Gets the selected item stack from the contents.
     *
     * @return the selected <code>ItemStack</code>; <code>EMPTY</code> if there is none
     */
    default @NotNull ItemStack getSelectedStack() {
        if (isEmpty()) {
            return ItemStack.EMPTY;
        }
        return getStacks().get(getSelectedIndex());
    }

    /**
     * Clamps a given index into a valid index that points to a stack.
     *
     * @param index a given index to transform
     * @return a valid index; <code>-1</code> if the contents are empty
     */
    default int clampIndex(int index) {
        if (isEmpty()) {
            return -1;
        } else {
            return Math.clamp(index, 0, getStacks().size() - 1);
        }
    }

    /**
     * Searches the contents for a stack that matches a given stack and returns its index.
     *
     * @param stack An <code>ItemStack</code> to search for a stack that matches it
     * @param startingIndex the index to start the search from
     * @return the index of a stack that matches; <code>-1</code> if there is none
     */
    default int indexOf(@NotNull ItemStack stack, int startingIndex) {
        if (isEmpty()) {
            return -1;
        }
        startingIndex = clampIndex(startingIndex);
        // First check stacks after starting index
        for (int i = startingIndex; i < getStacks().size(); i++) {
            if (ItemStack.areItemsAndComponentsEqual(stack, getStacks().get(i))) {
                return i;
            }
        }
        // Next check stacks before starting index
        for (int i = startingIndex - 1; i >= 0; i--) {
            if (ItemStack.areItemsAndComponentsEqual(stack, getStacks().get(i))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Gets a <code>Fraction</code> representing how full the contents are.
     *
     * @return a <code>Fraction</code> representing fullness. <code>Fraction.ONE</code>
     *         means the contents are full.
     */
    @NotNull Fraction getFillFraction();

    /**
     * Check if the contents have reached max stacks or the total occupancy has reached max occupancy.
     *
     * @return <code>true</code> if the contents should display as full; <code>false</code> otherwise
     */
    boolean isFull();

    /**
     * Copies the selected stack so it can be used.
     *
     * @return  a copy of the selected stack
     */
    default @NotNull ItemStack copySelectedStack() {
        return copySelectedStack(null);
    }

    /**
     * Copies the selected stack. If the current stack matches the existing stack,
     * instead of copying, sets the count of the passed in stack.
     * This is so the ItemStack object can be the same instance, which is useful in cases like
     * checking for the active item stack.
     *
     * @param currentSelectedStack  selected stack to transform
     * @return                      copy of the selected stack to modify
     */
    default @NotNull ItemStack copySelectedStack(@Nullable ItemStack currentSelectedStack) {
        if (isEmpty()) {
            return ItemStack.EMPTY;
        }
        else {
            ItemStack selectedStack = getSelectedStack();
            if (currentSelectedStack != null && ItemStack.areItemsAndComponentsEqual(selectedStack, currentSelectedStack)) {
                currentSelectedStack.setCount(selectedStack.getCount());
                return currentSelectedStack;
            }
            else {
                return selectedStack.copy();
            }
        }
    }
}
