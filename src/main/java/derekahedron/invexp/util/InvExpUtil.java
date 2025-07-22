package derekahedron.invexp.util;

import derekahedron.invexp.InventoryExpansion;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * Contains utility function for Inventory Expansion
 */
public class InvExpUtil {

    /**
     * Creates a new identifier under the Inventory Expansion namespace
     *
     * @param id    String to create the identifier for
     * @return      Identifier under the Inventory Expansion namespace
     */
    public static @NotNull Identifier identifier(@NotNull String id) {
        return Identifier.of(InventoryExpansion.MOD_ID, id);
    }

    /**
     * Signal to players screen handler that content was changed
     *
     * @param player    player whose inventory changed
     */
    public static void onContentChanged(@NotNull PlayerEntity player) {
        ScreenHandler screenHandler = player.currentScreenHandler;
        if (screenHandler != null) {
            screenHandler.onContentChanged(player.getInventory());
        }
    }
}
