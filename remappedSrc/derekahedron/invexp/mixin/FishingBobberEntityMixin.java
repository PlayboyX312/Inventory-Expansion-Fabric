package derekahedron.invexp.mixin;

import derekahedron.invexp.sack.SackContents;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(FishingBobberEntity.class)
public class FishingBobberEntityMixin {

    /**
     * Test the selected item in the main hand sack stack
     */
    @ModifyVariable(
            method = "removeIfInvalid",
            ordinal = 0,
            at = @At("STORE")
    )
    private @NotNull ItemStack checkMainHand(ItemStack stack) {
        return SackContents.selectedStackOf(stack);
    }


    /**
     * Test the selected item in the offhand sack stack
     */
    @ModifyVariable(
            method = "removeIfInvalid",
            ordinal = 1,
            at = @At("STORE")
    )
    private @NotNull ItemStack checkOffHand(ItemStack stack) {
        return SackContents.selectedStackOf(stack);
    }
}
