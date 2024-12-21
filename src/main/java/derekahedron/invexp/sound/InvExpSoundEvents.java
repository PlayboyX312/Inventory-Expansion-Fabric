package derekahedron.invexp.sound;

import derekahedron.invexp.util.InvExpUtil;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

/**
 * Stores all Sound Events for Inventory Expansion
 */
public class InvExpSoundEvents {
    public static final SoundEvent ITEM_SACK_INSERT;
    public static final SoundEvent ITEM_SACK_INSERT_FAIL;
    public static final SoundEvent ITEM_SACK_REMOVE_ONE;
    public static final SoundEvent ITEM_QUIVER_INSERT;
    public static final SoundEvent ITEM_QUIVER_INSERT_FAIL;
    public static final SoundEvent ITEM_QUIVER_REMOVE_ONE;

    static {
        ITEM_SACK_INSERT = register("item.sack.insert");
        ITEM_SACK_INSERT_FAIL = register("item.sack.insert_fail");
        ITEM_SACK_REMOVE_ONE = register("item.sack.remove_one");
        ITEM_QUIVER_INSERT = register("item.quiver.insert");
        ITEM_QUIVER_INSERT_FAIL = register("item.quiver.insert_fail");
        ITEM_QUIVER_REMOVE_ONE = register("item.quiver.remove_one");
    }

    /**
     * Register an Inventory Expansion sound event.
     *
     * @param id    String to register the sound event under
     * @return      SoundEvent that was created and registered
     */
    public static SoundEvent register(String id) {
        Identifier identifier = InvExpUtil.identifier(id);
        return Registry.register(Registries.SOUND_EVENT, identifier, SoundEvent.of(identifier));
    }

    /**
     * Run all the static registration for sound events.
     */
    public static void initialize() {
        // Do nothing
    }
}
