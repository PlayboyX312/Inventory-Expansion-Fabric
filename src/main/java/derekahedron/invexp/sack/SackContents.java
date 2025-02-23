package derekahedron.invexp.sack;

import com.google.common.collect.Lists;
import derekahedron.invexp.component.InvExpDataComponentTypes;
import derekahedron.invexp.component.types.SackContentsComponent;
import derekahedron.invexp.entity.player.PlayerEntityDuck;
import derekahedron.invexp.registry.DecentralizedReference;
import derekahedron.invexp.util.ContainerItemContents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import org.apache.commons.lang3.math.Fraction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Manages contents of a sack. Extends ContainerItemContents for improved modifying of
 * contents.
 */
public class SackContents extends ContainerItemContents implements SackContentsChecker {
    public final ItemStack sackStack;
    public SackContentsComponent component;

    /**
     * Creates a new SackContents object. Private as of() should be used to create
     * SackContents, so we can ensure they are valid.
     *
     * @param sackStack     stack containing the contents
     * @param component     contents component
     */
    private SackContents(@NotNull ItemStack sackStack, @NotNull SackContentsComponent component) {
        this.sackStack = sackStack;
        this.component = component;
    }

    /**
     * Create a new SackContents from the given stack. If the stack cannot have
     * contents, returns null
     *
     * @param sackStack     stack to create contents from
     * @return              created SackContents; null if not valid
     */
    public static @Nullable SackContents of(@Nullable ItemStack sackStack) {
        SackContentsComponent component = SackContentsComponent.getComponent(sackStack);
        if (component == null) {
            return null;
        }
        return new SackContents(sackStack, component);
    }

    /**
     * Gets the selected stack from the given stack assuming it is a valid sack.
     * This is a helper used often in mixins with vanilla code.
     *
     * @param sackStack     stack to fetch selected stack from
     * @return              selected stack or given stack if invalid
     */
    public static @NotNull ItemStack selectedStackOf(@NotNull ItemStack sackStack) {
        SackContents contents = SackContents.of(sackStack);
        if (contents != null && !contents.isEmpty()) {
            return contents.getSelectedStack();
        }
        else {
            return sackStack;
        }
    }

    /**
     * Gets the selected stack from the given stack. If the user passed in is a player
     * with a current sack usage for the given stack, return the stack stored in the sack usage
     * given it is equivalent to the selected stack.
     * This is so we can fetch the same ItemStack instance as vanilla code often
     * uses instance equality checks, like checking for if an item stack is equal to the active item stack.
     *
     * @param user          User of the sack
     * @param sackStack     stack to fetch the selected stack from
     * @return              selected stack or given stack if invalid
     */
    public static @NotNull ItemStack selectedStackOf(@Nullable LivingEntity user, @NotNull ItemStack sackStack) {
        if (user instanceof PlayerEntity player) {
            SackUsage usage = ((PlayerEntityDuck) player).invexp$getUsageForSackStack(sackStack);
            if (usage != null) {
                SackContents contents = SackContents.of(usage.sackStack);
                if (contents != null && !contents.isEmpty()) {
                    ItemStack selectedStack = contents.getSelectedStack();
                    if (ItemStack.areEqual(usage.selectedStack, selectedStack)) {
                        return usage.selectedStack;
                    }
                    else {
                        return selectedStack;
                    }
                }
                else {
                    return usage.sackStack;
                }
            }
        }
        return SackContents.selectedStackOf(sackStack);
    }

    /**
     * Checks the validity of the sack contents. First checks the component validity, which
     * is calculated when the component is created and when the DataPacks are updated.
     * Then checks for weight, types, and max stacks, which are all instant checks.
     *
     * @return  if the sack contents are valid
     */
    public boolean isValid() {
        if (!component.isValid()) {
            return false;
        }
        return (getSackTypes().size() <= getMaxSackTypes() &&
                component.getTotalWeight() <= getMaxSackWeight() &&
                getStacks().size() <= getMaxSackStacks());
    }

