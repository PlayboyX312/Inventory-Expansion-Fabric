package derekahedron.invexp.quiver;

import com.google.common.collect.Lists;
import derekahedron.invexp.component.InvExpDataComponentTypes;
import derekahedron.invexp.component.types.QuiverContentsComponent;
import derekahedron.invexp.item.QuiveredItemStack;
import derekahedron.invexp.util.ContainerItemContents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.math.Fraction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Manages contents of a quiver. Extends ContainerItemContents for improved modifying of
 * contents.
 */
public class QuiverContents extends ContainerItemContents implements QuiverContentsChecker {
    public final ItemStack quiverStack;
    private QuiverContentsComponent component;

    /**
     * Creates a new QuiverContents object. Private as of() should be used to create
     * QuiverContents, so we can ensure they are valid.
     *
     * @param quiverStack   stack containing the contents
     * @param component     contents component
     */
    private QuiverContents(@NotNull ItemStack quiverStack, @NotNull QuiverContentsComponent component) {
        this.quiverStack = quiverStack;
        this.component = component;
    }

    /**
     * Create a new QuiverContents from the given stack. If the stack cannot have
     * contents, returns null
     *
     * @param quiverStack   stack to create contents from
     * @return              created QuiverContents; null if not valid
     */
    public static @Nullable QuiverContents of(@NotNull ItemStack quiverStack) {
        QuiverContentsComponent component = QuiverContentsComponent.getComponent(quiverStack);
        if (component == null) {
            return null;
        }
        return new QuiverContents(quiverStack, component);
    }

    /**
     * Creates a new QuiveredItemStack from the selected stack in the quiver.
     * The selected stack must match the predicate passed in.
     *
     * @param predicate     predicate to test the selected stack for
     * @return              new QuiveredItemStack from the selected stack; EMPTY if one doesn't exist
     */
    public @NotNull ItemStack getProjectileStack(@NotNull Predicate<ItemStack> predicate) {
        if (isEmpty()) {
            return ItemStack.EMPTY;
        }
        // Make sure the selected stack matches the predicate
        ItemStack selectedStack = getSelectedStack();
        if (!predicate.test(selectedStack)) {
            return ItemStack.EMPTY;
        }
        for (int i = component.stacks.size() - 1; i >= 0; i--) {
            ItemStack nestedStack = component.stacks.get(i);
            if (ItemStack.areItemsAndComponentsEqual(selectedStack, nestedStack)) {
                return new QuiveredItemStack(nestedStack, quiverStack);
            }
        }
        return new QuiveredItemStack(selectedStack, quiverStack);
    }

    /**
     * Checks the validity of the quiver contents. First checks the component validity, which
     * is calculated when the component is created and when the DataPacks are updated.
     * Then checks for occupancy and max stacks, which are both instant checks.
     *
     * @return  if the quiver contents are valid
     */
    public boolean isValid() {
        if (!component.isValid()) {
            return false;
        }
        return getTotalOccupancy().compareTo(getMaxQuiverOccupancy()) <= 0 && getStacks().size() <= getMaxQuiverStacks();
    }

    /**
     * Checks if the contents are valid. If they are not, create a new QuiverContents
     * and add each item one by one. Leftover stacks are given to the player after the validation.
     *
     * @param player    player holding the quiver
     */
    public void validate(@NotNull PlayerEntity player) {
        if (isValid()) {
            return;
        }

        ArrayList<ItemStack> removedStacks = new ArrayList<>(getStacks().size());
        QuiverContents newContents = new QuiverContents(quiverStack, QuiverContentsComponent.DEFAULT);
        Builder builder = newContents.getBuilder();
        for (int i = getStacks().size() - 1; i >= 0; i--) {
            ItemStack stack = getStacks().get(i).copy();
            builder.add(stack, 0);
            if (!stack.isEmpty()) {
                removedStacks.add(stack);
            }
        }

        builder.selectedIndex = builder.nextSelectedIndex(getSelectedStack(), getSelectedIndex());
        builder.apply();
        component = newContents.component;
        for (ItemStack stack : removedStacks) {
            player.giveOrDropStack(stack);
        }
    }

    /**
     * Gets the stack from the component.
     *
     * @return  List of stack contents
     */
    @Override
    public @NotNull List<ItemStack> getStacks() {
        return component.stacks;
    }

