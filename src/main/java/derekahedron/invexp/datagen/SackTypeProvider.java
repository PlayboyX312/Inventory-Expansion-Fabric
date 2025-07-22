package derekahedron.invexp.datagen;

import derekahedron.invexp.InventoryExpansion;
import derekahedron.invexp.registry.InvExpRegistryKeys;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.registry.*;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class SackTypeProvider extends FabricDynamicRegistryProvider {

    public SackTypeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(@NotNull RegistryWrapper.WrapperLookup lookup, @NotNull Entries entries) {
        entries.addAll(lookup.getOrThrow(InvExpRegistryKeys.SACK_TYPE));
    }

    @Override
    public String getName() {
        return String.format("%s Sack Types", InventoryExpansion.MOD_NAME);
    }
}
