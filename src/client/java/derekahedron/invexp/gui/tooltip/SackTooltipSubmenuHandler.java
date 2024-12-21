package derekahedron.invexp.gui.tooltip;

import derekahedron.invexp.sack.SackContents;
import derekahedron.invexp.util.ContainerItemContents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Sack Submenu handler for handling scrolling on sacks.
 */
public class SackTooltipSubmenuHandler extends ContainerItemTooltipSubmenuHandler {

    /**
     * Create a new Sack Submenu handler.
     *
     * @param client    Client to create handler from
     */
    public SackTooltipSubmenuHandler(MinecraftClient client) {
        super(client);
    }

    /**
     * Gets sack contents for the given item.
     *
     * @param stack     stack to get contents for
     * @return          sack contents for the given stack
     */
    @Override
    public @Nullable ContainerItemContents contentsOf(@NotNull ItemStack stack) {
        return SackContents.of(stack);
    }

    /**
     * Only allow scrolling on dynamic display slots and animates swapping selected item.
     *
     * @param slotId    slot that is being scrolled
     * @param stack     stack in the slot
     * @param player    player that is scrolling
     * @return          if the selected index should change
     */
    @Override
    public boolean onScroll(
            int slotId, @NotNull ItemStack stack, @NotNull ClientPlayerEntity player
    ) {
        // Do not allow scrolling on slots without dynamic display.
        if (player.currentScreenHandler.getSlot(slotId).disablesDynamicDisplay()) {
            return false;
        }

        // Animate equip progress when changing stack
        if (player.getMainHandStack() == stack) {
            client.gameRenderer.firstPersonRenderer.resetEquipProgress(Hand.MAIN_HAND);
        }
        else if (player.getOffHandStack() == stack) {
            client.gameRenderer.firstPersonRenderer.resetEquipProgress(Hand.OFF_HAND);
        }

        return true;
    }
}
