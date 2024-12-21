package derekahedron.invexp.mixin.client;

import derekahedron.invexp.entity.player.PlayerEntityDuck;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

    /**
     * Start player using sack before they stop using an item.
     */
    @Inject(
            method = "stopUsingItem",
            at = @At("HEAD")
    )
    private void beforeStopUsingItem(PlayerEntity player, @NotNull CallbackInfo ci) {
        ((PlayerEntityDuck) player).invexp$startUsingSack();
    }

    /**
     * Stop player using sack after they stop using an item.
     */
    @Inject(
            method = "stopUsingItem",
            at = @At("RETURN")
    )
    private void afterStopUsingItem(PlayerEntity player, @NotNull CallbackInfo ci) {
        ((PlayerEntityDuck) player).invexp$stopUsingSack();
    }
}
