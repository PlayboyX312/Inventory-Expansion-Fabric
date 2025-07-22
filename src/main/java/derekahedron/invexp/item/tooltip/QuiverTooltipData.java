package derekahedron.invexp.item.tooltip;

import derekahedron.invexp.quiver.QuiverContentsReader;
import net.minecraft.item.tooltip.TooltipData;

/**
 * Holds quiver tooltip data.
 *
 * @param contents Contents of the quiver
 */
public record QuiverTooltipData(QuiverContentsReader contents) implements TooltipData {
}
