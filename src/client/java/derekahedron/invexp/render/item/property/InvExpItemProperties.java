package derekahedron.invexp.render.item.property;

import derekahedron.invexp.util.InvExpUtil;
import net.minecraft.client.render.item.property.bool.BooleanProperties;

/**
 * Sets up Inventory Expansion item properties
 */
public class InvExpItemProperties {

    /**
     * Register custom Inventory Expansion item properties
     */
    public static void initialize() {
        BooleanProperties.ID_MAPPER.put(InvExpUtil.identifier("sack/has_contents"), SackHasContentsProperty.CODEC);
        BooleanProperties.ID_MAPPER.put(InvExpUtil.identifier("sack/using_selected_item"), SackUsingSelectedItemProperty.CODEC);
        BooleanProperties.ID_MAPPER.put(InvExpUtil.identifier("sack/is_open"), SackIsOpenProperty.CODEC);
        BooleanProperties.ID_MAPPER.put(InvExpUtil.identifier("quiver/has_contents"), QuiverHasContentsProperty.CODEC);
        BooleanProperties.ID_MAPPER.put(InvExpUtil.identifier("bundle/has_contents"), BundleHasContentsProperty.CODEC);
    }
}
