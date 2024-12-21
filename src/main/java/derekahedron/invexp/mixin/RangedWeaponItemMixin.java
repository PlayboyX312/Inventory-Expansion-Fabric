package derekahedron.invexp.mixin;

import derekahedron.invexp.quiver.QuiverContents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(RangedWeaponItem.class)
public class RangedWeaponItemMixin {

    /**
     * When getting the held projectile, first check if a quiver is being held,
     * then check the contents of that quiver. If there is a match, return a created
     * QuiveredItemStack.
     */
    @Inject(
            method = "getHeldProjectile",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void getHeldProjectileFromQuiver(
            LivingEntity entity, Predicate<ItemStack> predicate, @NotNull CallbackInfoReturnable<ItemStack> cir
    ) {
        // Check hands in reverse (Offhand first)
        for (int i = Hand.values().length - 1; i >= 0; i--) {
            Hand hand = Hand.values()[i];

            QuiverContents contents = QuiverContents.of(entity.getStackInHand(hand));
            if (contents != null) {
                ItemStack projectile = contents.getProjectileStack(predicate);
                if (!projectile.isEmpty()) {
                    cir.setReturnValue(projectile);
                    return;
                }
            }
        }
    }
}
