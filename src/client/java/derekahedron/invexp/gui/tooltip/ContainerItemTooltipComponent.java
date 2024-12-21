package derekahedron.invexp.gui.tooltip;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.math.Fraction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Adds functions for rendering container item tooltips. Most of the default values
 * can be modified by overriding functions. You can do this to allow as many rows as you wish
 * as well as increase items in a row. This will probably never be used as the default values work well.
 */
public interface ContainerItemTooltipComponent {
    int DEFAULT_MAX_ROWS = 3;
    int MIN_MAX_ROWS = 3; // Hard coded minimum. If less rows are displayed, odd behavior may occur
    int DEFAULT_SLOT_LENGTH = 24;
    int DEFAULT_ROW_WIDTH = 4;
    int DEFAULT_PROGRESS_BAR_PADDING = 4;
    int DEFAULT_PROGRESS_BAR_HEIGHT = 13;
    int EXTRA_ITEMS_TEXT_COLOR = 0xFFFFFF;
    int PROGRESS_BAR_TEXT_COLOR = 0xFFFFFF;
    int DESCRIPTION_TEXT_COLOR = 0xAAAAAA;

    /**
     * @return  width that the tooltip takes up
     */
    default int getTooltipWidth() {
        return getRowWidth() * getSlotLength();
    }

    /**
     * @return  max rows that can be displayed at a time
     */
    default int getMaxRows() {
        return DEFAULT_MAX_ROWS;
    }

    /**
     * @return  max items that are displayed in a row
     */
    default int getRowWidth() {
        return DEFAULT_ROW_WIDTH;
    }

    /**
     * @return  length in pixels that an item slot takes up
     */
    default int getSlotLength() {
        return DEFAULT_SLOT_LENGTH;
    }

    /**
     * @return  height of the progress bar in pixels
     */
    default int getProgressBarHeight() {
        return DEFAULT_PROGRESS_BAR_HEIGHT;
    }

    /**
     * @return  get padding in pixels surrounding the progress bar
     */
    default int getProgressBarPadding() {
        return DEFAULT_PROGRESS_BAR_PADDING;
    }

    /**
     * Get list of stacks to display
     *
     * @return  list of stacks to display
     */
    @NotNull List<ItemStack> getStacks();

    /**
     * Gets the index of the selected stack
     *
     * @return  selected index; or -1 if there is none
     */
    int getSelectedIndex();

    /**
     * Gets the fullness fraction for how full the container is
     *
     * @return  fraction depicting how full the progress bar should be
     */
    @NotNull Fraction getFillFraction();

    /**
     * Gets the progress bar texture.
     *
     * @return  texture identifier for the progress bar
     */
    @NotNull Identifier getProgressBarFillTexture();

    /**
     * Gets the progress bar border texture.
     *
     * @return  texture identifier for the progress bar border
     */
    @NotNull Identifier getProgressBarBorderTexture();

    /**
     * Gets the slot background texture.
     *
     * @return  texture identifier for the slot background texture
     */
    @NotNull Identifier getSlotBackgroundTexture();

    /**
     * Gets the slot highlight back texture.
     *
     * @return  texture identifier for highlighted back texture
     */
    @NotNull Identifier getSlotHighlightBackTexture();

    /**
     * Gets the slot highlight front texture.
     *
     * @return  texture identifier for highlighted front texture
     */
    @NotNull Identifier getSlotHighlightFrontTexture();

    /**
     * Generates text to display over the progress bar.
     *
     * @return  text to overlay on the progress bar; can be null
     */
    @Nullable Text getProgressBarLabel();

