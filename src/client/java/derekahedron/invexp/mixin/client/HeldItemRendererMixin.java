package derekahedron.invexp.mixin.client;

import derekahedron.invexp.sack.SackContents;
import derekahedron.invexp.sack.SackContentsReader;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {

    /**
     * Use the selected stack for the main hand when getting the hand render type.
     */
    @ModifyVariable(
            method = "getHandRenderType(Lnet/minecraft/client/network/ClientPlayerEntity;)Lnet/minecraft/client/render/item/HeldItemRenderer$HandRenderType;",
            at = @At("STORE"),
            ordinal = 0
    )
    private static @NotNull ItemStack getRenderTypeForMainHand(ItemStack stack, ClientPlayerEntity player) {
        return SackContents.selectedStackOf(player, stack);
    }

    /**
     * Use the selected stack for the offhand when getting the hand render type.
     */
    @ModifyVariable(
            method = "getHandRenderType(Lnet/minecraft/client/network/ClientPlayerEntity;)Lnet/minecraft/client/render/item/HeldItemRenderer$HandRenderType;",
            at = @At("STORE"),
            ordinal = 1
    )
    private static @NotNull ItemStack getRenderTypeForOffHand(ItemStack stack, ClientPlayerEntity player) {
        return SackContents.selectedStackOf(player, stack);
    }

    /**
     * Check if the selected stack is a charged crossbow to get the proper hand render type.
     */
    @ModifyArg(
            method = "getUsingItemHandRenderType",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/item/HeldItemRenderer;isChargedCrossbow(Lnet/minecraft/item/ItemStack;)Z"
            )
    )
    private static ItemStack isSackChargedCrossbow(ItemStack stack) {
        SackContentsReader contents = SackContents.of(stack);
        if (contents == null || contents.isEmpty()) {
            return stack;
        }
        return contents.getSelectedStack();
    }

    /**
     * Renders the selected stack in the main hand
     */
    @ModifyVariable(
            method = "updateHeldItems",
            at = @At("STORE"),
            ordinal = 0
    )
    private @NotNull ItemStack updateMainHandSelectedStack(ItemStack stack) {
        HeldItemRenderer self = (HeldItemRenderer) (Object) this;
        return SackContents.selectedStackOf(self.client.player, stack);
    }

    /**
     * Renders the selected stack in the offhand
     */
    @ModifyVariable(
            method = "updateHeldItems",
            at = @At("STORE"),
            ordinal = 1
    )
    private @NotNull ItemStack updateOffHandSelectedStack(ItemStack stack) {
        HeldItemRenderer self = (HeldItemRenderer) (Object) this;
        return SackContents.selectedStackOf(self.client.player, stack);
    }
}
