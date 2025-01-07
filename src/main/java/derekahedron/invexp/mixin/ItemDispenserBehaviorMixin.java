package derekahedron.invexp.mixin;

import derekahedron.invexp.block.entity.DispenserBlockEntityDuck;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ItemDispenserBehavior.class)
public class ItemDispenserBehaviorMixin {

    /**
     * If the dispenser currently has a usage buffer, a sack is being used.
     * Add the stacks to the buffer instead.
     */
    @Inject(
            method = "addStackOrSpawn",
            at = @At("HEAD"),
            cancellable = true
    )
    public void catchAddedStacks(@NotNull BlockPointer pointer, ItemStack stack, @NotNull CallbackInfo ci) {
        DispenserBlockEntity dispenser = pointer.blockEntity();
        List<ItemStack> usageBuffer = ((DispenserBlockEntityDuck) dispenser).invexp$getUsageBuffer();
        if (usageBuffer != null) {
            usageBuffer.add(stack);
            ci.cancel();
        }
    }
}
