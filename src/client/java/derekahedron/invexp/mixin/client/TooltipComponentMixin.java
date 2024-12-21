package derekahedron.invexp.mixin.client;

import derekahedron.invexp.gui.tooltip.QuiverTooltipComponent;
import derekahedron.invexp.gui.tooltip.SackTooltipComponent;
import derekahedron.invexp.item.tooltip.QuiverTooltipData;
import derekahedron.invexp.item.tooltip.SackTooltipData;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.tooltip.TooltipData;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TooltipComponent.class)
public interface TooltipComponentMixin {

    /**
     * Render custom tooltips for sacks and quivers.
     */
    @Inject(
            method = "of(Lnet/minecraft/item/tooltip/TooltipData;)Lnet/minecraft/client/gui/tooltip/TooltipComponent;",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void ofCustomTooltip(
            @NotNull TooltipData tooltipData, @NotNull CallbackInfoReturnable<TooltipComponent> cir
    ) {
        switch (tooltipData) {
            case SackTooltipData sackTooltipData:
                cir.setReturnValue(new SackTooltipComponent(sackTooltipData.contents()));
                break;
            case QuiverTooltipData quiverTooltipData:
                cir.setReturnValue(new QuiverTooltipComponent(quiverTooltipData.contents()));
                break;
            default:
                break;
        }
    }
}
