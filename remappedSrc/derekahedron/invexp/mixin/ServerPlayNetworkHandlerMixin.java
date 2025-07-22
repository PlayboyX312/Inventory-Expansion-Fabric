package derekahedron.invexp.mixin;


import derekahedron.invexp.entity.player.PlayerEntityDuck;
import derekahedron.invexp.sack.SackContents;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {

    /**
     * Start player using sack before they release hold on an item.
     */
    @Inject(
            method = "onPlayerAction",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/server/world/ServerWorld;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void beforeReleaseUseItem(@NotNull PlayerActionC2SPacket packet, @NotNull CallbackInfo ci) {
        if (packet.getAction() == PlayerActionC2SPacket.Action.RELEASE_USE_ITEM) {
            ((PlayerEntityDuck) ((ServerPlayNetworkHandler) (Object) this).player).invexp$startUsingSack();
        }
    }

    /**
     * Stop player using sack after they release hold on an item.
     */
    @Inject(
            method = "onPlayerAction",
            at = @At("RETURN")
    )
    private void afterReleaseUseItem(@NotNull PlayerActionC2SPacket packet, @NotNull CallbackInfo ci) {
        if (packet.getAction() == PlayerActionC2SPacket.Action.RELEASE_USE_ITEM) {
            ((PlayerEntityDuck) ((ServerPlayNetworkHandler) (Object) this).player).invexp$stopUsingSack();
        }
    }

    /**
     * Start player using sack before they interact with an item.
     */
    @Inject(
            method = "onPlayerInteractItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/server/world/ServerWorld;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void beforeInteractItem(PlayerInteractItemC2SPacket packet, @NotNull CallbackInfo ci) {
        ((PlayerEntityDuck) ((ServerPlayNetworkHandler) (Object) this).player).invexp$startUsingSack();
    }

    /**
     * Stop player using sack after they interact with an item.
     */
    @Inject(
            method = "onPlayerInteractItem",
            at = @At("RETURN")
    )
    private void afterInteractItem(PlayerInteractItemC2SPacket packet, @NotNull CallbackInfo ci) {
        ((PlayerEntityDuck) ((ServerPlayNetworkHandler) (Object) this).player).invexp$stopUsingSack();
    }

    /**
     * Start player using sack before they interact with a block.
     */
    @Inject(
            method = "onPlayerInteractBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/server/world/ServerWorld;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void beforeInteractBlock(PlayerInteractBlockC2SPacket packet, @NotNull CallbackInfo ci) {
        ((PlayerEntityDuck) ((ServerPlayNetworkHandler) (Object) this).player).invexp$startUsingSack();
    }

    /**
     * Stop player using sack after they interact with a block.
     */
    @Inject(
            method = "onPlayerInteractBlock",
            at = @At("RETURN")
    )
    private void afterInteractBlock(PlayerInteractBlockC2SPacket packet, @NotNull CallbackInfo ci) {
        ((PlayerEntityDuck) ((ServerPlayNetworkHandler) (Object) this).player).invexp$stopUsingSack();
    }

    /**
     * Start player using sack before they interact with an entity.
     */
    @Inject(
            method = "onPlayerInteractEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/server/world/ServerWorld;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void beforeInteractEntity(PlayerInteractEntityC2SPacket packet, @NotNull CallbackInfo ci) {
        ((PlayerEntityDuck) ((ServerPlayNetworkHandler) (Object) this).player).invexp$startUsingSack();
    }

    /**
     * Stop player using sack after they interact with an entity.
     */
    @Inject(
            method = "onPlayerInteractEntity",
            at = @At("RETURN")
    )
    private void afterInteractEntity(PlayerInteractEntityC2SPacket packet, @NotNull CallbackInfo ci) {
        ((PlayerEntityDuck) ((ServerPlayNetworkHandler) (Object) this).player).invexp$stopUsingSack();
    }

    /**
     * When a player tries to pick an item, if the item is in the sack they are
     * currently holding, change the selected index to select that item and cancel.
     */
    @Inject(
            method = "onPickItem",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onPickItemFromSack(@NotNull ItemStack stack, @NotNull CallbackInfo ci) {
        ServerPlayerEntity player = ((ServerPlayNetworkHandler) (Object) this).player;
        if (!stack.isItemEnabled(player.getWorld().getEnabledFeatures())) {
            return;
        }

        PlayerInventory inventory = player.getInventory();
        SackContents contents = SackContents.of(inventory.getStack(inventory.selectedSlot));
        if (contents != null && !contents.isEmpty()) {
            int newSelectedIndex = contents.indexOf(stack, contents.getSelectedIndex());
            if (newSelectedIndex != -1) {
                contents.setSelectedIndex(newSelectedIndex);
                // Tell client that the slot was updated
                player.networkHandler.sendPacket(new UpdateSelectedSlotS2CPacket(inventory.selectedSlot));
                player.playerScreenHandler.sendContentUpdates();
                ci.cancel();
            }
        }
    }
}
