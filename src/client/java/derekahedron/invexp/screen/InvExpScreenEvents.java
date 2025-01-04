package derekahedron.invexp.screen;

import derekahedron.invexp.network.packet.c2s.play.SetSelectedIndexC2SPacket;
import derekahedron.invexp.sack.SackContents;
import derekahedron.invexp.util.ContainerItemContents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.input.Scroller;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Hand;
import org.joml.Vector2i;

/**
 * Sets up Inventory Expansion screen event handlers
 */
public class InvExpScreenEvents {
    public static final Scroller scroller = new Scroller();

    /**
     * Handles scrolling over a container item to change the selected index.
     * Returns true if the index was not changed, meaning the scroll event is not cancelled.
     *
     * @param screen        screen scrolled on
     * @param mouseX        x position of the mouse
     * @param mouseY        y position of the mouse
     * @param horizontal    how much was scrolled horizontally
     * @param vertical      how much was scrolled vertically
     * @return              false if scrolling changed selected index; true otherwise
     */
    public static boolean scrollContainerItem(
            Screen screen, double mouseX, double mouseY, double horizontal, double vertical
    ) {
        // Ensure screen is a handled screen and the player is not null
        if (!(screen instanceof HandledScreen<?> handledScreen) ||
                handledScreen.client == null || handledScreen.client.player == null) {
            return true;
        }

        Slot slot = handledScreen.getSlotAt(mouseX, mouseY);
        if (slot == null || !slot.hasStack() || slot.disablesDynamicDisplay()) {
            return true;
        }
        ItemStack stack = slot.getStack();

        // Fail if contents are invalid
        ContainerItemContents contents = ContainerItemContents.of(stack);
        if (contents == null || contents.isEmpty()) {
            return true;
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
        if (handledScreen.client.player.currentScreenHandler instanceof CreativeInventoryScreen.CreativeScreenHandler) {
            slot = null;
            for (Slot s : handledScreen.client.player.playerScreenHandler.slots) {
                if (s.getStack() == stack) {
                    slot = s;
                    break;
                }
            }
            if (slot == null) {
                // Fail if slot was not found
                return true;
            }
        }

        // If you can scroll, set index and send packet to server
        if (contents instanceof SackContents) {
            // Animate equip progress when changing stack
            if (handledScreen.client.player.getMainHandStack() == stack) {
                handledScreen.client.gameRenderer.firstPersonRenderer.resetEquipProgress(Hand.MAIN_HAND);
            }
            else if (handledScreen.client.player.getOffHandStack() == stack) {
                handledScreen.client.gameRenderer.firstPersonRenderer.resetEquipProgress(Hand.OFF_HAND);
            }
        }
        contents.setSelectedIndex(newSelectedIndex);
        ClientPlayNetworking.send(new SetSelectedIndexC2SPacket(slot.id, newSelectedIndex));
        return false;
    }

    /**
     * Register Inventory Expansion screen event handlers
     */
    public static void initialize() {
        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) ->
                ScreenMouseEvents.allowMouseScroll(screen).register(InvExpScreenEvents::scrollContainerItem));
    }
}
