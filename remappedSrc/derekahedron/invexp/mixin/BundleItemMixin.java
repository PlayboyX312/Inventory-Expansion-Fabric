package derekahedron.invexp.mixin;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BundleItem.class)
public class BundleItemMixin {

    /**
     * We allow showing the full bundle contents, so we set the return value here to just
     * the size of the contents.
     */
    @Inject(
            method = "getNumberOfStacksShown",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void getFullNumberOfStacksShown(
            @NotNull ItemStack stack, @NotNull CallbackInfoReturnable<Integer> cir
    ) {
        BundleContentsComponent contents = stack.getOrDefault(
                DataComponentTypes.BUNDLE_CONTENTS, BundleContentsComponent.DEFAULT
        );
        cir.setReturnValue(contents.size());
    }
}
