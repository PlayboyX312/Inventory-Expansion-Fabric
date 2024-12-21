package derekahedron.invexp.gui.tooltip;

import derekahedron.invexp.network.packet.c2s.play.SetSelectedIndexC2SPacket;
import derekahedron.invexp.util.ContainerItemContents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.tooltip.TooltipSubmenuHandler;
import net.minecraft.client.input.Scroller;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

/**
 * Abstract class for submenu handlers that allow scrolling to change the
 * selected index of the container item.
 */
public abstract class ContainerItemTooltipSubmenuHandler implements TooltipSubmenuHandler {
    protected final MinecraftClient client;
    protected final Scroller scroller;

    /**
     * Creates a submenu handler for a container item that allows scrolling to change the selected index.
     *
     * @param client    client to create the handler from
     */
    public ContainerItemTooltipSubmenuHandler(MinecraftClient client) {
        this.client = client;
        this.scroller = new Scroller();
    }

    /**
     * Gets container item contents for the given item.
     *
     * @param stack     stack to get contents for
     * @return          container contents for the given stack
     */
    public abstract @Nullable ContainerItemContents contentsOf(@NotNull ItemStack stack);

    /**
     * Check if scrolling is allowed and run any pre-scrolling functionality.
     *
     * @param slotId    slot that is being scrolled
     * @param stack     stack in the slot
     * @param player    player that is scrolling
     * @return          if the selected index should change
     */
    public abstract boolean onScroll(
            int slotId, @NotNull ItemStack stack, @NotNull ClientPlayerEntity player
    );

    /**
     * Checks if this handler should work for the given stack.
     *
     * @param slot  slot that is being tested
     * @return      true if the submenu handler works here; false otherwise
     */
    @Override
    public boolean isApplicableTo(@NotNull Slot slot) {
        ContainerItemContents contents = contentsOf(slot.getStack());
        return contents != null && !contents.isEmpty();
    }

    /**
     * Handle scrolling by changing the selected index if applicable.
     *
     * @param horizontal    horizontal scroll distance
     * @param vertical      vertical scroll distance
     * @param slotId        slot id that was scrolled
     * @param stack         stack that was scrolled
     * @return              true if the scroll was handled; false otherwise
     */
    @Override
    public boolean onScroll(double horizontal, double vertical, int slotId, @NotNull ItemStack stack) {
        if (client.player == null) {
            return false;
        }

        // Fail if contents are invalid
        ContainerItemContents contents = contentsOf(stack);
        if (contents == null || contents.isEmpty()) {
            return false;
        }

        // Get amount scrolled. Use horizontal scroll if vertical is empty
        Vector2i scrollVector = scroller.update(horizontal, vertical);
        int numScrolled = scrollVector.y == 0 ? -scrollVector.x : scrollVector.y;
        if (numScrolled == 0) {
            return false;
        }

        // Scroll to the new index
        int newSelectedIndex = Scroller.scrollCycling(numScrolled, contents.getSelectedIndex(), contents.getStacks().size());
        if (newSelectedIndex == contents.getSelectedIndex()) {
            return false;
        }

        // Creative screens do not have slot ids that are synced, so we must find the corresponding slot
        // in the creative inventory
        if (client.player.currentScreenHandler instanceof CreativeInventoryScreen.CreativeScreenHandler) {
            slotId = -1;
            for (int i = 0; i < client.player.playerScreenHandler.slots.size(); i++) {
                if (client.player.playerScreenHandler.slots.get(i).getStack() == stack) {
                    slotId = i;
                    break;
                }
            }
            if (slotId == -1) {
                // Fail if slot was not found
                return false;
            }
        }

        // If you can scroll, set index and send packet to server
        if (onScroll(slotId, stack, client.player)) {
            contents.setSelectedIndex(newSelectedIndex);
            ClientPlayNetworking.send(new SetSelectedIndexC2SPacket(slotId, newSelectedIndex));
            return true;
        }
        return false;
    }

    /**
     * Nothing happens on reset
     */
    @Override
    public void reset(Slot slot) {

    }

    /**
     * Nothing happens on slot click
     */
    @Override
    public void onMouseClick(Slot slot, SlotActionType actionType) {

    }
}
