package derekahedron.invexp.server.network;

import derekahedron.invexp.InventoryExpansion;
import derekahedron.invexp.network.packet.c2s.play.SetSelectedIndexC2SPacket;
import derekahedron.invexp.util.ContainerItemContents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Stores callback functions for C2S payloads
 */
public class ServerNetworkHandlers {

    /**
     * Handles set index C2S payload.
     * Sets the selected index of the given container item.
     *
     * @param payload   payload data of the request
     * @param context   context of how the request was sent
     */
    public static void onSetSelectedIndex(
            @NotNull SetSelectedIndexC2SPacket payload, ServerPlayNetworking.@NotNull Context context
    ) {
        ScreenHandler handler = context.player().currentScreenHandler;
        // Make sure the given slot id is valid
        int slotId = payload.slotId();
        if (slotId < 0 || slotId >= handler.slots.size()) {
            InventoryExpansion.LOGGER.debug(
                    "Player {} set selected index of invalid slot id {}", context.player(), slotId
            );
            return;
        }

        // Makes sure the sack is valid
        ItemStack stack = handler.slots.get(slotId).getStack();
        ContainerItemContents contents = ContainerItemContents.of(stack);
        if (contents == null) {
            InventoryExpansion.LOGGER.debug(
                    "Player {} set selected index of invalid stack {}", context.player(), stack
            );
            return;
        }

        contents.setSelectedIndex(payload.selectedIndex());
        handler.onContentChanged(context.player().getInventory());
    }

    /**
     * Register handlers for C2S packets
     */
    public static void initialize() {
        ServerPlayNetworking.registerGlobalReceiver(
                SetSelectedIndexC2SPacket.ID,
                ServerNetworkHandlers::onSetSelectedIndex
        );
    }
}
