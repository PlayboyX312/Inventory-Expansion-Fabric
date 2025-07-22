package derekahedron.invexp.util;

import derekahedron.invexp.InventoryExpansion;
import derekahedron.invexp.quiver.QuiverContents;
import derekahedron.invexp.sack.SackContents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Defines an abstract content wrapper for viewing and modifying contents of a container item.
 * Extended by SackContents and QuiverContents. Generally, the idea is that the Contents object
 * is a NOOP to create and can easily check contents without modifying.
 * If a modification can happen, a builder is created (which copies the items when created and applied)
 * where the modifications occur, so we are only creating the builder when necessary.
 */
public abstract class ContainerItemContents implements ContainerItemContentsReader {

    /**
     * Create a Contents object from the given stack based on the stack that is passed in.
     * If one cannot be created, return null. If more content types are added, this function
     * will have to be modified. This is to be used in places where any contents can be used.
     *
     * @param stack     stack to create contents from if possible
     * @return          contents created from the stack; null if none are created
     */
    public static @Nullable ContainerItemContentsReader of(@Nullable ItemStack stack) {
        ContainerItemContentsReader contents;
        if ((contents = SackContents.of(stack)) != null) {
            return contents;
        } else if ((contents = QuiverContents.of(stack)) != null) {
            return contents;
        }
        return null;
    }

    public static @Nullable ContainerItemContents of(@Nullable ItemStack stack, @NotNull World world) {
        ContainerItemContents contents;
        if ((contents = SackContents.of(stack, world)) != null) {
            return contents;
        } else if ((contents = QuiverContents.of(stack)) != null) {
            return contents;
        }
        return null;
    }

    /**
     * Adds the given stack to the contents
     *
     * @param stack     stack to add
     * @return          number of items added
     */
    public int add(@NotNull ItemStack stack) {
        if (getMaxAllowed(stack) > 0) {
            Builder builder = getBuilder();
            int added = builder.add(stack, 0);
            if (added > 0) {
                builder.apply();
            }
            return added;
        }
        return 0;
    }

    /**
     * Adds stack from the given slot by the given player
     *
     * @param slot      slot to add item from
     * @param player    player adding the item
     * @return          number of items added
     */
    public int add(@NotNull Slot slot, @NotNull PlayerEntity player) {
        return add(Stream.of(slot), player);
    }

    /**
     * Adds stream of slots to contents. Useful so we don't have to create and apply
     * a new builder for each addition.
     *
     * @param slots     slots to add item from
     * @param player    player adding the items
     * @return          total number of items added
     */
    public int add(@NotNull Stream<Slot> slots, @NotNull PlayerEntity player) {
        // Do not create builder until needed
        Builder builder = null;
        // Default to using this as the contents checker
        ContainerItemContentsReader checker = this;
        int added = 0;

        for (Slot slot : slots.toList()) {
            ItemStack stack = slot.getStack();
            int maxAllowed = checker.getMaxAllowed(stack);
            if (maxAllowed > 0) {
                stack = slot.takeStackRange(stack.getCount(), maxAllowed, player);
                if (!stack.isEmpty()) {
                    if (builder == null) {
                        // When we need to add, create a new builder and
                        // set that as the checker
                        builder = getBuilder();
                        checker = builder;
                    }
                    added += builder.add(stack);
                }
            }
        }

        // Only apply if actually added
        if (added > 0) {
            builder.apply();
        }
        return added;
    }

    /**
     * Removes a given stack from the contents
     *
     * @param stack     stack to remove
     * @return          number of items removed
     */
    public int remove(@NotNull ItemStack stack) {
        if (isEmpty() || stack.isEmpty()) {
            return 0;
        }
        Builder builder = getBuilder();
        int removed = builder.remove(stack);
        if (removed == stack.getCount()) {
            builder.apply();
            return removed;
        }
        return 0;
    }

