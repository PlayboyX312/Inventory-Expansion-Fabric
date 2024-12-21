package derekahedron.invexp.render.item.model;

import derekahedron.invexp.util.InvExpUtil;
import net.minecraft.client.render.item.model.ItemModelTypes;

/**
 * Sets up Inventory Expansion item models
 */
public class InvExpItemModels {

    /**
     * Register custom Inventory Expansion item models
     */
    public static void initialize() {
        ItemModelTypes.ID_MAPPER.put(InvExpUtil.identifier("sack/selected_item"), SackSelectedItemModel.Unbaked.CODEC);
    }
}
