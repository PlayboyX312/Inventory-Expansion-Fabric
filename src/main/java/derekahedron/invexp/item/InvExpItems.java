package derekahedron.invexp.item;

import derekahedron.invexp.component.InvExpDataComponentTypes;
import derekahedron.invexp.component.types.QuiverContentsComponent;
import derekahedron.invexp.component.types.SackContentsComponent;
import derekahedron.invexp.sack.SacksHelper;
import derekahedron.invexp.util.InvExpUtil;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import org.apache.commons.lang3.math.Fraction;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Items for Inventory Expansion
 */
public class InvExpItems {
    public static final Item SACK =
            register("sack",
                    SackItem::new,
                    new Item.Settings()
                            .maxCount(1)
                            .component(InvExpDataComponentTypes.SACK_CONTENTS, SackContentsComponent.DEFAULT)
                            .component(InvExpDataComponentTypes.MAX_SACK_TYPES, 1)
                            .component(InvExpDataComponentTypes.MAX_SACK_WEIGHT, SacksHelper.DEFAULT_SACK_WEIGHT.multiplyBy(Fraction.getFraction(4)))
                            .component(InvExpDataComponentTypes.MAX_SACK_STACKS, 64));
    public static final Item QUIVER =
            register("quiver",
                    QuiverItem::new,
                    new Item.Settings()
                            .maxCount(1)
                            .component(InvExpDataComponentTypes.QUIVER_CONTENTS, QuiverContentsComponent.DEFAULT)
                            .component(InvExpDataComponentTypes.MAX_QUIVER_OCCUPANCY, Fraction.getFraction(8))
                            .component(InvExpDataComponentTypes.MAX_QUIVER_STACKS, 64));

    /**
     * Registers an Inventory Expansion item.
     * Adds the given ID to the settings before creation.
     *
     * @param id a <code>String</code> to register the item under
     * @param factory the method that creates the new <code>Item</code>. Accepts an <code>Item.Settings</code>
     * @param settings the <code>Item.Settings</code> to create the new <code>Item</code> with
     * @return the <code>Item</code> that was created and registered
     */
    public static Item register(
            String id, @NotNull Function<Item.Settings, Item> factory, Item.@NotNull Settings settings
    ) {
        return Items.register(RegistryKey.of(RegistryKeys.ITEM, InvExpUtil.identifier(id)), factory, settings);
    }

    /**
     * Run all the static registration for items. Also add items to the Creative Inventory.
     */
    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register((itemGroup) ->
                itemGroup.addAfter(Items.PINK_BUNDLE, new ItemStack(InvExpItems.SACK)));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(
                (itemGroup) -> itemGroup.addAfter(Items.CROSSBOW, new ItemStack(InvExpItems.QUIVER)));
    }
}
