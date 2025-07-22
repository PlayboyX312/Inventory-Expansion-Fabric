package derekahedron.invexp.mixin;

import derekahedron.invexp.entity.player.PlayerEntityDuck;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {

    /**
     * When interacting with an item, ServerPlayers sometimes sync their inventory with
     * the client. This happens before we stop using the sack, so we detect that here and
     * stop prematurely.
     */
    @Inject(
            method = "interactItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/screen/PlayerScreenHandler;syncState()V"
            )
    )
    private void stopUsingSackBeforeSync(
            ServerPlayerEntity player, World world, ItemStack stack, Hand hand,
            @NotNull CallbackInfoReturnable<ActionResult> cir
    ) {
        ((PlayerEntityDuck) player).invexp$stopUsingSack();
    }
}
