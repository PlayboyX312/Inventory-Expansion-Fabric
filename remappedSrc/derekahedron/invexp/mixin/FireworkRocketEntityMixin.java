package derekahedron.invexp.mixin;

import derekahedron.invexp.sack.SackContents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(FireworkRocketEntity.class)
public class FireworkRocketEntityMixin {

    /**
     * getHandPosOffset() does not detect selected stack. Instead of modifying that function,
     * we instead modify the args when the position is set.
     */
    @ModifyArgs(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/projectile/FireworkRocketEntity;setPosition(DDD)V"
            )
    )
    private void setProperVect(Args args) {
        FireworkRocketEntity self = (FireworkRocketEntity) (Object) this;
        if (self.shooter instanceof PlayerEntity player && player.isGliding()) {
            SackContents contents = SackContents.of(player.getOffHandStack());
            // If the offhand selected stack is a firework but the main hand selected stack is not update args
            // so the position is at the offhand.
            if (contents != null &&
                    contents.getSelectedStack().isOf(Items.FIREWORK_ROCKET) &&
                    !SackContents.selectedStackOf(player.getMainHandStack()).isOf(Items.FIREWORK_ROCKET)
            ) {
                Vec3d vec3d = player.getRotationVector(
                        0.0F,
                        player.getYaw() + (player.getMainArm().getOpposite() == Arm.RIGHT ? 80 : -80)
                ).multiply(0.5);
                args.set(0, player.getX() + vec3d.x);
                args.set(1, player.getY() + vec3d.y);
                args.set(2, player.getZ() + vec3d.z);
            }
        }
    }
}