    /**
     * Gets the selected index from the component.
     *
     * @return  selected index; -1 if there is none
     */
    @Override
    public int getSelectedIndex() {
        return component.selectedIndex;
    }

    /**
     * Check if the contents have reached max stacks or the total occupancy has reached max occupancy.
     *
     * @return  true if the contents should display as full
     */
    @Override
    public boolean isFull() {
        return getTotalOccupancy().compareTo(getMaxQuiverOccupancy()) >= 0 || getStacks().size() >= getMaxQuiverStacks();
    }

    /**
     * Gets a fraction for displaying fullness of contents.
     *
     * @return  fraction representing fullness
     */
    @Override
    public @NotNull Fraction getFillFraction() {
        if (isFull()) {
            return Fraction.ONE;
        }
        else {
            return getTotalOccupancy().divideBy(getMaxQuiverOccupancy());
        }
    }

    /**
     * Gets the stored quiver stack.
     *
     * @return  quiver stack that holds the contents
     */
    @Override
    public @NotNull ItemStack getQuiverStack() {
        return quiverStack;
    }

    /**
     * Gets the total occupancy from the component.
     *
     * @return  total quiver occupancy that the contents hold
     */
    @Override
    public @NotNull Fraction getTotalOccupancy() {
        return component.getTotalOccupancy();
    }

    /**
     * Create a new builder for modifying quiver contents.
     *
     * @return  builder for quiver contents
     */
    @Override
    public @NotNull Builder getBuilder() {
        return new Builder();
    }

    /**
     * Builder for QuiverContents. Contains a copy of the quiver contents to be modified.
     */
    public class Builder extends ContainerItemContents.Builder implements QuiverContentsChecker {
        public final List<ItemStack> stacks;
        public int selectedIndex;
        public Fraction totalOccupancy;

        /**
         * Copies component data into modifiable versions.
         */
        public Builder() {
            this.stacks = new ArrayList<>(component.getStacks());
            this.selectedIndex = component.selectedIndex;
            this.totalOccupancy = component.getTotalOccupancy();
        }

        /**
         * Applies the copied values to the QuiverContents object this is attached to.
         */
        @Override
        public void apply() {
            component = new QuiverContentsComponent(
                    List.copyOf(stacks),
                    clampIndex(selectedIndex)
            );
            quiverStack.set(InvExpDataComponentTypes.QUIVER_CONTENTS, component);
        }

        /**
         * Tries to add the given stack to the quiver. First tries merging with existing items,
         * then tries inserting at the given index.
         *
         * @param stack     stack to add
         * @param insertAt  where to insert the new stack
         * @return          number of items added
         */
        @Override
        public int add(@NotNull ItemStack stack, int insertAt) {
            if (!canTryInsert(stack)) {
                return 0;
            }
            Fraction occupancy = QuiverHelper.getOccupancy(stack);
            int added = 0;
            int toAdd = Math.min(stack.getCount(), getMaxAllowedByOccupancy(stack));
            if (toAdd > 0) {
                for (int i = 0; i < stacks.size(); i++) {
                    ItemStack nestedStack = stacks.get(i);
                    if (ItemStack.areItemsAndComponentsEqual(stack, nestedStack)) {
                        int amount = Math.min(toAdd, nestedStack.getMaxCount() - nestedStack.getCount());
                        if (amount > 0) {
                            stacks.set(i, nestedStack.copyWithCount(nestedStack.getCount() + amount));
                            stack.decrement(amount);
                            toAdd -= amount;
                            added += amount;
                            totalOccupancy = totalOccupancy.add(occupancy.multiplyBy(Fraction.getFraction(amount)));
                        }
                        if (toAdd <= 0) {
                            // Return early if all the item was added.
                            return added;
                        }
                    }
                }

                // Add remaining to new stack
                if (canAddStack()) {
                    selectedIndex++;
                    added += toAdd;
                    ItemStack newStack = stack.split(toAdd);
                    totalOccupancy = totalOccupancy.add(QuiverHelper.getOccupancyOfStack(newStack));
                    stacks.addFirst(newStack);
                }
            }
            return added;
        }

