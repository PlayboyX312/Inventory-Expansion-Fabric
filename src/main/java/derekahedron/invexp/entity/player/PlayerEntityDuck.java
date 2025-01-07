package derekahedron.invexp.entity.player;

import derekahedron.invexp.sack.SackUsage;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

/**
 * Adds methods to the player entity. When a player starts using a stack,
 * usages are created for the main hand and offhand. If the stack in the main hand or
 * offhand change while using the sack, this will not cause issues as the usage is associated
 * more with the ItemStack than the hand.
 * When the player stops using the sack, changes made to the usage are applied to the sack stored in the
 * usage.
 */
public interface PlayerEntityDuck {

    /**
     * Check if player is currently using sack.
     *
     * @return  true if the player is using a sack; false otherwise
     */
    boolean invexp$isUsingSack();

    /**
     * Start player using sack.
     */
    void invexp$startUsingSack();

    /**
     * Stop player using sack.
     */
    void invexp$stopUsingSack();

    /**
     * Gets the usage for the player with a sack stack matching the given stack.
     *
     * @param sackStack     sack stack to get usage for
     * @return              sack usage associated with the sack stack; null if there is none
     */
    SackUsage invexp$getUsageForSackStack(ItemStack sackStack);

    /**
     * Gets the usage for the player with a selected stack matching the given stack.
     *
     * @param selectedStack     selected stack to get usage for
     * @return                  sack usage associated with the selected stack; null if there is none
     */
    SackUsage invexp$getUsageForSelectedStack(ItemStack selectedStack);
}
