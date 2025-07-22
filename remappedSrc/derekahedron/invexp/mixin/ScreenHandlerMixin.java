package derekahedron.invexp.mixin;

import derekahedron.invexp.item.QuiverItem;
import derekahedron.invexp.item.SackItem;
import derekahedron.invexp.quiver.QuiverContents;
import derekahedron.invexp.sack.SackContents;
import derekahedron.invexp.sack.SacksHelper;
import derekahedron.invexp.util.ContainerItemContents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Stream;

@Mixin(ScreenHandler.class)
public class ScreenHandlerMixin {

    /**
     * Adds functionality for double-clicking sacks and quivers in an inventory.
     * Doing so picks up all items that can be added and inserts them into the container.
     */
    @Inject(
            method = "internalOnSlotClick",
            at = @At("HEAD"),
            cancellable = true
    )
    private void doubleClickContainerItem(
            int slotIndex, int button, SlotActionType actionType, PlayerEntity player, @NotNull CallbackInfo ci
    ) {
        ScreenHandler self = (ScreenHandler) (Object) this;
        // First, make sure it was a double click on a valid slot
        ItemStack cursorStack = self.getCursorStack();
        if (slotIndex < 0 || actionType != SlotActionType.PICKUP_ALL) {
            return;
        }
        Slot selectedSlot = self.slots.get(slotIndex);
        if (selectedSlot.hasStack() && selectedSlot.canTakeItems(player)) {
            return;
        }

        // Check that the item is a valid container
        ContainerItemContents contents = ContainerItemContents.of(cursorStack);
        if (contents == null || (contents.isEmpty() && contents instanceof SackContents)) {
            return;
        }

        // First pull from slots that aren't full, then pull from slots that are full.
        // This is in line with vanilla behavior.
        ArrayList<Slot> partialSlots = new ArrayList<>(self.slots.size());
        ArrayList<Slot> fullSlots = new ArrayList<>(self.slots.size());
        for (Slot slot : self.slots) {
            if (slot.hasStack() && self.canInsertIntoSlot(cursorStack, slot)) {
                ItemStack stack = slot.getStack();
                if (stack.getCount() < stack.getMaxCount()) {
                    partialSlots.add(slot);
                }
                else {
                    fullSlots.add(slot);
                }
            }
        }

        // Following vanilla behavior, if button != 0, reverse order.
        if (button != 0) {
            Collections.reverse(partialSlots);
            Collections.reverse(fullSlots);
        }

        // Combine slot streams
        Stream<Slot> slots = Stream.concat(partialSlots.stream(), fullSlots.stream());

        if (contents instanceof SackContents sackContents) {
            // Only add slots that already have their type in the sack
            slots = slots.filter(slot -> sackContents.isInTypes(SacksHelper.getSackType(slot.getStack())));
        }

        // Try adding all slots and play sound if successful
        if (contents.add(slots, player) > 0) {
            if (contents instanceof SackContents) {
                SackItem.playInsertSound(player);
            }
            else if (contents instanceof QuiverContents) {
                QuiverItem.playInsertSound(player);
            }
        }
        ci.cancel();
    }
}