    /**
     * Checks if the contents are valid. If they are not, create a new SackContents
     * and add each item one by one. Leftover stacks are given to the player after the validation.
     *
     * @param player    player holding the sack
     */
    public void validate(@NotNull PlayerEntity player) {
        if (isValid()) {
            return;
        }

        List<ItemStack> removedStacks = new ArrayList<>(getStacks().size());
        SackContents newContents = new SackContents(sackStack, SackContentsComponent.DEFAULT);
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
        return component.getStacks();
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
     * Check if the contents have reached max stacks or the total weight has reached max weight.
     *
     * @return  true if the contents should display as full
     */
    @Override
    public boolean isFull() {
        return getTotalWeight() >= getMaxSackWeight() || getStacks().size() >= getMaxSackStacks();
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
            return Fraction.getFraction(getTotalWeight(), getMaxSackWeight());
        }
    }

    /**
     * Gets the stored sack stack.
     *
     * @return  sack stack that holds the contents
     */
    @Override
    public @NotNull ItemStack getSackStack() {
        return sackStack;
    }

    /**
     * Gets the sack types from the component.
     *
     * @return  List of all sack types in the contents
     */
    @Override
    public @NotNull List<RegistryEntry<SackType>> getSackTypes() {
        return component.getSackTypes();
    }

    /**
     * Gets the total weight from the component.
     *
     * @return  total sack weight that the contents hold
     */
    @Override
    public int getTotalWeight() {
        return component.getTotalWeight();
    }

    /**
     * Create a new builder for modifying sack contents.
     *
     * @return  builder for sack contents
     */
    @Override
    public @NotNull Builder getBuilder() {
        return new Builder();
    }

    /**
     * Builder for SackContents. Contains a copy of the sack contents to be modified.
     */
    public class Builder extends ContainerItemContents.Builder implements SackContentsChecker {
        public final List<RegistryEntry<SackType>> sackTypes;
        public final List<ItemStack> stacks;
        public int selectedIndex;
        public int totalWeight;

        /**
         * Copies component data into modifiable versions.
         */
        public Builder() {
            this.sackTypes = new ArrayList<>(component.getSackTypes());
            this.stacks = new ArrayList<>(component.getStacks());
            this.selectedIndex = component.selectedIndex;
            this.totalWeight = component.getTotalWeight();
        }

        /**
         * Applies the copied values to the SackContents object this is attached to.
         */
        @Override
        public void apply() {
            component = new SackContentsComponent(
                    List.copyOf(sackTypes),
                    List.copyOf(stacks),
                    clampIndex(selectedIndex)
            );
            sackStack.set(InvExpDataComponentTypes.SACK_CONTENTS, component);
        }

        /**
         * Tries to add the given stack to the sack. First tries merging with existing items,
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

            int weight = SacksHelper.getSackWeight(stack);
            int added = 0;
            int toAdd = Math.min(stack.getCount(), getMaxAllowedByWeight(stack));
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
                            totalWeight += amount * weight;
                        }
                        if (toAdd <= 0) {
                            // Return early if all the item was added.
                            return added;
                        }
                    }
                }

                // Add remaining to new stack
                if (canAddStack()) {
                    if (insertAt <= selectedIndex) {
                        selectedIndex++;
                    }
                    added += toAdd;
                    totalWeight += toAdd * weight;
                    tryAddType(SacksHelper.getSackType(stack));
                    stacks.add(insertAt, stack.split(toAdd));
                    return added;
                }
            }
            return added;
        }

        /**
         * Remove the given stack from the sack contents, updating weight and types.
         *
         * @param stack     stack to remove
         * @param toRemove  how many of the given stack to remove
         * @return          how many items were removed
         */
        @Override
        public int remove(@NotNull ItemStack stack, int toRemove) {
            if (stacks.isEmpty() || stack.isEmpty() || toRemove <= 0) {
                return 0;
            }
            int removed = 0;
            int weight = SacksHelper.getSackWeight(stack);

            // Track if the selected index was removed
            boolean removedSelected = false;
            for (int i = 0; i < stacks.size() && toRemove > 0; i++) {
                ItemStack nestedStack = stacks.get(i);
                if (ItemStack.areItemsAndComponentsEqual(stack, nestedStack)) {
                    if (toRemove >= nestedStack.getCount()) {
                        removed += nestedStack.getCount();
                        toRemove -= nestedStack.getCount();
                        totalWeight -= weight * nestedStack.getCount();
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
                        totalWeight -= weight * toRemove;
                        stacks.set(i, nestedStack.copyWithCount(nestedStack.getCount() - toRemove));
                        toRemove = 0;
                    }
                }
            }

            // Update selected index to nearest matching stack
            if (removedSelected) {
                selectedIndex = nextSelectedIndex(stack, selectedIndex);
            }

            // Try to remove sack type
            if (removed > 0) {
                tryRemoveType(SacksHelper.getSackType(stack));
            }

            return removed;
        }

