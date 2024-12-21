package derekahedron.invexp.item;

import derekahedron.invexp.component.InvExpDataComponentTypes;
import derekahedron.invexp.component.types.QuiverContentsComponent;
import derekahedron.invexp.component.types.SackContentsComponent;
import derekahedron.invexp.sack.SacksHelper;
import derekahedron.invexp.util.InvExpUtil;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Stores all Items for Inventory Expansion
 */
public class InvExpItems {

    public static final Item SACK;
    public static final Item QUIVER;

    static {
        SACK = register("sack", SackItem::new, new Item.Settings()
                .maxCount(1)
                .component(InvExpDataComponentTypes.SACK_CONTENTS, SackContentsComponent.DEFAULT)
                .component(InvExpDataComponentTypes.MAX_SACK_TYPES, 1)
                .component(InvExpDataComponentTypes.MAX_SACK_WEIGHT, 256 * SacksHelper.DEFAULT_SACK_WEIGHT)
                .component(InvExpDataComponentTypes.MAX_SACK_STACKS, 64)
        );
        QUIVER = register("quiver", QuiverItem::new, new Item.Settings()
                .maxCount(1)
                .component(InvExpDataComponentTypes.QUIVER_CONTENTS, QuiverContentsComponent.DEFAULT)
                .component(InvExpDataComponentTypes.MAX_QUIVER_OCCUPANCY, 8)
                .component(InvExpDataComponentTypes.MAX_QUIVER_STACKS, 64)
        );
    }

    /**
     * Register an Inventory Expansion item. Adds the given ID to the settings before creation.
     *
     * @param id        String to register the item under
     * @param factory   Creation function for the item. Takes in a settings
     * @param settings  Settings for the item.
     * @return          Item that was created and registered
     */
    public static Item register(
            String id, @NotNull Function<Item.Settings, Item> factory, Item.@NotNull Settings settings
    ) {
        Identifier identifier = InvExpUtil.identifier(id);
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, identifier);
        Item item = factory.apply(settings.registryKey(key));
        return Registry.register(Registries.ITEM, identifier, item);
    }

    /**
     * Run all the static registration for items. Also add items to the Creative Inventory.
     */
    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register((itemGroup) ->
                itemGroup.addAfter(Items.PINK_BUNDLE, new ItemStack(InvExpItems.SACK))
        );
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(
                (itemGroup) -> itemGroup.addAfter(Items.CROSSBOW, new ItemStack(InvExpItems.QUIVER))
        );
    }
}
