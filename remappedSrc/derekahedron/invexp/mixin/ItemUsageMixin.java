package derekahedron.invexp.mixin;

import derekahedron.invexp.entity.player.PlayerEntityDuck;
import derekahedron.invexp.sack.SackContents;
import derekahedron.invexp.sack.SackUsage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemUsage.class)
public class ItemUsageMixin {

    @Inject(
            method = "exchangeStack(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;Z)Lnet/minecraft/item/ItemStack;",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void exchange(
            ItemStack inputStack, @NotNull PlayerEntity player, ItemStack outputStack, boolean creativeOverride,
            @NotNull CallbackInfoReturnable<ItemStack> cir
    ) {
        if (player.isInCreativeMode()) {
            if (creativeOverride && player.getInventory().contains(stack -> {
                SackContents contents = SackContents.of(stack);
                if (contents != null && !contents.isEmpty()) {
                    for (ItemStack nestedStack : contents.getStacks()) {
                        if (!nestedStack.isEmpty() && ItemStack.areItemsAndComponentsEqual(nestedStack, outputStack)) {
                            return true;
                        }
                    }
                }
                return false;
            })) {
                cir.setReturnValue(inputStack);
            }
            return;
        }

        if (inputStack.getCount() <= 1) {
            return;
        }
        SackUsage usage = ((PlayerEntityDuck) player).invexp$getUsageForSelectedStack(inputStack);
        if (usage == null || !ItemStack.areItemsAndComponentsEqual(usage.originalSelectedStack, usage.selectedStack) || usage.originalSelectedStack.getCount() <= 1) {
            return;
        }
        SackContents contents = SackContents.of(usage.sackStack);
        if (contents == null || contents.isEmpty()) {
            return;
        }

        if (contents.remove(usage.selectedStack.copyWithCount(1)) == 0) {
            return;
        }
        usage.originalSelectedStack.decrement(1);
        inputStack.decrement(1);
        contents.add(outputStack);
        if (!outputStack.isEmpty() && !player.getInventory().insertStack(outputStack)) {
            player.dropItem(outputStack, false);
        }
        cir.setReturnValue(inputStack);
    }
}