        /**
         * Pops the selected stack from the contents, updating weight and types.
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
            totalWeight -= SacksHelper.getSackWeight(selectedStack) * selectedStack.getCount();
            tryRemoveType(SacksHelper.getSackType(selectedStack));
            return selectedStack;
        }

        /**
         * Remove all stacks from contents and clear total weight and types.
         *
         * @return  List of copies of previous contents
         */
        @Override
        public @NotNull List<ItemStack> popAllStacks() {
            List<ItemStack> copies = Lists.transform(stacks, ItemStack::copy);
            stacks.clear();
            selectedIndex = -1;
            sackTypes.clear();
            totalWeight = 0;
            return copies;
        }

        /**
         * Gets the sack stored in the related StackContents.
         *
         * @return  sack stack that holds the contents
         */
        @Override
        public @NotNull ItemStack getSackStack() {
            return sackStack;
        }

        /**
         * Gets the modified sack types.
         *
         * @return  List of all sack types in the contents
         */
        @Override
        public @NotNull List<RegistryEntry<SackType>> getSackTypes() {
            return sackTypes;
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
         * Gets the modified total weight.
         *
         * @return  total sack weight that the contents hold
         */
        @Override
        public int getTotalWeight() {
            return totalWeight;
        }

        /**
         * Try to add the given sack type if it is not already in the list of types.
         *
         * @param sackType  sack type to try to add
         */
        public void tryAddType(@Nullable RegistryEntry<SackType> sackType) {
            if (sackType != null && !isInTypes(sackType)) {
                sackTypes.add(sackType);
            }
        }

        /**
         * Try to remove the sack type from the list of types if there is not an item
         * in the contents that has the type.
         *
         * @param sackType  sack type to try to remove
         */
        public void tryRemoveType(@Nullable RegistryEntry<SackType> sackType) {
            if (sackType == null || !isInTypes(sackType)) {
                return;
            }
            for (ItemStack nestedStack : getStacks()) {
                if (DecentralizedReference.referencesEqual(sackType, SacksHelper.getSackType(nestedStack))) {
                    return;
                }
            }
            for (int i = 0; i < sackTypes.size(); i++) {
                if (DecentralizedReference.referencesEqual(sackType, sackTypes.get(i))) {
                    sackTypes.remove(i);
                    i--;
                }
            }
        }
    }

    /**
     * Try to pickup the given stack into a sack. Fails if the sack stack is not valid.
     * Allows pickup if the sack already has the given type. Will not add the type to the sack.
     *
     * @param sackStack     stack to try to insert into
     * @param stack         stack to try to insert
     * @return              true if the added stack is now empty; false otherwise
     */
    public static boolean attemptPickup(@NotNull ItemStack sackStack, @NotNull ItemStack stack) {
        SackContents contents = SackContents.of(sackStack);
        if (contents == null) {
            return false;
        }
        RegistryEntry<SackType> sackType = SacksHelper.getSackType(stack);
        if (sackType == null || !contents.isInTypes(sackType)) {
            return false;
        }
        contents.add(stack);
        return stack.isEmpty();
    }
}