    /**
     * Render contents of the container. Display the row with the selected item
     * and the surrounding rows. If there are items past the rows that are displayed,
     * render the amount of items at the start or end of the bottom/top row.
     *
     * @param textRenderer  text renderer
     * @param x             x position to draw contents at
     * @param y             y position to draw contents at
     * @param width         width of the entire tooltip; can be more than the default width
     * @param top           y position of the top of the tooltip
     * @param drawContext   draw context
     */
    default void drawContents(TextRenderer textRenderer, int x, int y, int width, int top, DrawContext drawContext) {
        // Calculate row values
        int numRows = getNumRows();
        int maxRows = Math.max(getMaxRows(), MIN_MAX_ROWS);
        int prevVisibleRows = maxRows / 2;
        int nextVisibleRows = (maxRows - 1) / 2;

        // Get selected stack values
        int selectedIndex = getSelectedIndex();
        ItemStack selectedStack;
        if (selectedIndex != -1) {
            selectedStack = getStacks().get(selectedIndex);
        }
        else {
            selectedStack = null;
            selectedIndex = 0;
        }
        int selectedRow = getRowForIndex(selectedIndex);

        // Clamp selected row to display the most rows possible when at the beginning or end of contents
        if (selectedRow < prevVisibleRows) {
            selectedRow = prevVisibleRows;
        }
        else if ((numRows - selectedRow - 1) < nextVisibleRows) {
            selectedRow = numRows - nextVisibleRows - 1;
        }

        // Create list to stores stacks that should be displayed
        List<ItemStack> displayedStacks = new ArrayList<>(maxRows * getRowWidth());
        int seedStart = 0;  // Count how many items come before displayedStacks to calculate seed
        int countPrev = 0;
        int countNext = 0;
        // Run through all stacks to either add their count or add them to be displayed
        for (int i = 0; i < getStacks().size(); i++) {
            int row = getRowForIndex(i);
            ItemStack stack = getStacks().get(i);
            if (row < selectedRow - prevVisibleRows) {
                seedStart++;
                countPrev += stack.getCount();
            }
            else if (row > selectedRow + nextVisibleRows) {
                countNext += stack.getCount();
            }
            else {
                displayedStacks.add(stack);
            }
        }

        // Get ends of display area
        int endX = x + getXMargin(width) + getTooltipWidth();
        int endY = y + Math.min(numRows, maxRows) * getSlotLength();

        // Calculate padding surrounding items
        int itemPadding = (getSlotLength() - 16) / 2;

        for (int i = 0; i < displayedStacks.size(); i++) {
            // iterate through list backwards to account for empty space at the beginning
            ItemStack stack = displayedStacks.get(displayedStacks.size() - 1 - i);
            int slotX = endX - (1 + i % getRowWidth()) * getSlotLength();
            int slotY = endY - (1 + i / getRowWidth()) * getSlotLength();
            if (i == displayedStacks.size() - 1 && countPrev > 0) {
                // If there are previous items, display the count instead
                countPrev += stack.getCount();
                drawContext.drawCenteredTextWithShadow(
                        textRenderer, "+" + countPrev,
                        slotX + getSlotLength() / 2, slotY + itemPadding + 6, EXTRA_ITEMS_TEXT_COLOR
                );
            }
            else if (i == 0 && countNext > 0) {
                // If there are next items, display the count instead
                countNext += stack.getCount();
                drawContext.drawCenteredTextWithShadow(
                        textRenderer, "+" + countNext,
                        slotX + getSlotLength() / 2, slotY + itemPadding + 6, EXTRA_ITEMS_TEXT_COLOR
                );
            }
            else {
                // Render background. If stack is selected, render a different background
                if (stack == selectedStack) {
                    drawContext.drawGuiTexture(
                            RenderLayer::getGuiTextured, getSlotHighlightBackTexture(),
                            slotX, slotY, getSlotLength(), getSlotLength()
                    );
                }
                else {
                    drawContext.drawGuiTexture(
                            RenderLayer::getGuiTextured, getSlotBackgroundTexture(),
                            slotX, slotY, getSlotLength(), getSlotLength()
                    );
                }
                drawContext.drawItem(
                        stack, slotX + itemPadding, slotY + itemPadding,
                        seedStart + displayedStacks.size() - 1 - i
                );
                drawContext.drawStackOverlay(textRenderer, stack, slotX + itemPadding, slotY + itemPadding);
                // If stack is selected, render a highlight
                if (stack == selectedStack) {
                    drawContext.drawGuiTexture(
                            RenderLayer::getGuiTextured, getSlotHighlightFrontTexture(), slotX, slotY,
                            getSlotLength(), getSlotLength()
                    );
                }
            }
        }

        // Add selected tooltip at the top if there is a selected stack
        if (selectedStack != null) {
            Text name = selectedStack.getFormattedName();
            int nameX = x + (width - textRenderer.getWidth(name.asOrderedText())) / 2 - 12;
            int nameY = top - 15;
            drawContext.drawTooltip(
                    textRenderer, name, nameX, nameY, selectedStack.get(DataComponentTypes.TOOLTIP_STYLE)
            );
        }
    }

