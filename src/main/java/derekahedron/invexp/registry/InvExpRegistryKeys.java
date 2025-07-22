package derekahedron.invexp.registry;

import derekahedron.invexp.sack.*;
import derekahedron.invexp.util.InvExpUtil;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

/**
 * Registry keys for Inventory Expansion
 */
public class InvExpRegistryKeys {
    public static final RegistryKey<Registry<SackType>> SACK_TYPE =
            register("sack_type");
    public static final RegistryKey<Registry<SackTypeDefault>> SACK_TYPE_DEFINITION =
            register("sack_type_default");
    public static final RegistryKey<Registry<SackWeightDefault>> SACK_WEIGHT_DEFINITION =
            register("sack_weight_default");

    /**
     * Registers an Inventory Expansion registry key.
     *
     * @param id a <code>String</code> to register the registry under
     * @return the <code>RegistryKey</code> that was created and registered
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
        DynamicRegistries.registerSynced(InvExpRegistryKeys.SACK_TYPE_DEFINITION, SackTypeDefault.CODEC);
        DynamicRegistries.registerSynced(InvExpRegistryKeys.SACK_WEIGHT_DEFINITION, SackWeightDefault.CODEC);
    }
}
