package derekahedron.invexp.mixin.client;

import derekahedron.invexp.sack.SackContents;
import net.minecraft.client.render.entity.FishingBobberEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.util.Arm;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FishingBobberEntityRenderer.class)
public class FishingBobberEntityRendererMixin {

    /**
     * Default behavior assumes the offhand if there is no fishing rod in the main hand.
     * Before the check, we should first check if there is a fishing rod in the main hand selected
     * stack.
     */
    @Inject(
            method = "getArmHoldingRod",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void getArmHoldingRod(@NotNull PlayerEntity player, @NotNull CallbackInfoReturnable<Arm> cir) {
        SackContents contents = SackContents.of(player.getMainHandStack(), player.getWorld());
        if (contents != null && !contents.isEmpty() && contents.getSelectedStack().getItem() instanceof FishingRodItem) {
            cir.setReturnValue(player.getMainArm());
        }
    }
}
