package derekahedron.invexp.util;

import derekahedron.invexp.quiver.QuiverContents;
import derekahedron.invexp.sack.ImmutableSackContents;
import derekahedron.invexp.sack.SackContents;
import derekahedron.invexp.sack.SackContentsReader;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Helps control the dragging functionality of the container item.
 */
public abstract class ContainerItemSlotDragger {

    /**
     * Create a new dragger from the given stack.
     *
     * @param stack     stack to create the dragger from
     * @return          new dragger from the given stack; null if stack cannot be dragged
     */
    public static @Nullable ContainerItemSlotDragger of(ItemStack stack) {
        ContainerItemContentsReader contents = ContainerItemContents.of(stack);
        // First check if item is a sack
        if (contents instanceof ImmutableSackContents sackContents) {
            return new SackSlotDragger(sackContents.sackStack);
        }
        // Second check if item is a quiver
        else if (contents instanceof QuiverContents quiverContents) {
            return new QuiverSlotDragger(quiverContents.quiverStack);
        }
        // Last check if item is a bundle
        else if (stack.getItem() instanceof BundleItem && stack.contains(DataComponentTypes.BUNDLE_CONTENTS)) {
            return new BundleSlotDragger(stack);
        }
        return null;
    }

    /**
     * Inserts or removes item in slot when hovered over.
     *
     * @param slot      slot hovered over
     * @param screen    screen being viewed
     */
    public void onHover(@NotNull Slot slot, @NotNull HandledScreen<?> screen) {
        ItemStack stack = slot.getStack();
        if (screen.getScreenHandler().canInsertIntoSlot(slot)) {
            if (screen.heldButtonType == 1) {
                if (stack.isEmpty() && !isEmpty()) {
                    screen.onMouseClick(slot, slot.getIndex(), screen.heldButtonType, SlotActionType.PICKUP);
                }
            }
            else if (screen.heldButtonType == 0) {
                if (!stack.isEmpty() && canTryInsert(stack)) {
                    screen.onMouseClick(slot, slot.getIndex(), screen.heldButtonType, SlotActionType.PICKUP);
                }
            }
        }
    }

    /**
     * Test if the stack should try to be inserted. Should be false for
     * items that will swap stacks with when clicked.
     *
     * @param stack     stack to test
     * @return          true if we should try inserting; false otherwise
     */
    public abstract boolean canTryInsert(ItemStack stack);

    /**
     * Checks if the given container item is empty
     *
     * @return  true if the container is empty; false otherwise
     */
    public abstract boolean isEmpty();

    /**
     * SlotDragger for Bundles
     */
    public static class BundleSlotDragger extends ContainerItemSlotDragger {
        public final ItemStack bundleStack;

        /**
         * Create a new BundleSlotDragger from bundle contents
         *
         * @param bundleStack   bundle stack to make the dragger from
         */
        public BundleSlotDragger(ItemStack bundleStack) {
            this.bundleStack = bundleStack;
        }

        /**
         * @return  true if the component is empty; false otherwise
         */
        @Override
        public boolean isEmpty() {
            BundleContentsComponent contents = bundleStack.get(DataComponentTypes.BUNDLE_CONTENTS);
            return contents == null || contents.isEmpty();
        }

        /**
         * Bundles can always try inserting.
         *
         * @return  true always
         */
        @Override
        public boolean canTryInsert(ItemStack stack) {
            return true;
        }
    }

    /**
     * SlotDragger for Sacks
     */
    public static class SackSlotDragger extends ContainerItemSlotDragger {
        public final ItemStack sackStack;

        /**
         * Create a new SackSlotDragger from sack contents
         *
         * @param sackStack     sack stack to make the dragger from
         */
        public SackSlotDragger(ItemStack sackStack) {
            this.sackStack = sackStack;
        }

        /**
         * @return  true if the contents is empty; false otherwise
         */
        @Override
        public boolean isEmpty() {
            SackContentsReader contents = SackContents.of(sackStack);
            return contents == null || contents.isEmpty();
        }

        /**
         * @return  true if the item matches types and can be nested; false otherwise
         */
        @Override
        public boolean canTryInsert(ItemStack stack) {
            SackContentsReader contents = SackContents.of(sackStack);
            return contents != null && contents.canTryInsert(stack);
        }
    }

    /**
     * SlotDragger for Quivers
     */
    public static class QuiverSlotDragger extends ContainerItemSlotDragger {
        public final ItemStack quiverStack;

        /**
         * Create a new QuiverSlotDragger from quiver contents
         *
         * @param quiverStack   quiver stack to make the dragger from
         */
        public QuiverSlotDragger(ItemStack quiverStack) {
            this.quiverStack = quiverStack;
        }

        /**
         * @return  true if the contents is empty; false otherwise
         */
        @Override
        public boolean isEmpty() {
            QuiverContents contents = QuiverContents.of(quiverStack);
            return contents == null || contents.isEmpty();
        }

        /**
         * @return  true if the item is an arrow that can be nested; false otherwise
         */
        @Override
        public boolean canTryInsert(ItemStack stack) {
            QuiverContents contents = QuiverContents.of(quiverStack);
            return contents != null && contents.canTryInsert(stack);
        }
    }
}
