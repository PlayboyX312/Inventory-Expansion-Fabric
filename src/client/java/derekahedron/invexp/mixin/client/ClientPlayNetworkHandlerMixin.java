package derekahedron.invexp.mixin.client;

import derekahedron.invexp.util.DataPackChangeDetector;
import derekahedron.invexp.util.ContainerItemContents;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.common.SynchronizeTagsS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    /**
     * Item bobbing animation plays when the count of an item increases.
     * If comparing two containers, the count will always be one. So before comparing,
     * we check if the contents has gained an item, and if so, we set the count of the old
     * item stack to nothing so it will pass the count comparison.
     */
    @Inject(
            method = "onScreenHandlerSlotUpdate",
            at = @At(
                    value = "INVOKE",
                    ordinal = 1,
                    target = "Lnet/minecraft/item/ItemStack;isEmpty()Z"
            ),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void changeCountBeforeComparison (
            ScreenHandlerSlotUpdateS2CPacket packet, @NotNull CallbackInfo ci,
            PlayerEntity playerEntity, ItemStack itemStack, int i,
            boolean bl, ItemStack itemStack2
    ) {
        ContainerItemContents oldContents = ContainerItemContents.of(itemStack2);
        ContainerItemContents newContents = ContainerItemContents.of(itemStack);
        if (oldContents == null || newContents == null) {
            return;
        }
        int oldCount = 0;
        int newCount = 0;
        for (ItemStack stack : oldContents.getStacks()) {
            oldCount += stack.getCount();
        }
        for (ItemStack stack : newContents.getStacks()) {
            newCount += stack.getCount();
        }
        if (newCount > oldCount) {
            itemStack2.setCount(0);
        }
    }

    /**
     * After receiving new tags from the server, if the connection is not local,
     * signal data pack change.
     */
    @Inject(
            method = "onSynchronizeTags",
            at = @At("RETURN")
    )
    private void afterSynchronizeTags(SynchronizeTagsS2CPacket packet, @NotNull CallbackInfo ci) {
        ClientPlayNetworkHandler self = (ClientPlayNetworkHandler) (Object) this;
        if (!self.connection.isLocal()) {
            DataPackChangeDetector.markDirty();
        }
    }
}
