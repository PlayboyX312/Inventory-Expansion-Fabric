package derekahedron.invexp.mixin;

import derekahedron.invexp.util.ContainerItemContents;
import derekahedron.invexp.util.ContainerItemContentsReader;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShulkerBoxBlockEntity.class)
public class ShulkerBoxBlockEntityMixin {

    /**
     * This tests if an item can be inserted into a shulker box. If it can,
     * we want to make sure that this item is does not contain items that cannot be inserted.
     * This doesn't really have a use in vanilla, but can possibly prevent infinite stacking for mods.
     */
    @Inject(
            method = "canInsert",
            at = @At("RETURN"),
            cancellable = true
    )
    private void canInsertContainerItem(
            int slot, ItemStack stack, Direction dir, @NotNull CallbackInfoReturnable<Boolean> cir
    ) {
        if (cir.getReturnValue()) {
            ContainerItemContentsReader contents = ContainerItemContents.of(stack);
            if (contents != null && !contents.isEmpty()) {
                ShulkerBoxBlockEntity self = (ShulkerBoxBlockEntity) (Object) this;
                for (ItemStack nestedStack : contents.getStacks()) {
                    if (!self.canInsert(slot, nestedStack, dir)) {
                        cir.setReturnValue(false);
                        return;
                    }
                }
            }
        }
    }
}
