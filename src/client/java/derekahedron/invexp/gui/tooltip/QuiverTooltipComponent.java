package derekahedron.invexp.gui.tooltip;

import derekahedron.invexp.quiver.QuiverContentsReader;
import derekahedron.invexp.util.InvExpUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.math.Fraction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Special tooltip for displaying quivers and their contents.
 *
 * @param contents  quiver contents to draw the tooltip from
 */
public record QuiverTooltipComponent(QuiverContentsReader contents) implements TooltipComponent, ContainerItemTooltipComponent {
    public static final Identifier QUIVER_PROGRESS_BAR_BORDER_TEXTURE = InvExpUtil.identifier("container/quiver/quiver_progressbar_border");
    public static final Identifier QUIVER_PROGRESS_BAR_FILL_TEXTURE = InvExpUtil.identifier("container/quiver/quiver_progressbar_fill");
    public static final Identifier QUIVER_PROGRESS_BAR_FULL_TEXTURE = InvExpUtil.identifier("container/quiver/quiver_progressbar_full");
    public static final Identifier QUIVER_SLOT_HIGHLIGHT_BACK_TEXTURE = InvExpUtil.identifier("container/quiver/slot_highlight_back");
    public static final Identifier QUIVER_SLOT_HIGHLIGHT_FRONT_TEXTURE = InvExpUtil.identifier("container/quiver/slot_highlight_front");
    public static final Identifier QUIVER_SLOT_BACKGROUND_TEXTURE = InvExpUtil.identifier("container/quiver/slot_background");
    public static final Text QUIVER_FULL = Text.translatable("item.invexp.quiver.full");
    public static final Text QUIVER_EMPTY = Text.translatable("item.invexp.quiver.empty");
    public static final Text QUIVER_TOO_MANY_STACKS = Text.translatable("item.invexp.quiver.too_many_stacks");
    public static final Text QUIVER_EMPTY_DESCRIPTION = Text.translatable("item.invexp.quiver.description.empty");
    public static final String QUIVER_EMPTY_DESCRIPTION_PLURAL = "item.invexp.quiver.description.empty.plural";

    /**
     * Draws the quiver tooltip by first drawing either the contents or an empty description,
     * then finally drawing a progress bar.
     *
     * @param textRenderer  text renderer
     * @param x             x position to draw tooltip at
     * @param y             y position to draw tooltip at
     * @param width         width of the entire tooltip
     * @param height        height of the entire tooltip
     * @param drawContext   draw context
     */
    @Override
    public void drawItems(
            @NotNull TextRenderer textRenderer, int x, int y, int width, int height, @NotNull DrawContext drawContext
    ) {
        int top = y;
        if (contents.isEmpty()) {
            // Draw empty description
            Text quiverEmptyDescription = getQuiverEmptyDescription();
            drawContext.drawWrappedTextWithShadow(
                    textRenderer, quiverEmptyDescription, x + getXMargin(width), y,
                    getTooltipWidth(), DESCRIPTION_TEXT_COLOR
            );
            y += getHeight(quiverEmptyDescription, textRenderer);
        } else {
            // Draw contents
            drawContents(textRenderer, x, y, width, top, drawContext);
            y += getContentsHeight();
        }
        // Finally draw progress bar
        y += getProgressBarPadding();
        drawProgressBar(textRenderer, x, y, width, drawContext);
        // y += getProgressBarPadding();
    }

    /**
     * Gets the width this tooltip requires.
     *
     * @param textRenderer  text renderer
     * @return              width the tooltip takes up
     */
    @Override
    public int getWidth(@Nullable TextRenderer textRenderer) {
        return getTooltipWidth();
    }

    /**
     * Gets the height this tooltip requires
     *
     * @param textRenderer  text renderer
     * @return              height the tooltip takes up
     */
    @Override
    public int getHeight(@NotNull TextRenderer textRenderer) {
        int height = 0;
        if (contents.isEmpty()) {
            height += getHeight(getQuiverEmptyDescription(), textRenderer);
        } else {
            height += getContentsHeight();
        }
        height += getProgressBarPadding();
        height += getProgressBarHeight();
        height += getProgressBarPadding();
        return height;
    }

    /**
     * Ensures the quiver tooltip renders even if the cursor stack has another item.
     */
    @Override
    public boolean isSticky() {
        return true;
    }

    /**
     * Generates description for an empty quiver.
     *
     * @return  empty quiver description text
     */
    public @NotNull Text getQuiverEmptyDescription() {
        Fraction maxStacks = contents.getMaxQuiverOccupancy();
        if (!maxStacks.equals(Fraction.ONE)) {
            return Text.translatable(QUIVER_EMPTY_DESCRIPTION_PLURAL, maxStacks.toProperString());
        } else {
            return QUIVER_EMPTY_DESCRIPTION;
        }
    }

    /**
     * @return  list of stacks from the contents
     */
    @Override
    public @NotNull List<ItemStack> getStacks() {
        return contents.getStacks();
    }

    /**
     * @return  selected index of the contents
     */
    @Override
    public int getSelectedIndex() {
        return contents.getSelectedIndex();
    }

    /**
     * @return  fullness fraction of the contents
     */
    @Override
    public @NotNull Fraction getFillFraction() {
        return contents.getFillFraction();
    }

    /**
     * Gets a different progress bar texture depending on if the contents are full.
     *
     * @return  texture identifier for the progress bar
     */
    @Override
    public @NotNull Identifier getProgressBarFillTexture() {
        if (contents.isFull()) {
            return QUIVER_PROGRESS_BAR_FULL_TEXTURE;
        } else {
            return QUIVER_PROGRESS_BAR_FILL_TEXTURE;
        }
    }

    /**
     * @return  texture identifier for the progress bar border
     */
    @Override
    public @NotNull Identifier getProgressBarBorderTexture() {
        return QUIVER_PROGRESS_BAR_BORDER_TEXTURE;
    }

    /**
     * @return  texture identifier for the slot background texture
     */
    @Override
    public @NotNull Identifier getSlotBackgroundTexture() {
        return QUIVER_SLOT_BACKGROUND_TEXTURE;
    }

    /**
     * @return  texture identifier for highlighted back texture
     */
    @Override
    public @NotNull Identifier getSlotHighlightBackTexture() {
        return QUIVER_SLOT_HIGHLIGHT_BACK_TEXTURE;
    }

    /**
     * @return  texture identifier for highlighted front texture
     */
    @Override
    public @NotNull Identifier getSlotHighlightFrontTexture() {
        return QUIVER_SLOT_HIGHLIGHT_FRONT_TEXTURE;
    }

    /**
     * Generate text to display on the progress bar. Either FULL, EMPTY,
     * TOO MANY STACKS, or nothing.
     *
     * @return  text to overlay on the progress bar
     */
    @Override
    public @Nullable Text getProgressBarLabel() {
        if (contents.getTotalOccupancy().compareTo(contents.getMaxQuiverOccupancy()) >= 0) {
            return QUIVER_FULL;
        }
        else if (getStacks().size() >= contents.getMaxQuiverStacks()) {
            return QUIVER_TOO_MANY_STACKS;
        }
        else if (contents.isEmpty()) {
            return QUIVER_EMPTY;
        }
        else {
            return null;
        }
    }
}
