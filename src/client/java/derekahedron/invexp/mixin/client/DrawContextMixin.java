package derekahedron.invexp.mixin.client;

import derekahedron.invexp.sack.SackContents;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(DrawContext.class)
public class DrawContextMixin {

    /**
     * Draw cooldown progress for the selected stack if it exists, otherwise draw for the
     * original stack.
     */
    @ModifyVariable(
            method = "drawCooldownProgress",
            at = @At("HEAD"),
            argsOnly = true
    )
    private @NotNull ItemStack drawCooldownProgressForSelectedStack(ItemStack stack) {
        return SackContents.selectedStackOf(stack);
    }
}
