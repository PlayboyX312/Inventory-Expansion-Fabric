package derekahedron.invexp.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import derekahedron.invexp.sack.SackContents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FilledMapItem.class)
public class FilledMapItemMixin {

    /**
     * We want to update maps if they are in a sack in the offhand, but vanilla code doesn't check this.
     * So we check if the stack we are ticking is equal to the offhand stack and update colors if so.
     */
    @Inject(
            method = "inventoryTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;getOffHandStack()Lnet/minecraft/item/ItemStack;"
            )
    )
    private void tickInOffhandSack(
            ItemStack stack, World world, Entity entity, int slot, boolean selected,
            @NotNull CallbackInfo ci, @Local @NotNull MapState mapState
    ) {
        // Same logic for checking maps as vanilla
        if (!mapState.locked && !selected && entity instanceof PlayerEntity player) {
            SackContents contents = SackContents.of(player.getOffHandStack());
            if (contents != null && !contents.isEmpty() && ItemStack.areEqual(stack, contents.getSelectedStack())) {
                ((FilledMapItem) (Object) this).updateColors(world, entity, mapState);
            }
        }
    }
}
