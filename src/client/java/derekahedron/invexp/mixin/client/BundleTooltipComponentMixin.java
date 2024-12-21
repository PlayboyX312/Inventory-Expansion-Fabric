package derekahedron.invexp.mixin.client;

import derekahedron.invexp.gui.tooltip.ExtendedBundleTooltipComponent;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.BundleTooltipComponent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BundleTooltipComponent.class)
public class BundleTooltipComponentMixin {

    /**
     * Bundles only display the first few items in vanilla. This mixin cancels the vanilla method for
     * rending the stacks in the bundle and instead uses a new extended component that renders
     * via the modded method. This should render identically unless there is another mod that modifies
     * bundle tooltip rendering.
     */
    @Inject(
            method = "drawNonEmptyTooltip",
            at = @At("HEAD"),
            cancellable = true
    )
    private void drawFullNonEmptyTooltip(
            TextRenderer textRenderer, int x, int y, int width, int height, DrawContext context,
            @NotNull CallbackInfo ci
    ) {
        ExtendedBundleTooltipComponent extendedComponent = new ExtendedBundleTooltipComponent((BundleTooltipComponent) (Object) (this));
        extendedComponent.drawContents(textRenderer, x, y, width, y, context);
        y += extendedComponent.getContentsHeight();
        y += extendedComponent.getProgressBarPadding();
        extendedComponent.drawProgressBar(textRenderer, x, y, width, context);
        ci.cancel();
    }
}
