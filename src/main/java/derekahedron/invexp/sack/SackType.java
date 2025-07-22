package derekahedron.invexp.sack;

import com.mojang.serialization.Codec;
import derekahedron.invexp.registry.InvExpRegistryKeys;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;

/**
 * Defines a sack type that items can be inserted into a sack under.
 * Contains a name that is displayed in the sack tooltip.
 */
public record SackType() {
    public static final Codec<SackType> CODEC;
    public static final PacketCodec<RegistryByteBuf, SackType> PACKET_CODEC;
    public static final Codec<RegistryKey<SackType>> KEY_CODEC;
    public static final PacketCodec<ByteBuf, RegistryKey<SackType>> KEY_PACKET_CODEC;
    public static final Codec<RegistryEntry<SackType>> ENTRY_CODEC;
    public static final PacketCodec<RegistryByteBuf, RegistryEntry<SackType>> ENTRY_PACKET_CODEC;

    static {
        CODEC = Codec.unit(SackType::new);
        PACKET_CODEC = new PacketCodec<>() {

            @Override
            public SackType decode(RegistryByteBuf object) {
                return new SackType();
            }

            @Override
            public void encode(RegistryByteBuf object, SackType object2) {
            }

        };
        KEY_CODEC = RegistryKey.createCodec(InvExpRegistryKeys.SACK_TYPE);
        KEY_PACKET_CODEC = RegistryKey.createPacketCodec(InvExpRegistryKeys.SACK_TYPE);
        ENTRY_CODEC = RegistryElementCodec.of(
                InvExpRegistryKeys.SACK_TYPE, CODEC
        );
        ENTRY_PACKET_CODEC = PacketCodecs.registryEntry(
                InvExpRegistryKeys.SACK_TYPE, PACKET_CODEC
        );
    }
}