    /**
     * Gets the row index for a given index of an item in the contents.
     * Does account for the initial empty spaces at the start of the first row.
     *
     * @param index     index of the item in the contents
     * @return          row index of the item
     */
    default int getRowForIndex(int index) {
        int emptySpaces = getRowWidth() - (1 + (getStacks().size() - 1) % getRowWidth());
        return (index + emptySpaces) / getRowWidth();
    }

    /**
     * Gets the number of rows that the total contents use.
     *
     * @return  total number of rows in the contents
     */
    default int getNumRows() {
        return 1 + (getStacks().size() - 1) / getRowWidth();
    }

    /**
     * Calculates the length in pixels that the progress bar should take up.
     *
     * @return  length in pixels of the progress bar
     */
    default int getProgressBarFill() {
        int progressBarWidth = getTooltipWidth() - 2;
        return MathHelper.clamp(MathHelper.multiplyFraction(getFillFraction(), progressBarWidth), 0, progressBarWidth);
    }

    /**
     * Draws the fullness progress bar. Does not account for the padding surrounding the progress bar.
     *
     * @param textRenderer  text renderer
     * @param x             x position to draw progress bar at
     * @param y             y position to draw progress bar at
     * @param width         width of the entire tooltip; can be more than the default width
     * @param drawContext   draw context
     */
    default void drawProgressBar(
            @NotNull TextRenderer textRenderer, int x, int y, int width, @NotNull DrawContext drawContext
    ) {
        x += getXMargin(width);
        // Draw bar and border
        drawContext.drawGuiTexture(
                RenderLayer::getGuiTextured, getProgressBarFillTexture(), x + 1, y,
                getProgressBarFill(), getProgressBarHeight()
        );
        drawContext.drawGuiTexture(
                RenderLayer::getGuiTextured, getProgressBarBorderTexture(), x, y,
                getTooltipWidth(), getProgressBarHeight()
        );
        // If there is a label, draw it centered on the progress bar
        Text progressBarLabel = getProgressBarLabel();
        if (progressBarLabel != null) {
            drawContext.drawCenteredTextWithShadow(
                    textRenderer, progressBarLabel, x + getTooltipWidth() / 2,
                    y + (getProgressBarHeight() - (textRenderer.fontHeight - 2)) / 2, PROGRESS_BAR_TEXT_COLOR
            );
        }
    }

    /**
     * Calculates the height of the contents.
     *
     * @return  height in pixels that the contents take up
     */
    default int getContentsHeight() {
        return Math.min(getNumRows(), Math.max(getMaxRows(), MIN_MAX_ROWS)) * getSlotLength();
    }

    /**
     * Calculates how much x margin is needed to center the tooltip, given that the width
     * of the entire tooltip is greater than the width of the container tooltip.
     *
     * @param width     width of the entire tooltip; can be more than the default width
     * @return          horizontal margin needed to center the tooltip
     */
    default int getXMargin(int width) {
        return (width - getTooltipWidth()) / 2;
    }

    /**
     * Calculates the height that the given text takes up.
     *
     * @param text          text to get the height for
     * @param textRenderer  text renderer
     * @return              height in pixels that the given text takes up
     */
    default int getHeight(@Nullable Text text, @NotNull TextRenderer textRenderer) {
        if (text == null) {
            return 0;
        }
        return textRenderer.wrapLines(text, getTooltipWidth()).size() * textRenderer.fontHeight;
    }
}
