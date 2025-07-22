package derekahedron.invexp.gui.tooltip;

import derekahedron.invexp.sack.SackContentsReader;
import derekahedron.invexp.util.InvExpUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.math.Fraction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * Special tooltip for displaying sacks and their contents.
 *
 * @param contents  sack contents to draw the tooltip from
 */
public record SackTooltipComponent(SackContentsReader contents) implements TooltipComponent, ContainerItemTooltipComponent {
    public static final Identifier SACK_PROGRESS_BAR_BORDER_TEXTURE = InvExpUtil.identifier("container/sack/sack_progressbar_border");
    public static final Identifier SACK_PROGRESS_BAR_FILL_TEXTURE = InvExpUtil.identifier("container/sack/sack_progressbar_fill");
    public static final Identifier SACK_PROGRESS_BAR_FULL_TEXTURE = InvExpUtil.identifier("container/sack/sack_progressbar_full");
    public static final Identifier SACK_SLOT_HIGHLIGHT_BACK_TEXTURE = InvExpUtil.identifier("container/sack/slot_highlight_back");
    public static final Identifier SACK_SLOT_HIGHLIGHT_FRONT_TEXTURE = InvExpUtil.identifier("container/sack/slot_highlight_front");
    public static final Identifier SACK_SLOT_BACKGROUND_TEXTURE = InvExpUtil.identifier("container/sack/slot_background");
    public static final Text SACK_FULL = Text.translatable("item.invexp.sack.full");
    public static final Text SACK_EMPTY = Text.translatable("item.invexp.sack.empty");
    public static final Text SACK_TOO_MANY_STACKS = Text.translatable("item.invexp.sack.too_many_stacks");
    public static final String SACK_PARTIAL = "item.invexp.sack.partial";
    public static final Text SACK_EMPTY_DESCRIPTION = Text.translatable("item.invexp.sack.description.empty");
    public static final String SACK_EMPTY_DESCRIPTION_PLURAL = "item.invexp.sack.description.empty.plural";
    public static final String SACK_TYPE_DESCRIPTION = "item.invexp.sack.description.type";
    public static final String SACK_TYPE_DESCRIPTION_CONJUNCTION = "item.invexp.sack.description.type.conjunction";
    public static final String SACK_TYPE_DESCRIPTION_MANY_CONJUNCTION = "item.invexp.sack.description.type.many.conjunction";
    public static final String SACK_TYPE_DESCRIPTION_MANY_LAST_CONJUNCTION = "item.invexp.sack.description.type.many.last_conjunction";

    /**
     * Draws the sack tooltip by first drawing a description, then the contents if there are any,
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
    public void drawItems(@NotNull TextRenderer textRenderer, int x, int y, int width, int height, @NotNull DrawContext drawContext) {
        int top = y;  // Store top
        if (contents.isEmpty()) {
            // Draw empty description
            Text sackEmptyDescription = getSackEmptyDescription();
            drawContext.drawWrappedTextWithShadow(
                    textRenderer, sackEmptyDescription, x + getXMargin(width), y,
                    getTooltipWidth(), DESCRIPTION_TEXT_COLOR
            );
            y += getHeight(sackEmptyDescription, textRenderer);
        } else {
            // Draw sack type description and contents
            Text sackTypeDescription = getSackTypeDescription();
            if (sackTypeDescription != null) {
                drawContext.drawWrappedTextWithShadow(
                        textRenderer, sackTypeDescription, x + getXMargin(width), y,
                        getTooltipWidth(), DESCRIPTION_TEXT_COLOR
                );
                y += getHeight(sackTypeDescription, textRenderer);
            }
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
            height += getHeight(getSackEmptyDescription(), textRenderer);
        } else {
            height += getHeight(getSackTypeDescription(), textRenderer);
            height += getContentsHeight();
        }
        height += getProgressBarPadding();
        height += getProgressBarHeight();
        height += getProgressBarPadding();
        return height;
    }

    /**
     * Ensures the sack tooltip renders even if the cursor stack has another item.
     */
    @Override
    public boolean isSticky() {
        return true;
    }

