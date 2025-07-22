package derekahedron.invexp.mixin;

import derekahedron.invexp.entity.player.PlayerEntityDuck;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemFrameEntity.class)
public class ItemFrameEntityMixin {

    /**
     * Stop using the sack early when interacting with ItemFrame.
     */
    @Inject(
            method = "interact",
            at = @At("HEAD")
    )
    private void stopUsingSack(PlayerEntity player, Hand hand, @NotNull CallbackInfoReturnable<ActionResult> cir) {
        ((PlayerEntityDuck) player).invexp$stopUsingSack();
    }
}