    /**
     * Pops single item from the selected stack
     *
     * @return  popped selected stack containing one item; EMPTY if there is none
     */
    public @NotNull ItemStack popSelectedItem() {
        if (isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack selectedStack = getSelectedStack();
        if (selectedStack.getCount() == 1) {
            return popSelectedStack();
        }
        Builder builder = getBuilder();
        selectedStack = selectedStack.copyWithCount(1);
        if (builder.remove(selectedStack) > 0) {
            builder.apply();
            return selectedStack;
        }
        else {
            // Should never happen
            InventoryExpansion.LOGGER.warn("ItemStack {} not removed", selectedStack);
            return ItemStack.EMPTY;
        }
    }

    /**
     * Pops selected stack from contents.
     *
     * @return  popped selected stack; EMPTY if there is none
     */
    public @NotNull ItemStack popSelectedStack() {
        if (isEmpty()) {
            return ItemStack.EMPTY;
        }
        Builder builder = getBuilder();
        ItemStack stack = builder.popSelectedStack();
        if (!stack.isEmpty()) {
            builder.apply();
        }
        return stack;
    }

    /**
     * Pop selected stack into a slot
     *
     * @param slot  slot to pop stack into
     * @return      true if the stack was popped; false otherwise
     */
    public boolean popSelectedStack(@NotNull Slot slot) {
        if (isEmpty()) {
            return false;
        }
        ItemStack selectedStack = getSelectedStack();
        int toRemove = selectedStack.getCount() - slot.insertStack(selectedStack.copy()).getCount();
        if (toRemove == 0) {
            return false;
        }
        else if (toRemove == selectedStack.getCount()) {
            popSelectedStack();
            return true;
        }
        Builder builder = getBuilder();
        if (builder.remove(selectedStack, toRemove) > 0) {
            builder.apply();
            return true;
        }
        else {
            // Should never happen
            InventoryExpansion.LOGGER.warn(
                    "ItemStack {} not removed. Potential duplicate in slot {}",
                    selectedStack, slot
            );
            return false;
        }
    }

    /**
     * Remove and return all stacks.
     *
     * @return  list of copied stacks
     */
    public @NotNull List<ItemStack> popAllStacks() {
        if (isEmpty()) {
            return List.of();
        }
        Builder builder = getBuilder();
        List<ItemStack> stacks = builder.popAllStacks();
        builder.apply();
        return stacks;
    }

    /**
     * Sets selected index of contents
     *
     * @param selectedIndex     new selected index
     */
    public void setSelectedIndex(int selectedIndex) {
        selectedIndex = clampIndex(selectedIndex);
        if (selectedIndex != getSelectedIndex()) {
            Builder builder = getBuilder();
            builder.setSelectedIndex(selectedIndex);
            builder.apply();
        }
    }

    /**
     * Sets the selected stack of the contents to the given stack. If the
     * stack cannot be added, the leftovers are sent through the
     *
     * @param selectedStack             new selected stack
     * @param leftoverStackConsumer     handle leftover stack
     */
    public void updateSelectedStack(
            @NotNull ItemStack selectedStack, @NotNull Consumer<ItemStack> leftoverStackConsumer
    ) {
        ItemStack leftoverStack = ItemStack.EMPTY;

        if (isEmpty()) {
            if (selectedStack.isEmpty()) {
                // Do nothing if both are empty
                return;
            }

            Builder builder = getBuilder();
            leftoverStack = selectedStack.copy();
            if (builder.add(leftoverStack, 0) > 0) {
                builder.apply();
            }
        }

        else {
            ItemStack oldStack = getSelectedStack();

            if (selectedStack.isEmpty()) {
                // If new stack is empty, remove old stack
                Builder builder = getBuilder();
                if (builder.remove(oldStack) != oldStack.getCount()) {
                    InventoryExpansion.LOGGER.warn("ItemStack {} not fully removed.", oldStack);
                }
                builder.apply();
            }
            else if (ItemStack.areItemsAndComponentsEqual(oldStack, selectedStack)) {
                // If stacks match, add/remove difference
                int countDiff = selectedStack.getCount() - oldStack.getCount();
                if (countDiff > 0) {
                    // Add stacks for positive difference
                    Builder builder = getBuilder();
                    leftoverStack = selectedStack.copyWithCount(countDiff);
                    if (builder.add(leftoverStack, 0) > 0) {
                        builder.apply();
                    }
                }
                else if (countDiff < 0) {
                    // Remove stacks for negative difference
                    Builder builder = getBuilder();
                    if (builder.remove(oldStack, -countDiff) != -countDiff) {
                        InventoryExpansion.LOGGER.warn(
                                "Count difference ({}) for {} not fully removed",
                                -countDiff, oldStack
                        );
                    }
                    builder.apply();
                }
                else {
                    // No change
                    return;
                }
            }
            else {
                // Stack is fully replaced
                Builder builder = getBuilder();
                leftoverStack = selectedStack.copy();
                if (builder.replaceSelectedStack(leftoverStack)) {
                    builder.apply();
                }
            }
        }

        if (!leftoverStack.isEmpty()) {
            leftoverStackConsumer.accept(leftoverStack);
        }
        copySelectedStack(selectedStack);
    }

    /**
     * Create a new builder for modifying contents.
     *
     * @return a builder that can modify this container contents
     */
    public abstract @NotNull Builder getBuilder();

    /**
     * Builder for modifying contents. Changes made do not take effect until the builder is applied.
     */
    public abstract static class Builder implements ContainerItemContentsReader {
        /**
         * Applies changes made to the original contents and container stack.
         */
        public abstract void apply();

        /**
         * Adds an item stack to the contents. First tries adding to existing stacks,
         * then inserts remainder at given index.
         *
         * @param stack     stack to add
         * @param insertAt  where to insert the new stack
         * @return          number of items added
         */
        public abstract int add(@NotNull ItemStack stack, int insertAt);

        /**
         * Remove the given stack from the contents
         *
         * @param stack     stack to remove
         * @param toRemove  how many of the given stack to remove
         * @return          how many items were removed
         */
        public abstract int remove(@NotNull ItemStack stack, int toRemove);

        /**
         * Pops the selected stack from the contents.
         *
         * @return ItemStack popped from the contents; EMPTY if none
         */
        public abstract @NotNull ItemStack popSelectedStack();

        /**
         * Remove all stacks from contents.
         *
         * @return  List of copies of previous contents
         */
        public abstract @NotNull List<ItemStack> popAllStacks();

        /**
         * Sets the selected index
         *
         * @param selectedIndex     new selected index
         */
        public abstract void setSelectedIndex(int selectedIndex);

        /**
         * Adds given stack, inserting at the beginning.
         *
         * @param stack     stack to add
         * @return          number of items added
         */
        public int add(@NotNull ItemStack stack) {
            return add(stack, 0);
        }

        /**
         * Tries to remove entire stack from contents.
         *
         * @param stack     stack to remove
         * @return          number of items removed
         */
        public int remove(@NotNull ItemStack stack) {
            return remove(stack, stack.getCount());
        }

        /**
         * Try to replace the selected stack with the given stack. Will
         * remove the selected stack then try to add the given stack in its place.
         *
         * @param stack     stack to replace selected stack
         * @return          true if the operation completes; false otherwise
         */
        public boolean replaceSelectedStack(@NotNull ItemStack stack) {
            return replaceSelectedStack(stack, getSelectedStack().getCount());
        }

        /**
         * Try to replace a given amount of the selected stack with the given stack. Will
         * remove the selected stack then try to add the given stack in its place.
         *
         * @param stack     stack to replace selected stack
         * @param count     amount of the selected stack to replace
         * @return          true if the operation completes; false otherwise
         */
        public boolean replaceSelectedStack(@NotNull ItemStack stack, int count) {
            if (isEmpty()) {
                return false;
            }

            ItemStack selectedStack = getSelectedStack();
            int insertAt;
            if (count > selectedStack.getCount()) {
                // Fail if asking for more than possible
                InventoryExpansion.LOGGER.warn("Cannot remove {} from selected stack {}", count, selectedStack);
                return false;
            }
            else if (count < selectedStack.getCount()) {
                // If trying to remove less than selected stack, remove from start
                if (remove(selectedStack, count) != count) {
                    InventoryExpansion.LOGGER.warn("Selected Stack {} not fully replaced!", selectedStack);
                }
                // Insert above selected index
                insertAt = getSelectedIndex() + 1;
            }
            else {
                // Insert at old selected index
                insertAt = getSelectedIndex();
                // Remove stack
                popSelectedStack();
            }

            ItemStack firstPick;
            ItemStack backupPick;
            if (ItemStack.areItemsEqual(stack, selectedStack)) {
                // If items are equal (like tool losing durability) prioritize selecting new stack
                firstPick = stack.copy();
                backupPick = selectedStack;
            }
            else {
                // If items are not equal (item transforms) prioritize selecting old stack
                firstPick = selectedStack;
                backupPick = stack.copy();
            }

            add(stack, insertAt);

            // Set new selected index
            setSelectedIndex(nextSelectedIndex(firstPick, backupPick, insertAt));
            return true;
        }

        /**
         * Find the next selected index of a matching stack. If none are found,
         * use clamped starting index.
         *
         * @param stack             stack to match with
         * @param startingIndex     index to start search from
         * @return                  new index
         */
        public int nextSelectedIndex(@NotNull ItemStack stack, int startingIndex) {
            return nextSelectedIndex(stack, null, startingIndex);
        }

        /**
         * Find the next selected index matching the given stack. If none are found,
         * default to matching with the backup stack if that is given.
         * Otherwise, clamp the given starting index.
         *
         * @param stack             stack to match with
         * @param backupStack       stack to match with if first stack is not found
         * @param startingIndex     index to start search from
         * @return                  new index
         */
        public int nextSelectedIndex(@NotNull ItemStack stack, @Nullable ItemStack backupStack, int startingIndex) {
            if (isEmpty()) {
                return -1;
            }
            int newIndex = indexOf(stack, startingIndex);
            if (newIndex != -1) {
                return newIndex;
            }
            else if (backupStack != null && (newIndex = indexOf(backupStack, startingIndex)) != -1) {
                return newIndex;
            }
            else {
                return clampIndex(startingIndex);
            }
        }
    }
}
