package derekahedron.invexp.component.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import derekahedron.invexp.sack.SackType;
import derekahedron.invexp.util.InvExpCodecs;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.entry.RegistryEntry;
import org.apache.commons.lang3.math.Fraction;

import java.util.Optional;

/**
 * Controls how an <code>ItemStack</code> is inserted into a sack. Has priority over the individual
 * sack type and sack weight components. Ideally, we would only use those components instead of having this,
 * but because we need to include DataPack functionality, we need to have a default manager.
 * However, this means we cannot make an item that has data the prevents it from being inserted in a sack,
 * so we allow this component to have an empty sack type which will prevent it from being inserted.
 * In the future, I would like to remove this component, so use it sparingly.
 *
 * @param sackType the <code>SackType</code> that the stack has
 * @param sackWeight the <code>SackWeight</code> that one item in the stack takes up
 */
public record SackInsertableComponent(Optional<RegistryEntry<SackType>> sackType, Optional<Fraction> sackWeight) {
    public static final Codec<SackInsertableComponent> CODEC;
    public static final PacketCodec<RegistryByteBuf, SackInsertableComponent> PACKET_CODEC;

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                SackType.ENTRY_CODEC.optionalFieldOf("sack_type").forGetter(SackInsertableComponent::sackType),
                InvExpCodecs.FRACTION.optionalFieldOf("sack_weight").forGetter(SackInsertableComponent::sackWeight)
        ).apply(instance, SackInsertableComponent::new));
        PACKET_CODEC = PacketCodec.tuple(
                SackType.ENTRY_PACKET_CODEC.collect(PacketCodecs::optional), SackInsertableComponent::sackType,
                InvExpCodecs.FRACTION_PACKET.collect(PacketCodecs::optional), SackInsertableComponent::sackWeight,
                SackInsertableComponent::new
        );
    }
}
