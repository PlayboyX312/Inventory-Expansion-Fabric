package derekahedron.invexp.mixin.client;

import derekahedron.invexp.sack.SackContents;
import net.minecraft.client.render.entity.FishingBobberEntityRenderer;
import net.minecraft.client.render.item.property.bool.FishingRodCastProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FishingRodCastProperty.class)
public class FishingRodCastPropertyMixin {

    /**
     * If the value is none, we want to compare the selected stack in the hand of the player
     * instead of the regular stack in hand.
     */
    @Inject(
            method = "test",
            at = @At("RETURN"),
            cancellable = true
    )
    private void getValueIncludingSacks(
            ItemStack stack, ClientWorld world, LivingEntity entity, int seed, ItemDisplayContext displayContext, CallbackInfoReturnable<Boolean> cir
    ) {
        if (!cir.getReturnValue() && entity instanceof PlayerEntity player && player.fishHook != null) {
            Arm holdingArm = FishingBobberEntityRenderer.getArmHoldingRod(player);
            if (stack == SackContents.selectedStackOf(player, player.getStackInArm(holdingArm))) {
                cir.setReturnValue(true);
            }
        }
    }
}
