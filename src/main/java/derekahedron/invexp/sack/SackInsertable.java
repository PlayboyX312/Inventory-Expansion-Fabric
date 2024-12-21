package derekahedron.invexp.sack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import derekahedron.invexp.registry.InvExpRegistryKeys;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.dynamic.Codecs;

import java.util.Optional;

/**
 * Defines a default sack insertable behavior for items. This is dynamic and defined in
 * DataPacks.
 *
 * @param sackType      sack type to be inserted under. If none, cannot be inserted
 * @param sackWeight    weight that the item should take up in a sack
 */
public record SackInsertable(Optional<RegistryEntry<SackType>> sackType, Optional<Integer> sackWeight) {
    public static final Codec<SackInsertable> CODEC;
    public static final PacketCodec<RegistryByteBuf, SackInsertable> PACKET_CODEC;
    public static final Codec<RegistryEntry<SackInsertable>> ENTRY_CODEC;
    public static final PacketCodec<RegistryByteBuf, RegistryEntry<SackInsertable>> ENTRY_PACKET_CODEC;

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                SackType.ENTRY_CODEC.optionalFieldOf("sack_type").forGetter(SackInsertable::sackType),
                Codecs.NON_NEGATIVE_INT.optionalFieldOf("sack_weight").forGetter(SackInsertable::sackWeight)
        ).apply(instance, SackInsertable::new));
        PACKET_CODEC = PacketCodec.tuple(
                SackType.ENTRY_PACKET_CODEC.collect(PacketCodecs::optional), SackInsertable::sackType,
                PacketCodecs.INTEGER.collect(PacketCodecs::optional), SackInsertable::sackWeight,
                SackInsertable::new
        );
        ENTRY_CODEC = RegistryElementCodec.of(
                InvExpRegistryKeys.SACK_INSERTABLE, CODEC
        );
        ENTRY_PACKET_CODEC = PacketCodecs.registryEntry(
                InvExpRegistryKeys.SACK_INSERTABLE, PACKET_CODEC
        );
    }
}
