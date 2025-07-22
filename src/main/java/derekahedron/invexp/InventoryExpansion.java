package derekahedron.invexp;

import derekahedron.invexp.block.cauldron.InvExpCauldronBehavior;
import derekahedron.invexp.block.dispenser.InvExpDispenserBehavior;
import derekahedron.invexp.component.InvExpDataComponentTypes;
import derekahedron.invexp.item.InvExpItems;
import derekahedron.invexp.loot.InvExpLootTableModifiers;
import derekahedron.invexp.network.InvExpNetworking;
import derekahedron.invexp.registry.InvExpRegistryKeys;
import derekahedron.invexp.server.network.ServerNetworkHandlers;
import derekahedron.invexp.sound.InvExpSoundEvents;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Initializer for the Inventory Expansion mod
 */
public class InventoryExpansion implements ModInitializer {
	public static final String MOD_ID = "invexp";
	public static final String MOD_NAME = "Inventory Expansion";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	/**
	 * Run initializations
	 */
	@Override
	public void onInitialize() {
		InvExpRegistryKeys.initialize();
		InvExpDataComponentTypes.initialize();
		InvExpItems.initialize();
		InvExpSoundEvents.initialize();
		InvExpCauldronBehavior.initialize();
		InvExpDispenserBehavior.initialize();
		InvExpNetworking.initialize();
		ServerNetworkHandlers.initialize();
		InvExpLootTableModifiers.initialize();
	}
}