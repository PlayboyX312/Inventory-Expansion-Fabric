package derekahedron.invexp.datagen;

import derekahedron.invexp.InventoryExpansion;
import derekahedron.invexp.item.InvExpItemTags;
import derekahedron.invexp.registry.InvExpRegistryKeys;
import derekahedron.invexp.sack.SackWeightDefault;
import derekahedron.invexp.util.InvExpUtil;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.predicate.component.ComponentPredicateTypes;
import net.minecraft.predicate.component.ComponentsPredicate;
import net.minecraft.predicate.item.PotionContentsPredicate;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntryList;
import org.apache.commons.lang3.math.Fraction;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class SackWeightDefaultProvider extends FabricDynamicRegistryProvider {

    public SackWeightDefaultProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(@NotNull RegistryWrapper.WrapperLookup wrapperLookup, @NotNull Entries entries) {
        RegistryWrapper.Impl<Item> itemLookup = wrapperLookup.getOrThrow(RegistryKeys.ITEM);

        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_WEIGHT_DEFINITION, InvExpUtil.identifier("double")),
                new SackWeightDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackWeight.DOUBLE),
                        Fraction.getFraction(2)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_WEIGHT_DEFINITION, InvExpUtil.identifier("half")),
                new SackWeightDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackWeight.HALF),
                        Fraction.ONE_HALF));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_WEIGHT_DEFINITION, InvExpUtil.identifier("third")),
                new SackWeightDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackWeight.THIRD),
                        Fraction.ONE_THIRD));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_WEIGHT_DEFINITION, InvExpUtil.identifier("fourth")),
                new SackWeightDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackWeight.FOURTH),
                        Fraction.ONE_QUARTER));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_WEIGHT_DEFINITION, InvExpUtil.identifier("fifth")),
                new SackWeightDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackWeight.FIFTH),
                        Fraction.ONE_FIFTH));

        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_WEIGHT_DEFINITION, InvExpUtil.identifier("water_bottle")),
                new SackWeightDefault(
                        Optional.of(10),
                        Optional.of(Ingredient.ofItems(Items.POTION)),
                        Optional.of(ComponentsPredicate.Builder.create()
                                .partial(
                                        ComponentPredicateTypes.POTION_CONTENTS,
                                        new PotionContentsPredicate(RegistryEntryList.of(Potions.WATER)))
                                .build()),
                        Optional.of(Fraction.ONE_QUARTER)));
    }

    @Override
    public String getName() {
        return String.format("%s Sack Weight Defaults", InventoryExpansion.MOD_NAME);
    }
}
