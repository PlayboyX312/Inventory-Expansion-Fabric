package derekahedron.invexp.block.dispenser;

import derekahedron.invexp.item.InvExpItems;
import net.minecraft.block.DispenserBlock;

/**
 * Initializer for dispenser behaviors
 */
public class InvExpDispenserBehavior {

    /**
     * Adds dispenser behaviors for modded items
     */
    public static void initialize() {
        DispenserBlock.registerBehavior(InvExpItems.SACK, new SackDispenserBehavior());
        DispenserBlock.registerBehavior(InvExpItems.QUIVER, new QuiverDispenserBehavior());
    }
}
