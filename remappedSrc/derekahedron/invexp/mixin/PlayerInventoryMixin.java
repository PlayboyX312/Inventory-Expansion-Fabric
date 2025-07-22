package derekahedron.invexp.mixin;

import derekahedron.invexp.entity.player.PlayerEntityDuck;
import derekahedron.invexp.quiver.QuiverContents;
import derekahedron.invexp.sack.SackContents;
import derekahedron.invexp.sack.SackUsage;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {

    /**
     * When an item is inserted into the players inventory, first try inserting into quivers and sacks.
     * First search for quivers. If there are no quivers, search again for sacks.
     */
    @Inject(
            method = "insertStack(Lnet/minecraft/item/ItemStack;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void insertIntoContainerItem(ItemStack stack, @NotNull CallbackInfoReturnable<Boolean> cir) {
        PlayerInventory self = (PlayerInventory) (Object) this;

        // First attempt pickup into quivers
        // Start by attempting pickup into main hand
        if (QuiverContents.attemptPickup(self.getStack(self.selectedSlot), stack)) {
            cir.setReturnValue(true);
            return;
        }
        // Then try offhand
        else if (QuiverContents.attemptPickup(self.getStack(40), stack)) {
            cir.setReturnValue(true);
            return;
        }
        else {
            // Then try remaining slots
            for (int slot = 0; slot < self.main.size(); slot++) {
                if (QuiverContents.attemptPickup(self.getStack(slot), stack)) {
                    cir.setReturnValue(true);
                    return;
                }
            }
        }

        // Next try picking up into sacks
        // Start with main hand
        if (SackContents.attemptPickup(self.getStack(self.selectedSlot), stack)) {
            cir.setReturnValue(true);
        }
        // Then try offhand
        else if (SackContents.attemptPickup(self.getStack(40), stack)) {
            cir.setReturnValue(true);
        }
        else {
            // Finally try remaining slots
            for (int slot = 0; slot < self.main.size(); slot++) {
                if (SackContents.attemptPickup(self.getStack(slot), stack)) {
                    cir.setReturnValue(true);
                    break;
                }
            }
        }
    }

    /**
     * When removing an item from the player inventory, if the player is currently using
     * the sack, remove that item if it is in one of the players usages.
     */
    @Inject(
            method = "removeOne",
            at = @At("HEAD"),
            cancellable = true
    )
    private void removeFromSack(ItemStack stack, @NotNull CallbackInfo ci) {
        PlayerInventory self = (PlayerInventory) (Object) this;
        if (((PlayerEntityDuck) self.player).invexp$isUsingSack()) {
            SackUsage usage = ((PlayerEntityDuck) self.player).invexp$getUsageForSelectedStack(stack);
            if (usage != null) {
                usage.selectedStack = ItemStack.EMPTY;
                ci.cancel();
            }
        }
    }

    /**
     * Dropping an item from a sack should drop one of the selected item if the entire stack
     * isn't being dropped.
     */
    @Inject(
            method = "dropSelectedItem",
            at = @At("HEAD"),
            cancellable = true
    )
    private void dropSelectedItemFromSack(boolean entireStack, @NotNull CallbackInfoReturnable<ItemStack> info) {
        // Dropping the entire stack should drop the sack
        if (entireStack) {
            return;
        }
        PlayerInventory self = (PlayerInventory) (Object) this;
        ItemStack sackStack = self.getMainHandStack();
        SackContents contents = SackContents.of(sackStack);
        if (contents == null || contents.isEmpty()) {
            return;
        }
        info.setReturnValue(contents.popSelectedItem());
    }

    /**
     * After getting a slot with the stack fails, try again but instead check
     * for sacks with the stack in their contents.
     */
    @Inject(
            method = "getSlotWithStack",
            at = @At("RETURN"),
            cancellable = true
    )
    private void getSlotWithStackInSack(ItemStack stack, @NotNull CallbackInfoReturnable<Integer> cir) {
        // only run if the normal function does not find anything.
        if (cir.getReturnValue() != -1) {
            return;
        }
        PlayerInventory self = (PlayerInventory) (Object) this;

        // We want to prioritize sacks that already have the item selected, but if we find one that
        // doesn't, we store it here.
        int backupSlot = -1;
        int newSelectedIndex = -1;

        for (int slot = 0; slot < self.main.size(); slot++) {
            SackContents contents = SackContents.of(self.main.get(slot));
            if (contents != null && !contents.isEmpty()) {
                if (ItemStack.areItemsAndComponentsEqual(stack, contents.getSelectedStack())) {
                    // Find first sack that has the item selected already
                    cir.setReturnValue(slot);
                    return;
                }
                else if (backupSlot == -1) {
                    // Otherwise, if a backup hasn't been found, test if the item is in the stack
                    newSelectedIndex = contents.indexOf(stack, contents.getSelectedIndex());
                    if (newSelectedIndex != -1) {
                        backupSlot = slot;
                    }
                }
            }
        }

        // If there is a backup, set the selected index of that backup and return the slot.
        if (backupSlot != -1) {
            SackContents contents = SackContents.of(self.main.get(backupSlot));
            if (contents != null) {
                contents.setSelectedIndex(newSelectedIndex);
                cir.setReturnValue(backupSlot);
            }
        }
    }
}
