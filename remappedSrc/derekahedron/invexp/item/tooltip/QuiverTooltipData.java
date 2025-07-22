package derekahedron.invexp.item.tooltip;

import derekahedron.invexp.quiver.QuiverContents;
import net.minecraft.item.tooltip.TooltipData;

/**
 * Holds quiver tooltip data
 *
 * @param contents  Contents of the quiver
 */
public record QuiverTooltipData(QuiverContents contents) implements TooltipData {
}
