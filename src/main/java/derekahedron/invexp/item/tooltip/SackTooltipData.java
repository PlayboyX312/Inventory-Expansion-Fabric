package derekahedron.invexp.item.tooltip;

import derekahedron.invexp.sack.SackContentsReader;
import net.minecraft.item.tooltip.TooltipData;

/**
 * Holds sack tooltip data.
 *
 * @param contents Contents of the sack
 */
public record SackTooltipData(SackContentsReader contents) implements TooltipData {
}
