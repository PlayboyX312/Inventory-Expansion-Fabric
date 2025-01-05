package derekahedron.invexp.registry;

import derekahedron.invexp.sack.SackInsertable;
import derekahedron.invexp.sack.SackType;
import derekahedron.invexp.sack.TaggedSackInsertable;
import derekahedron.invexp.util.InvExpUtil;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

/**
 * Stores all registry keys for Inventory Expansion
 */
public class InvExpRegistryKeys {
    public static final RegistryKey<Registry<SackType>> SACK_TYPE;
    public static final RegistryKey<Registry<SackInsertable>> SACK_INSERTABLE;
    public static final RegistryKey<Registry<TaggedSackInsertable>> TAGGED_SACK_INSERTABLE;

    static {
        SACK_TYPE = register("sack_type");
        SACK_INSERTABLE = register("sack_insertable");
        TAGGED_SACK_INSERTABLE = register("tagged_sack_insertable");
    }

    /**
     * Register an Inventory Expansion registry key
     *
     * @param id    String to register the registry under
     * @return      RegistryKey that was created and registered
     */
    public static <T> RegistryKey<Registry<T>> register(String id) {
        return RegistryKey.ofRegistry(InvExpUtil.identifier(id));
    }

    /**
     * Run all the static registration for Registry Keys.
     * Also registers synced dynamic inventories.
     */
    public static void initialize() {
        DynamicRegistries.registerSynced(InvExpRegistryKeys.SACK_TYPE, SackType.CODEC);
        DynamicRegistries.registerSynced(InvExpRegistryKeys.SACK_INSERTABLE, SackInsertable.CODEC);
        DynamicRegistries.registerSynced(InvExpRegistryKeys.TAGGED_SACK_INSERTABLE, TaggedSackInsertable.CODEC);
    }
}
