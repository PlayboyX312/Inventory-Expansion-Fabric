package derekahedron.invexp.sack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import derekahedron.invexp.registry.InvExpRegistryKeys;
import derekahedron.invexp.registry.DecentralizedReference;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Unit;

import java.util.Optional;

/**
 * Defines a sack type that items can be inserted into a sack under.
 * Contains a name that is displayed in the sack tooltip.
 *
 * @param name  Optional name of the sack type
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
        ENTRY_PACKET_CODEC = DecentralizedReference.entryPacketCodec(
                InvExpRegistryKeys.SACK_TYPE, PACKET_CODEC
        );
    }
}
