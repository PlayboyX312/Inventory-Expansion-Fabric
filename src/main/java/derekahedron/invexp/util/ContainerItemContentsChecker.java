package derekahedron.invexp.util;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Interface that defines functions needed for checking contents and if an item can be
 * inserted into a container item
 */
public interface ContainerItemContentsChecker {

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
     * If an item can potentially be inserted into the container based on the item itself.
     * Generally, tests if the type of item matches.
     *
     * @param stack     stack to try to insert
     * @return          true if the item can try to be inserted; false otherwise
     */
    boolean canTryInsert(@NotNull ItemStack stack);

    /**
     * Check how many of the item stack can actually be added.
     *
     * @param stack     stack to test
     * @return          number of items that can be inserted
     */
    int getMaxAllowed(@NotNull ItemStack stack);

    /**
     * Check if there is room to add a new stack in the container contents.
     *
     * @return  true if there is space for a new stack; false otherwise
     */
    boolean canAddStack();

    /**
     * Check if the container is empty.
     *
     * @return  true if the container is empty; false otherwise
     */
    default boolean isEmpty() {
        return getStacks().isEmpty();
    }

    /**
     * Gets the selected item stack from the contents.
     *
     * @return  selected ItemStack; EMPTY if there is none
     */
    default @NotNull ItemStack getSelectedStack() {
        if (isEmpty()) {
            return ItemStack.EMPTY;
        }
        return getStacks().get(getSelectedIndex());
    }

    /**
     * Turns index into a valid index that points to a stack.
     *
     * @param index     index to transform
     * @return          valid index; -1 if empty
     */
    default int clampIndex(int index) {
        if (isEmpty()) {
            return -1;
        }
        else {
            return Math.clamp(index, 0, getStacks().size() - 1);
        }
    }

    /**
     * Search the contents for a stack that matches the given stack and returns the index.
     *
     * @param stack             stack to search for
     * @param startingIndex     index to start the search from
     * @return                  index of a stack that matches; -1 if there is none
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
}
