package derekahedron.invexp.block.cauldron;

import derekahedron.invexp.item.InvExpItems;
import net.minecraft.block.cauldron.CauldronBehavior;

/**
 * Initializer for cauldron behaviors
 */
public class InvExpCauldronBehavior {

    /**
     * Adds cauldron behaviors for modded items
     */
    public static void initialize() {
        CauldronBehavior.WATER_CAULDRON_BEHAVIOR.map().put(InvExpItems.SACK, CauldronBehavior::cleanArmor);
    }
}
