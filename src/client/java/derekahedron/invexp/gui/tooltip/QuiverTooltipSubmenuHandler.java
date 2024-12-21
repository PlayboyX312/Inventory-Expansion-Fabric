package derekahedron.invexp.gui.tooltip;

import derekahedron.invexp.quiver.QuiverContents;
import derekahedron.invexp.util.ContainerItemContents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Quiver Submenu handler for handling scrolling on quivers.
 */
public class QuiverTooltipSubmenuHandler extends ContainerItemTooltipSubmenuHandler {

    /**
     * Create a new Quiver Submenu handler.
     *
     * @param client    Client to create handler from
     */
    public QuiverTooltipSubmenuHandler(MinecraftClient client) {
        super(client);
    }

    /**
     * Gets quiver contents for the given item.
     *
     * @param stack     stack to get contents for
     * @return          quiver contents for the given stack
     */
    @Override
    public @Nullable ContainerItemContents contentsOf(@NotNull ItemStack stack) {
        return QuiverContents.of(stack);
    }

    /**
     * Always allow scrolling for quivers.
     *
     * @param slotId    slot that is being scrolled
     * @param stack     stack in the slot
     * @param player    player that is scrolling
     * @return          if the selected index should change
     */
    @Override
    public boolean onScroll(int slotId, @NotNull ItemStack stack, @NotNull ClientPlayerEntity player) {
        // Quivers have no special action on scroll
        return true;
    }
}
