package derekahedron.invexp.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import org.apache.commons.lang3.math.Fraction;

public class InvExpCodecs {

    public static final Codec<Fraction> FRACTION = new Codec<>() {

        public static final Codec<Fraction> NUMERATOR_DENOMINATOR_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("numerator").forGetter(Fraction::getNumerator),
                Codec.INT.fieldOf("denominator").forGetter(Fraction::getDenominator)
        ).apply(instance, Fraction::getFraction));

        @Override
        public <T> DataResult<Pair<Fraction, T>> decode(DynamicOps<T> ops, T input) {
            DataResult<Pair<Fraction, T>> result;
            try {
                result = Codec.DOUBLE.map(Fraction::getFraction).decode(ops, input);
                if (result.isSuccess()) return result;
            } catch (Exception e) {
                return DataResult.error(e::getMessage);
            }
            try {
                result = Codec.INT.map(Fraction::getFraction).decode(ops, input);
                if (result.isSuccess()) return result;
            } catch (Exception e) {
                return DataResult.error(e::getMessage);
            }
            try {
                result = Codec.STRING.map(Fraction::getFraction).decode(ops, input);
                if (result.isSuccess()) return result;
            } catch (Exception e) {
                return DataResult.error(e::getMessage);
            }
            try {
                result = NUMERATOR_DENOMINATOR_CODEC.decode(ops, input);
                if (result.isSuccess()) return result;
            } catch (Exception e) {
                return DataResult.error(e::getMessage);
            }
            return DataResult.error(() -> "Not a fraction: " + input);
        }

        @Override
        public <T> DataResult<T> encode(Fraction fraction, DynamicOps<T> ops, T prefix) {
            if (fraction.getDenominator() == 1) {
                return Codec.INT.comap(Fraction::getNumerator).encode(fraction, ops, prefix);
            } else {
                return NUMERATOR_DENOMINATOR_CODEC.encode(fraction, ops, prefix);
            }
        }
    };

    public static final Codec<Fraction> NON_NEGATIVE_FRACTION = FRACTION.validate(fraction ->
            fraction.getNumerator() >= 0
                    ? DataResult.success(fraction)
                    : DataResult.error(() -> "Fraction must be non-negative: " + fraction));

    public static final PacketCodec<RegistryByteBuf, Fraction> FRACTION_PACKET = PacketCodec.tuple(
            PacketCodecs.INTEGER, Fraction::getNumerator,
            PacketCodecs.INTEGER, Fraction::getDenominator,
            Fraction::getFraction);
}
