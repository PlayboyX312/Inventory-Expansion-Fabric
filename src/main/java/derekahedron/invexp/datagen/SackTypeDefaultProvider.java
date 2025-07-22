package derekahedron.invexp.datagen;

import derekahedron.invexp.InventoryExpansion;
import derekahedron.invexp.item.InvExpItemTags;
import derekahedron.invexp.registry.InvExpRegistryKeys;
import derekahedron.invexp.sack.SackType;
import derekahedron.invexp.sack.SackTypeDefault;
import derekahedron.invexp.sack.SackTypes;
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
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntryList;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class SackTypeDefaultProvider extends FabricDynamicRegistryProvider {

    public SackTypeDefaultProvider(@NotNull FabricDataOutput output,
                                   @NotNull CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(@NotNull RegistryWrapper.WrapperLookup wrapperLookup, @NotNull Entries entries) {
        RegistryWrapper.Impl<Item> itemLookup = wrapperLookup.getOrThrow(RegistryKeys.ITEM);
        RegistryWrapper.Impl<SackType> sackTypeLookup = wrapperLookup.getOrThrow(InvExpRegistryKeys.SACK_TYPE);

        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("wood")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.WOOD),
                        sackTypeLookup.getOrThrow(SackTypes.WOOD)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("door")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.DOOR),
                        sackTypeLookup.getOrThrow(SackTypes.DOOR)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("pressure_plate")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.PRESSURE_PLATE),
                        sackTypeLookup.getOrThrow(SackTypes.PRESSURE_PLATE)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("button")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.BUTTON),
                        sackTypeLookup.getOrThrow(SackTypes.BUTTON)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("stone")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.STONE),
                        sackTypeLookup.getOrThrow(SackTypes.STONE)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("bricks")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.BRICKS),
                        sackTypeLookup.getOrThrow(SackTypes.BRICKS)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("mud_bricks")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.MUD_BRICKS),
                        sackTypeLookup.getOrThrow(SackTypes.MUD_BRICKS)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("resin_bricks")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.RESIN_BRICKS),
                        sackTypeLookup.getOrThrow(SackTypes.RESIN_BRICKS)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("sandstone")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.SANDSTONE),
                        sackTypeLookup.getOrThrow(SackTypes.SANDSTONE)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("prismarine")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.PRISMARINE),
                        sackTypeLookup.getOrThrow(SackTypes.PRISMARINE)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("nether_bricks")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.NETHER_BRICKS),
                        sackTypeLookup.getOrThrow(SackTypes.NETHER_BRICKS)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("purpur")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.PURPUR),
                        sackTypeLookup.getOrThrow(SackTypes.PURPUR)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("metal_block")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.METAL_BLOCK),
                        sackTypeLookup.getOrThrow(SackTypes.METAL_BLOCK)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("crystal_block")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.CRYSTAL_BLOCK),
                        sackTypeLookup.getOrThrow(SackTypes.CRYSTAL_BLOCK)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("chains")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.CHAINS),
                        sackTypeLookup.getOrThrow(SackTypes.CHAINS)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("wool")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.WOOL),
                        sackTypeLookup.getOrThrow(SackTypes.WOOL)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("terracotta")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.TERRACOTTA),
                        sackTypeLookup.getOrThrow(SackTypes.TERRACOTTA)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("concrete")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.CONCRETE),
                        sackTypeLookup.getOrThrow(SackTypes.CONCRETE)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("concrete_powder")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.CONCRETE_POWDER),
                        sackTypeLookup.getOrThrow(SackTypes.CONCRETE_POWDER)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("glass")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.GLASS),
                        sackTypeLookup.getOrThrow(SackTypes.GLASS)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("bed")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.BED),
                        sackTypeLookup.getOrThrow(SackTypes.BED)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("candle")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.CANDLE),
                        sackTypeLookup.getOrThrow(SackTypes.CANDLE)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("banner")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.BANNER),
                        sackTypeLookup.getOrThrow(SackTypes.BANNER)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("soil")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.SOIL),
                        sackTypeLookup.getOrThrow(SackTypes.SOIL)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("ice")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.ICE),
                        sackTypeLookup.getOrThrow(SackTypes.ICE)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("snow")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.SNOW),
                        sackTypeLookup.getOrThrow(SackTypes.SNOW)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("bone_block")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.BONE_BLOCK),
                        sackTypeLookup.getOrThrow(SackTypes.BONE_BLOCK)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("ore")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.ORE),
                        sackTypeLookup.getOrThrow(SackTypes.ORE)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("fungus")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.FUNGUS),
                        sackTypeLookup.getOrThrow(SackTypes.FUNGUS)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("plant")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.PLANT),
                        sackTypeLookup.getOrThrow(SackTypes.PLANT)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("bamboo")),
                new SackTypeDefault(
                        Optional.of(10),
                        Optional.of(Ingredient.fromTag(itemLookup.getOrThrow(InvExpItemTags.SackType.BAMBOO))),
                        Optional.empty(),
                        Optional.of(sackTypeLookup.getOrThrow(SackTypes.BAMBOO))));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("chorus_fruit")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.CHORUS_FRUIT),
                        sackTypeLookup.getOrThrow(SackTypes.CHORUS_FRUIT)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("egg")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.EGG),
                        sackTypeLookup.getOrThrow(SackTypes.EGG)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("wheat_seeds")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.WHEAT_SEEDS),
                        sackTypeLookup.getOrThrow(SackTypes.WHEAT_SEEDS)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("cocoa_beans")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.COCOA_BEANS),
                        sackTypeLookup.getOrThrow(SackTypes.COCOA_BEANS)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("pumpkin_seeds")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.PUMPKIN_SEEDS),
                        sackTypeLookup.getOrThrow(SackTypes.PUMPKIN_SEEDS)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("melon_seeds")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.MELON_SEEDS),
                        sackTypeLookup.getOrThrow(SackTypes.MELON_SEEDS)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("beetroot_seeds")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.BEETROOT_SEEDS),
                        sackTypeLookup.getOrThrow(SackTypes.BEETROOT_SEEDS)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("torchflower_seeds")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.TORCHFLOWER_SEEDS),
                        sackTypeLookup.getOrThrow(SackTypes.TORCHFLOWER_SEEDS)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("pitcher_pod")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.PITCHER_POD),
                        sackTypeLookup.getOrThrow(SackTypes.PITCHER_POD)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("glow_berries")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.GLOW_BERRIES),
                        sackTypeLookup.getOrThrow(SackTypes.GLOW_BERRIES)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("sweet_berries")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.SWEET_BERRIES),
                        sackTypeLookup.getOrThrow(SackTypes.SWEET_BERRIES)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("nether_wart")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.NETHER_WART),
                        sackTypeLookup.getOrThrow(SackTypes.NETHER_WART)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("sea_creature")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.SEA_CREATURE),
                        sackTypeLookup.getOrThrow(SackTypes.SEA_CREATURE)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("kelp")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.KELP),
                        sackTypeLookup.getOrThrow(SackTypes.KELP)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("coral")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.CORAL),
                        sackTypeLookup.getOrThrow(SackTypes.CORAL)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("sponge")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.SPONGE),
                        sackTypeLookup.getOrThrow(SackTypes.SPONGE)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("melon")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.MELON),
                        sackTypeLookup.getOrThrow(SackTypes.MELON)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("pumpkin")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.PUMPKIN),
                        sackTypeLookup.getOrThrow(SackTypes.PUMPKIN)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("hive")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.NEST),
                        sackTypeLookup.getOrThrow(SackTypes.NEST)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("honey")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.HONEY),
                        sackTypeLookup.getOrThrow(SackTypes.HONEY)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("froglight")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.FROGLIGHT),
                        sackTypeLookup.getOrThrow(SackTypes.FROGLIGHT)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("sculk")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.SCULK),
                        sackTypeLookup.getOrThrow(SackTypes.SCULK)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("cobweb")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.COBWEB),
                        sackTypeLookup.getOrThrow(SackTypes.COBWEB)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("bedrock")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.BEDROCK),
                        sackTypeLookup.getOrThrow(SackTypes.BEDROCK)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("torch")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.TORCH),
                        sackTypeLookup.getOrThrow(SackTypes.TORCH)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("lantern")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.LANTERN),
                        sackTypeLookup.getOrThrow(SackTypes.LANTERN)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("end_crystal")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.END_CRYSTAL),
                        sackTypeLookup.getOrThrow(SackTypes.END_CRYSTAL)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("bell")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.BELL),
                        sackTypeLookup.getOrThrow(SackTypes.BELL)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("scaffolding")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.SCAFFOLDING),
                        sackTypeLookup.getOrThrow(SackTypes.SCAFFOLDING)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("pot")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.POT),
                        sackTypeLookup.getOrThrow(SackTypes.POT)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("armor_stand")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.ARMOR_STAND),
                        sackTypeLookup.getOrThrow(SackTypes.ARMOR_STAND)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("item_frame")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.ITEM_FRAME),
                        sackTypeLookup.getOrThrow(SackTypes.ITEM_FRAME)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("painting")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.PAINTING),
                        sackTypeLookup.getOrThrow(SackTypes.PAINTING)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("sign")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.SIGN),
                        sackTypeLookup.getOrThrow(SackTypes.SIGN)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("head")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.HEAD),
                        sackTypeLookup.getOrThrow(SackTypes.HEAD)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("infested_stone")),
                new SackTypeDefault(
                        10,
                        itemLookup.getOrThrow(InvExpItemTags.SackType.INFESTED_STONE),
                        sackTypeLookup.getOrThrow(SackTypes.INFESTED_STONE)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("redstone_component")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.REDSTONE_COMPONENT),
                        sackTypeLookup.getOrThrow(SackTypes.REDSTONE_COMPONENT)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("rail")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.RAIL),
                        sackTypeLookup.getOrThrow(SackTypes.RAIL)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("minecart")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.MINECART),
                        sackTypeLookup.getOrThrow(SackTypes.MINECART)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("tnt")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.TNT),
                        sackTypeLookup.getOrThrow(SackTypes.TNT)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("bucket")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.BUCKET),
                        sackTypeLookup.getOrThrow(SackTypes.BUCKET)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("fire_charge")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.FIRE_CHARGE),
                        sackTypeLookup.getOrThrow(SackTypes.FIRE_CHARGE)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("bone_meal")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.BONE_MEAL),
                        sackTypeLookup.getOrThrow(SackTypes.BONE_MEAL)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("name_tag")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.NAME_TAG),
                        sackTypeLookup.getOrThrow(SackTypes.NAME_TAG)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("lead")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.LEAD),
                        sackTypeLookup.getOrThrow(SackTypes.LEAD)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("compass")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.COMPASS),
                        sackTypeLookup.getOrThrow(SackTypes.COMPASS)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("clock")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.CLOCK),
                        sackTypeLookup.getOrThrow(SackTypes.CLOCK)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("map")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.MAP),
                        sackTypeLookup.getOrThrow(SackTypes.MAP)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("wind_charge")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.WIND_CHARGE),
                        sackTypeLookup.getOrThrow(SackTypes.WIND_CHARGE)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("firework_rocket")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.FIREWORK_ROCKET),
                        sackTypeLookup.getOrThrow(SackTypes.FIREWORK_ROCKET)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("saddle")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.SADDLE),
                        sackTypeLookup.getOrThrow(SackTypes.SADDLE)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("boat")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.BOAT),
                        sackTypeLookup.getOrThrow(SackTypes.BOAT)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("goat_horn")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.GOAT_HORN),
                        sackTypeLookup.getOrThrow(SackTypes.GOAT_HORN)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("music_disc")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.MUSIC_DISC),
                        sackTypeLookup.getOrThrow(SackTypes.MUSIC_DISC)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("totem_of_undying")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.TOTEM_OF_UNDYING),
                        sackTypeLookup.getOrThrow(SackTypes.TOTEM_OF_UNDYING)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("arrow")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.ARROW),
                        sackTypeLookup.getOrThrow(SackTypes.ARROW)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("food")),
                new SackTypeDefault(
                        -10,
                        itemLookup.getOrThrow(InvExpItemTags.SackType.FOOD),
                        sackTypeLookup.getOrThrow(SackTypes.FOOD)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("carrot")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.CARROT),
                        sackTypeLookup.getOrThrow(SackTypes.CARROT)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("potato")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.POTATO),
                        sackTypeLookup.getOrThrow(SackTypes.POTATO)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("beetroot")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.BEETROOT),
                        sackTypeLookup.getOrThrow(SackTypes.BEETROOT)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("raw_fish")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.RAW_FISH),
                        sackTypeLookup.getOrThrow(SackTypes.RAW_FISH)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("bottle")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.BOTTLE),
                        sackTypeLookup.getOrThrow(SackTypes.BOTTLE)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("potion")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.POTION),
                        sackTypeLookup.getOrThrow(SackTypes.POTION)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("wheat")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.WHEAT),
                        sackTypeLookup.getOrThrow(SackTypes.WHEAT)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("creature")),
                new SackTypeDefault(
                        10,
                        itemLookup.getOrThrow(InvExpItemTags.SackType.CREATURE),
                        sackTypeLookup.getOrThrow(SackTypes.CREATURE)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("heart_of_the_sea")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.HEART_OF_THE_SEA),
                        sackTypeLookup.getOrThrow(SackTypes.HEART_OF_THE_SEA)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("heavy_core")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.HEAVY_CORE),
                        sackTypeLookup.getOrThrow(SackTypes.HEAVY_CORE)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("dye")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.DYE),
                        sackTypeLookup.getOrThrow(SackTypes.DYE)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("paper")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.PAPER),
                        sackTypeLookup.getOrThrow(SackTypes.PAPER)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("book")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.BOOK),
                        sackTypeLookup.getOrThrow(SackTypes.BOOK)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("firework_star")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.FIREWORK_STAR),
                        sackTypeLookup.getOrThrow(SackTypes.FIREWORK_STAR)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("sugar")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.SUGAR),
                        sackTypeLookup.getOrThrow(SackTypes.SUGAR)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("banner_pattern")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.BANNER_PATTERN),
                        sackTypeLookup.getOrThrow(SackTypes.BANNER_PATTERN)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("pottery_sherd")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.POTTERY_SHERD),
                        sackTypeLookup.getOrThrow(SackTypes.POTTERY_SHERD)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("smithing_template")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.SMITHING_TEMPLATE),
                        sackTypeLookup.getOrThrow(SackTypes.SMITHING_TEMPLATE)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("key")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.KEY),
                        sackTypeLookup.getOrThrow(SackTypes.KEY)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("spawn_egg")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.SPAWN_EGG),
                        sackTypeLookup.getOrThrow(SackTypes.SPAWN_EGG)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("command_block")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.COMMAND_BLOCK),
                        sackTypeLookup.getOrThrow(SackTypes.COMMAND_BLOCK)));

        // Special Sack Types
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("moss_blocks")),
                new SackTypeDefault(
                        Optional.of(10),
                        Optional.of(Ingredient.ofItems(Items.MOSS_BLOCK, Items.PALE_MOSS_BLOCK)),
                        Optional.empty(),
                        Optional.of(sackTypeLookup.getOrThrow(SackTypes.PLANT))));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("water_bottle")),
                new SackTypeDefault(
                        Optional.of(10),
                        Optional.of(Ingredient.ofItems(Items.POTION)),
                        Optional.of(ComponentsPredicate.Builder.create()
                                .partial(
                                        ComponentPredicateTypes.POTION_CONTENTS,
                                        new PotionContentsPredicate(RegistryEntryList.of(Potions.WATER)))
                                .build()),
                        Optional.of(sackTypeLookup.getOrThrow(SackTypes.BOTTLE))));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("cake")),
                new SackTypeDefault(
                        Optional.empty(),
                        Optional.of(Ingredient.ofItem(Items.CAKE)),
                        Optional.empty(),
                        Optional.empty()));

        // Biomes O' Plenty Sack Types
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("body_part")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.BODY_PART),
                        sackTypeLookup.getOrThrow(SackTypes.BODY_PART)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("wispjelly")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.WISPJELLY),
                        sackTypeLookup.getOrThrow(SackTypes.WISPJELLY)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("null")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.NULL),
                        sackTypeLookup.getOrThrow(SackTypes.NULL)));

        // Storage Drawers Sack Types
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("drawer")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.DRAWER),
                        sackTypeLookup.getOrThrow(SackTypes.DRAWER)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("drawer_upgrade")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.DRAWER_UPGRADE),
                        sackTypeLookup.getOrThrow(SackTypes.DRAWER_UPGRADE)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("drawer_controller")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.DRAWER_CONTROLLER),
                        sackTypeLookup.getOrThrow(SackTypes.DRAWER_CONTROLLER)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("key_button")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.KEY_BUTTON),
                        sackTypeLookup.getOrThrow(SackTypes.KEY_BUTTON)));

        // Trailier Tales Sack Types
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("cyan_rose_seeds")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.CYAN_ROSE_SEEDS),
                        sackTypeLookup.getOrThrow(SackTypes.CYAN_ROSE_SEEDS)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("dawntrail_seeds")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.DAWNTRAIL_SEEDS),
                        sackTypeLookup.getOrThrow(SackTypes.DAWNTRAIL_SEEDS)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("manedrop_germ")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.MANEDROP_GERM),
                        sackTypeLookup.getOrThrow(SackTypes.MANEDROP_GERM)));

        // Waystones Sack Types
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("waystone")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.WAYSTONE),
                        sackTypeLookup.getOrThrow(SackTypes.WAYSTONE)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("portstone")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.PORTSTONE),
                        sackTypeLookup.getOrThrow(SackTypes.PORTSTONE)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("sharestone")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.SHARESTONE),
                        sackTypeLookup.getOrThrow(SackTypes.SHARESTONE)));
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("warp_plate")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.WARP_PLATE),
                        sackTypeLookup.getOrThrow(SackTypes.WARP_PLATE)));

        // Wilder Wild Sack Types
        entries.add(
                RegistryKey.of(InvExpRegistryKeys.SACK_TYPE_DEFINITION, InvExpUtil.identifier("prickly_pear")),
                new SackTypeDefault(
                        itemLookup.getOrThrow(InvExpItemTags.SackType.PRICKLY_PEAR),
                        sackTypeLookup.getOrThrow(SackTypes.PRICKLY_PEAR)));
    }

    @Override
    public String getName() {
        return String.format("%s Sack Type Defaults", InventoryExpansion.MOD_NAME);
    }
}
