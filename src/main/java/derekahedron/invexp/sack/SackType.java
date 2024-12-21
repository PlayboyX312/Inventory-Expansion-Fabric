package derekahedron.invexp.sack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import derekahedron.invexp.registry.InvExpRegistryKeys;
import derekahedron.invexp.registry.DecentralizedReference;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

import java.util.Optional;

/**
 * Defines a sack type that items can be inserted into a sack under.
 * Contains a name that is displayed in the sack tooltip.
 *
 * @param name  Optional name of the sack type
 */
public record SackType(Optional<Text> name) {
    public static final Codec<SackType> CODEC;
    public static final PacketCodec<RegistryByteBuf, SackType> PACKET_CODEC;
    public static final Codec<RegistryEntry<SackType>> ENTRY_CODEC;
    public static final PacketCodec<RegistryByteBuf, RegistryEntry<SackType>> ENTRY_PACKET_CODEC;

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                TextCodecs.CODEC.optionalFieldOf("name").forGetter(SackType::name)
        ).apply(instance, SackType::new));
        PACKET_CODEC = PacketCodec.tuple(
                TextCodecs.OPTIONAL_UNLIMITED_REGISTRY_PACKET_CODEC,
                SackType::name,
                SackType::new
        );
        ENTRY_CODEC = RegistryElementCodec.of(
                InvExpRegistryKeys.SACK_TYPE, CODEC
        );
        ENTRY_PACKET_CODEC = DecentralizedReference.entryPacketCodec(
                InvExpRegistryKeys.SACK_TYPE, PACKET_CODEC
        );
    }
}
