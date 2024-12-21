package derekahedron.invexp.mixin.client;

import derekahedron.invexp.entity.player.PlayerEntityDuck;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    /**
     * Start player using sack before they use an item.
     */
    @Inject(
            method = "doItemUse",
            at = @At("HEAD")
    )
    private void beforeItemUse(@NotNull CallbackInfo ci) {
        MinecraftClient self = (MinecraftClient) (Object) this;
        if (self.player != null) {
            ((PlayerEntityDuck) self.player).invexp$startUsingSack();
        }
    }

    /**
     * Stop player using sack after they use an item.
     */
    @Inject(
            method = "doItemUse",
            at = @At("RETURN")
    )
    private void afterItemUse(@NotNull CallbackInfo ci) {
        MinecraftClient self = (MinecraftClient) (Object) this;
        if (self.player != null) {
            ((PlayerEntityDuck) self.player).invexp$stopUsingSack();
        }
    }
}
