package derekahedron.invexp.component;

import derekahedron.invexp.component.types.QuiverContentsComponent;
import derekahedron.invexp.component.types.SackContentsComponent;
import derekahedron.invexp.component.types.SackInsertableComponent;
import derekahedron.invexp.sack.SackType;
import derekahedron.invexp.util.InvExpUtil;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.dynamic.Codecs;

/**
 * Stores all Data Component Types for Inventory Expansion
 */
public class InvExpDataComponentTypes {

    public static final ComponentType<SackInsertableComponent> SACK_INSERTABLE;
    public static final ComponentType<RegistryEntry<SackType>> SACK_TYPE;
    public static final ComponentType<Integer> SACK_WEIGHT;
    public static final ComponentType<Integer> MAX_SACK_TYPES;
    public static final ComponentType<Integer> MAX_SACK_WEIGHT;
    public static final ComponentType<Integer> MAX_SACK_STACKS;
    public static final ComponentType<SackContentsComponent> SACK_CONTENTS;
    public static final ComponentType<Integer> MAX_QUIVER_OCCUPANCY;
    public static final ComponentType<Integer> MAX_QUIVER_STACKS;
    public static final ComponentType<QuiverContentsComponent> QUIVER_CONTENTS;

    static {
        SACK_INSERTABLE = register("sack_insertable", ComponentType.<SackInsertableComponent>builder().codec(SackInsertableComponent.CODEC).packetCodec(SackInsertableComponent.PACKET_CODEC).build());
        SACK_TYPE = register("sack_type", ComponentType.<RegistryEntry<SackType>>builder().codec(SackType.ENTRY_CODEC).packetCodec(SackType.ENTRY_PACKET_CODEC).build());
        SACK_WEIGHT = register("sack_weight", ComponentType.<Integer>builder().codec(Codecs.NON_NEGATIVE_INT).packetCodec(PacketCodecs.VAR_INT).build());
        MAX_SACK_TYPES = register("max_sack_types", ComponentType.<Integer>builder().codec(Codecs.NON_NEGATIVE_INT).packetCodec(PacketCodecs.VAR_INT).build());
        MAX_SACK_WEIGHT = register("max_sack_weight", ComponentType.<Integer>builder().codec(Codecs.NON_NEGATIVE_INT).packetCodec(PacketCodecs.VAR_INT).build());
        MAX_SACK_STACKS = register("max_sack_stacks", ComponentType.<Integer>builder().codec(Codecs.NON_NEGATIVE_INT).packetCodec(PacketCodecs.VAR_INT).build());
        SACK_CONTENTS = register("sack_contents", ComponentType.<SackContentsComponent>builder().codec(SackContentsComponent.CODEC).packetCodec(SackContentsComponent.PACKET_CODEC).build());
        MAX_QUIVER_OCCUPANCY = register("max_quiver_occupancy", ComponentType.<Integer>builder().codec(Codecs.NON_NEGATIVE_INT).packetCodec(PacketCodecs.VAR_INT).build());
        MAX_QUIVER_STACKS = register("max_quiver_stacks", ComponentType.<Integer>builder().codec(Codecs.NON_NEGATIVE_INT).packetCodec(PacketCodecs.VAR_INT).build());
        QUIVER_CONTENTS = register("quiver_contents", ComponentType.<QuiverContentsComponent>builder().codec(QuiverContentsComponent.CODEC).packetCodec(QuiverContentsComponent.PACKET_CODEC).build());
    }

    /**
     * Register an Inventory Expansion component type
     *
     * @param id String to register the component type under
     * @param componentType Component Type to register
     * @return Component Type that was registered
     */
    public static <T> ComponentType<T> register(String id, ComponentType<T> componentType) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, InvExpUtil.identifier(id), componentType);
    }

    /**
     * Run this function on start up to run the static registration
     */
    public static void initialize() {
        // Do Nothing
    }
}