    /**
     * Generates description for an empty sack.
     *
     * @return  empty sack description text
     */
    public @NotNull Text getSackEmptyDescription() {
        Fraction maxWeight = contents.getMaxSackWeight();
        if (!maxWeight.equals(Fraction.ONE)) {
            return Text.translatable(SACK_EMPTY_DESCRIPTION_PLURAL, maxWeight.toProperString());
        } else {
            return SACK_EMPTY_DESCRIPTION;
        }
    }

    /**
     * Generates description to display all the sack types that are used.
     *
     * @return  sack type description text
     */
    public @Nullable Text getSackTypeDescription() {
        Text description = null;
        // Get list of all names
        List<? extends Text> names = contents.getSackTypes().stream()
                .map(RegistryEntry::getKey)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(RegistryKey::getValue)
                .map(id -> Text.translatable("sack_type." + id.getNamespace() + "." + id.getPath()))
                .toList();

        for (int i = 0; i < names.size(); i++) {
            Text name = names.get(i);
            // If first entry, set description to name
            if (description == null) {
                description = name;
            }
            // If not last entry, add conjunction "original_list, new_entry"
            else if (i != names.size() - 1) {
                description = Text.translatable(SACK_TYPE_DESCRIPTION_MANY_CONJUNCTION, description, name);
            }
            // If last entry for list of size 2, add conjunction "original_list and new_entry"
            else if (names.size() == 2) {
                description = Text.translatable(SACK_TYPE_DESCRIPTION_CONJUNCTION, description, name);
            }
            // If last entry for list of size > 2, add conjunction "original_list, and new_entry"
            else {
                description = Text.translatable(SACK_TYPE_DESCRIPTION_MANY_LAST_CONJUNCTION, description, name);
            }
        }
        // If a description was made, prepend "Sack of " to names
        if (description != null) {
            description = Text.translatable(SACK_TYPE_DESCRIPTION, description);
        }
        return description;
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
            return SACK_PROGRESS_BAR_FULL_TEXTURE;
        } else {
            return SACK_PROGRESS_BAR_FILL_TEXTURE;
        }
    }

    /**
     * @return  texture identifier for the progress bar border
     */
    @Override
    public @NotNull Identifier getProgressBarBorderTexture() {
        return SACK_PROGRESS_BAR_BORDER_TEXTURE;
    }

    /**
     * @return  texture identifier for the slot background texture
     */
    @Override
    public @NotNull Identifier getSlotBackgroundTexture() {
        return SACK_SLOT_BACKGROUND_TEXTURE;
    }

    /**
     * @return  texture identifier for highlighted back texture
     */
    @Override
    public @NotNull Identifier getSlotHighlightBackTexture() {
        return SACK_SLOT_HIGHLIGHT_BACK_TEXTURE;
    }

    /**
     * @return  texture identifier for highlighted front texture
     */
    @Override
    public @NotNull Identifier getSlotHighlightFrontTexture() {
        return SACK_SLOT_HIGHLIGHT_FRONT_TEXTURE;
    }

    /**
     * Generate text to display on the progress bar. Either FULL, EMPTY,
     * TOO MANY STACKS, or a fraction of the total weight out of max weight.
     *
     * @return  text to overlay on the progress bar
     */
    @Override
    public @NotNull Text getProgressBarLabel() {
        if (contents.getTotalWeight().compareTo(contents.getMaxSackWeight()) >= 0) {
            return SACK_FULL;
        } else if (getStacks().size() >= contents.getMaxSackStacks()) {
            return SACK_TOO_MANY_STACKS;
        } else if (contents.isEmpty()) {
            return SACK_EMPTY;
        } else {
            return Text.translatable(SACK_PARTIAL, formatWeight(contents.getTotalWeight()), formatWeight(contents.getMaxSackWeight()));
        }
    }

    /**
     * Formats sack weight for display. The weight is divided by the default sack weight.
     * If the resulting value is a fraction, format to two decimal places.
     *
     * @param weight    sack weight to format
     * @return          formatted text representing the sack weight
     */
    public static @NotNull MutableText formatWeight(Fraction weight) {
        weight = weight.multiplyBy(Fraction.getFraction(64));
        if (weight.getNumerator() % weight.getDenominator() == 0) {
            return Text.literal(String.valueOf(weight.intValue()));
        } else {
            return Text.literal(String.format("%.2f", weight.floatValue()));
        }
    }
}
