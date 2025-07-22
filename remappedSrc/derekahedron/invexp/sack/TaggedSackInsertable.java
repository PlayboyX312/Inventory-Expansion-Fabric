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
 * @param priority      determines if this insertable should
 * @param sackType      sack type to be inserted under. If none, cannot be inserted
 * @param sackWeight    weight that the item should take up in a sack
 */
public record TaggedSackInsertable(int priority, Optional<RegistryEntry<SackType>> sackType, Optional<Integer> sackWeight) {
    public static final Codec<TaggedSackInsertable> CODEC;
    public static final PacketCodec<RegistryByteBuf, TaggedSackInsertable> PACKET_CODEC;
    public static final Codec<RegistryEntry<TaggedSackInsertable>> ENTRY_CODEC;
    public static final PacketCodec<RegistryByteBuf, RegistryEntry<TaggedSackInsertable>> ENTRY_PACKET_CODEC;

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.optionalFieldOf("priority", 0).forGetter(TaggedSackInsertable::priority),
                SackType.ENTRY_CODEC.optionalFieldOf("sack_type").forGetter(TaggedSackInsertable::sackType),
                Codecs.NON_NEGATIVE_INT.optionalFieldOf("sack_weight").forGetter(TaggedSackInsertable::sackWeight)
        ).apply(instance, TaggedSackInsertable::new));
        PACKET_CODEC = PacketCodec.tuple(
                PacketCodecs.INTEGER, TaggedSackInsertable::priority,
                SackType.ENTRY_PACKET_CODEC.collect(PacketCodecs::optional), TaggedSackInsertable::sackType,
                PacketCodecs.INTEGER.collect(PacketCodecs::optional), TaggedSackInsertable::sackWeight,
                TaggedSackInsertable::new
        );
        ENTRY_CODEC = RegistryElementCodec.of(
                InvExpRegistryKeys.TAGGED_SACK_INSERTABLE, CODEC
        );
        ENTRY_PACKET_CODEC = PacketCodecs.registryEntry(
                InvExpRegistryKeys.TAGGED_SACK_INSERTABLE, PACKET_CODEC
        );
    }
}
