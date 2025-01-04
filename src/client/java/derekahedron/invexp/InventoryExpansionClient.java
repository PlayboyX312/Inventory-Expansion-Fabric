package derekahedron.invexp;

import derekahedron.invexp.render.item.model.InvExpItemModels;
import derekahedron.invexp.render.item.property.InvExpItemProperties;
import derekahedron.invexp.screen.InvExpScreenEvents;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.input.Scroller;

/**
 * Handles client-only initialization for Inventory Expansion.
 */
public class InventoryExpansionClient implements ClientModInitializer {

	/**
	 * Run on initializing client.
	 */
	@Override
	public void onInitializeClient() {
		InvExpItemModels.initialize();
		InvExpItemProperties.initialize();
		InvExpScreenEvents.initialize();
	}
}