package derekahedron.invexp.mixin;

import derekahedron.invexp.sack.SackContents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(MapState.class)
public class MapStateMixin {

    /**
     * When updating trackers, an equal predicate is used to detect if there is a map
     * item in the player inventory that matches the map we are updating. This modifies the
     * predicate to also search inside sacks for a map that matches.
     */
    @Inject(
            method = "getEqualPredicate",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void getBetterEqualPredicate(
            ItemStack stack, @NotNull CallbackInfoReturnable<Predicate<ItemStack>> cir
    ) {
        Predicate<ItemStack> predicate = cir.getReturnValue();
        cir.setReturnValue((other) -> {
            if (predicate.test(other)) {
                return true;
            }
            SackContents contents = SackContents.of(other);
            if (contents != null && !contents.isEmpty()) {
                for (ItemStack nestedStack : contents.getStacks()) {
                    if (predicate.test(nestedStack)) {
                        return true;
                    }
                }
            }
            return false;
        });
    }
}
