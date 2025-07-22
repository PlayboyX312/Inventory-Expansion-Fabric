package derekahedron.invexp;

import derekahedron.invexp.datagen.*;
import derekahedron.invexp.registry.InvExpRegistryKeys;
import derekahedron.invexp.sack.SackTypes;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import org.jetbrains.annotations.NotNull;

public class InventoryExpansionDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(@NotNull FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();
        pack.addProvider(InvExpItemTagProvider::new);
        pack.addProvider(SackTypeProvider::new);
        pack.addProvider(SackTypeDefaultProvider::new);
        pack.addProvider(SackWeightDefaultProvider::new);
        pack.addProvider(InvExpRecipeProvider::new);
    }

    @Override
    public void buildRegistry(@NotNull RegistryBuilder registryBuilder) {
        registryBuilder.addRegistry(InvExpRegistryKeys.SACK_TYPE, SackTypes::bootstrap);
    }
}
