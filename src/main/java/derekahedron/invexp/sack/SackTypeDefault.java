package derekahedron.invexp.sack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.component.ComponentsPredicate;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record SackTypeDefault(
        Optional<Integer> priority,
        Optional<Ingredient> items,
        Optional<ComponentsPredicate> predicate,
        Optional<RegistryEntry<SackType>> sackType) {
    public static final Codec<SackTypeDefault> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT
                            .optionalFieldOf("priority")
                            .forGetter(SackTypeDefault::priority),
                    Ingredient.CODEC
                            .optionalFieldOf("items")
                            .forGetter(SackTypeDefault::items),
                    ComponentsPredicate.CODEC.codec()
                            .optionalFieldOf("predicate")
                            .forGetter(SackTypeDefault::predicate),
                    SackType.ENTRY_CODEC
                            .optionalFieldOf("sack_type")
                            .forGetter(SackTypeDefault::sackType)
            ).apply(instance, SackTypeDefault::new));

    public SackTypeDefault(int priority, RegistryEntryList<Item> tag, RegistryEntry<SackType> sackType) {
        this(Optional.of(priority), Optional.of(Ingredient.fromTag(tag)), Optional.empty(), Optional.of(sackType));
    }

    public SackTypeDefault(RegistryEntryList<Item> tag, RegistryEntry<SackType> sackType) {
        this(Optional.empty(), Optional.of(Ingredient.fromTag(tag)), Optional.empty(), Optional.of(sackType));
    }

    public boolean test(ItemStack stack) {
        return predicate.isEmpty() || predicate.get().test(stack);
    }

    public int compareTo(@NotNull SackTypeDefault other) {
        return priority.orElse(0) - other.priority.orElse(0);
    }
}
