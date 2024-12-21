package derekahedron.invexp.gui.tooltip;

import net.minecraft.client.gui.tooltip.BundleTooltipComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.math.Fraction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Bundle tooltips do not allow scrolling through the entire list and only display a small amount
 * of items. Sack and Quiver tooltips allow scrolling through the entire list of items.
 * We want to allow scrolling through items on bundles, so we have a mixin inject the rendering
 * defined here over the bundle rendering process. It does so by creating an ExtendedBundleTooltipComponent
 * from the original tooltip and renders with that instead.
 */
public class ExtendedBundleTooltipComponent implements ContainerItemTooltipComponent {
    public final BundleTooltipComponent tooltipComponent;
    public final List<ItemStack> stacks;

    /**
     * Creates an extended bundle tooltip from a given tooltip. Makes and stores a copy
     * of the stacks of the bundle contents so we don't have to make a new one each time we want
     * to access it.
     *
     * @param tooltipComponent  original tooltip
     */
    public ExtendedBundleTooltipComponent(@NotNull BundleTooltipComponent tooltipComponent) {
        this.tooltipComponent = tooltipComponent;
        this.stacks = tooltipComponent.bundleContents.stream().toList();
    }

    /**
     * @return  copied list of stacks from the component
     */
    @Override
    public @NotNull List<ItemStack> getStacks() {
        return stacks;
    }

    /**
     * @return  selected index of the component
     */
    @Override
    public int getSelectedIndex() {
        return tooltipComponent.bundleContents.getSelectedStackIndex();
    }

    /**
     * @return  fullness fraction of the component
     */
    @Override
    public @NotNull Fraction getFillFraction() {
        return tooltipComponent.bundleContents.getOccupancy();
    }

    /**
     * Gets progress bar fill texture from the original tooltip.
     *
     * @return  texture identifier for the progress bar
     */
    @Override
    public @NotNull Identifier getProgressBarFillTexture() {
        return tooltipComponent.getProgressBarFillTexture();
    }

    /**
     * Gets the progress bar border texture from the original tooltip.
     *
     * @return  texture identifier for the progress bar border
     */
    @Override
    public @NotNull Identifier getProgressBarBorderTexture() {
        return BundleTooltipComponent.BUNDLE_PROGRESS_BAR_BORDER_TEXTURE;
    }

    /**
     * Gets the slot background texture from the original tooltip.
     *
     * @return  texture identifier for the slot background texture
     */
    @Override
    public @NotNull Identifier getSlotBackgroundTexture() {
        return BundleTooltipComponent.BUNDLE_SLOT_BACKGROUND_TEXTURE;
    }

    /**
     * Gets the highlighted back texture from the original tooltip.
     *
     * @return  texture identifier for highlighted back texture
     */
    @Override
    public @NotNull Identifier getSlotHighlightBackTexture() {
        return BundleTooltipComponent.BUNDLE_SLOT_HIGHLIGHT_BACK_TEXTURE;
    }

    /**
     * Gets the highlighted front texture from the original tooltip.
     *
     * @return  texture identifier for highlighted front texture
     */
    @Override
    public @NotNull Identifier getSlotHighlightFrontTexture() {
        return BundleTooltipComponent.BUNDLE_SLOT_HIGHLIGHT_FRONT_TEXTURE;
    }

    /**
     * Gets the progress bar label from the original tooltip.
     *
     * @return  text to overlay on the progress bar
     */
    @Override
    public @Nullable Text getProgressBarLabel() {
        return tooltipComponent.getProgressBarLabel();
    }
}
