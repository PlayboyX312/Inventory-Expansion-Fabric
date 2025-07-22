package derekahedron.invexp.sack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.dynamic.Codecs;

import java.util.Optional;

public record SackTest(int priority, Ingredient items, Optional<RegistryEntry<SackType>> sackType, Optional<Integer> sackWeight) {
    public static final Codec<SackTest> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.optionalFieldOf("priority", 0).forGetter(SackTest::priority),
                    Ingredient.CODEC.fieldOf("items").forGetter(SackTest::items),
                    SackType.ENTRY_CODEC.optionalFieldOf("sack_type").forGetter(SackTest::sackType),
                    Codecs.NON_NEGATIVE_INT.optionalFieldOf("sack_weight").forGetter(SackTest::sackWeight)
            ).apply(instance, SackTest::new));
    public static final PacketCodec<RegistryByteBuf, SackTest> PACKET_CODEC =
            PacketCodec.tuple(
                    PacketCodecs.INTEGER, SackTest::priority,
                    Ingredient.PACKET_CODEC, SackTest::items,
                    SackType.ENTRY_PACKET_CODEC.collect(PacketCodecs::optional), SackTest::sackType,
                    PacketCodecs.INTEGER.collect(PacketCodecs::optional), SackTest::sackWeight,
                    SackTest::new
            );
}
