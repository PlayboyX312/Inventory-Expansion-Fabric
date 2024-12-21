package derekahedron.invexp.mixin.client;

import derekahedron.invexp.sack.SackContents;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {

    /**
     * When updating the render state, we check the stack in the hand to see if
     * it is a spyglass. This checks the selected stack so the spyglass is properly rendered
     * when used.
     */
    @ModifyVariable(
            method = "updateRenderState(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;F)V",
            at = @At("STORE"),
            ordinal = 0
    )
    private @NotNull ItemStack getSelectedStackInHand(ItemStack stack) {
        return SackContents.selectedStackOf(stack);
    }
}
