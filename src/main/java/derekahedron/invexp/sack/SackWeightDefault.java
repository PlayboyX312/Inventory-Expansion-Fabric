package derekahedron.invexp.sack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import derekahedron.invexp.util.InvExpCodecs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.component.ComponentsPredicate;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.entry.RegistryEntryList;
import org.apache.commons.lang3.math.Fraction;

import java.util.Optional;

public record SackWeightDefault(
        Optional<Integer> priority,
        Optional<Ingredient> items,
        Optional<ComponentsPredicate> predicate,
        Optional<Fraction> sackWeight) {
    public static final Codec<SackWeightDefault> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.optionalFieldOf("priority").forGetter(SackWeightDefault::priority),
                    Ingredient.CODEC.optionalFieldOf("items").forGetter(SackWeightDefault::items),
                    ComponentsPredicate.CODEC.codec().optionalFieldOf("predicate").forGetter(SackWeightDefault::predicate),
                    InvExpCodecs.FRACTION.optionalFieldOf("sack_weight").forGetter(SackWeightDefault::sackWeight)
            ).apply(instance, SackWeightDefault::new));

    public SackWeightDefault(RegistryEntryList<Item> tag, Fraction sackWeight) {
        this(Optional.empty(), Optional.of(Ingredient.fromTag(tag)), Optional.empty(), Optional.of(sackWeight));
    }

    public boolean test(ItemStack stack) {
        return predicate.isEmpty() || predicate.get().test(stack);
    }

    public int compareTo(SackWeightDefault other) {
        return priority.orElse(0) - other.priority.orElse(0);
    }
}