        /**
         * Remove the given stack from the quiver contents, updating occupancy.
         *
         * @param stack     stack to remove
         * @param toRemove  how many of the given stack to remove
         * @return          how many items were removed
         */
        @Override
        public int remove(@NotNull ItemStack stack, int toRemove) {
            if (isEmpty() || stack.isEmpty()) {
                return 0;
            }
            int removed = 0;
            Fraction occupancy = QuiverHelper.getOccupancy(stack);

            // Track if the selected index was removed
            boolean removedSelected = false;
            for (int i = 0; i < stacks.size() && toRemove > 0; i++) {
                ItemStack nestedStack = stacks.get(i);
                if (ItemStack.areItemsAndComponentsEqual(stack, nestedStack)) {
                    if (toRemove >= nestedStack.getCount()) {
                        removed += nestedStack.getCount();
                        toRemove -= nestedStack.getCount();
                        totalOccupancy = totalOccupancy.subtract(occupancy.multiplyBy(Fraction.getFraction(nestedStack.getCount())));
                        stacks.remove(i);
                        // update selected index when removing
                        if (i < selectedIndex) {
                            selectedIndex--;
                        }
                        else if (i == selectedIndex) {
                            removedSelected = true;
                        }
                        i--;
                    }
                    else {
                        removed += toRemove;
                        totalOccupancy = totalOccupancy.subtract(occupancy.multiplyBy(Fraction.getFraction(toRemove)));
                        stacks.set(i, nestedStack.copyWithCount(nestedStack.getCount() - toRemove));
                        toRemove = 0;
                    }
                }
            }

            // Update selected index to nearest matching stack
            if (removedSelected) {
                selectedIndex = nextSelectedIndex(stack, selectedIndex);
            }

            return removed;
        }

        /**
         * Pops the selected stack from the contents, updating occupancy.
         *
         * @return ItemStack popped from the contents; EMPTY if none
         */
        @Override
        public @NotNull ItemStack popSelectedStack() {
            if (isEmpty()) {
                return ItemStack.EMPTY;
            }

            ItemStack selectedStack = stacks.remove(selectedIndex).copy();
            selectedIndex = nextSelectedIndex(selectedStack, selectedIndex);
            totalOccupancy = totalOccupancy.subtract(QuiverHelper.getOccupancyOfStack(selectedStack));
            return selectedStack;
        }

        /**
         * Remove all stacks from contents and clear occupancy.
         *
         * @return  List of copies of previous contents
         */
        @Override
        public @NotNull List<ItemStack> popAllStacks() {
            List<ItemStack> copies = Lists.transform(stacks, ItemStack::copy);
            stacks.clear();
            selectedIndex = -1;
            totalOccupancy = Fraction.ZERO;
            return copies;
        }

        /**
         * Gets the quiver stored in the related QuiverContents.
         *
         * @return  quiver stack that holds the contents
         */
        @Override
        public @NotNull ItemStack getQuiverStack() {
            return quiverStack;
        }

        /**
         * Gets the modified stacks.
         *
         * @return  List of stack contents
         */
        @Override
        public @NotNull List<ItemStack> getStacks() {
            return stacks;
        }

        /**
         * Gets the modified selected index.
         *
         * @return  selected index; -1 if there is none
         */
        @Override
        public int getSelectedIndex() {
            return selectedIndex;
        }

        /**
         * Modifies the selected index
         *
         * @param selectedIndex     new selected index
         */
        @Override
        public void setSelectedIndex(int selectedIndex) {
            this.selectedIndex = selectedIndex;
        }

        /**
         * Gets the modified total occupancy.
         *
         * @return  total quiver occupancy that the contents hold
         */
        @Override
        public @NotNull Fraction getTotalOccupancy() {
            return totalOccupancy;
        }
    }

    /**
     * Try to pickup the given stack into a quiver. Fails if the quiver stack is not valid.
     *
     * @param quiverStack   stack to try to insert into
     * @param stack         stack to try to insert
     * @return              true if the added stack is now empty; false otherwise
     */
    public static boolean attemptPickup(@NotNull ItemStack quiverStack, @NotNull ItemStack stack) {
        QuiverContents contents = QuiverContents.of(quiverStack);
        if (contents == null) {
            return false;
        }
        contents.add(stack);
        return stack.isEmpty();
    }
}
