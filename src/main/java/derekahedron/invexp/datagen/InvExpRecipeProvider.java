package derekahedron.invexp.datagen;

import derekahedron.invexp.InventoryExpansion;
import derekahedron.invexp.item.InvExpItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

import java.util.concurrent.CompletableFuture;

public class InvExpRecipeProvider extends FabricRecipeProvider {

    public InvExpRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup registries, RecipeExporter exporter) {
        return new InvExpRecipeGenerator(registries, exporter);
    }

    @Override
    public String getName() {
        return String.format("%s Recipes", InventoryExpansion.MOD_NAME);
    }

    public static class InvExpRecipeGenerator extends RecipeGenerator {

        protected InvExpRecipeGenerator(RegistryWrapper.WrapperLookup registries, RecipeExporter exporter) {
            super(registries, exporter);
        }

        @Override
        public void generate() {
            createShaped(RecipeCategory.TOOLS, InvExpItems.SACK)
                    .input('-', Items.STRING)
                    .input('#', Items.LEATHER)
                    .pattern("-#-")
                    .pattern("# #")
                    .pattern("###")
                    .criterion("has_string", conditionsFromItem(Items.STRING))
                    .offerTo(exporter);
            createShaped(RecipeCategory.TOOLS, InvExpItems.QUIVER)
                    .input('-', Items.STRING)
                    .input('X', Items.RABBIT_HIDE)
                    .input('#', Items.LEATHER)
                    .pattern(" XX")
                    .pattern("-##")
                    .pattern(" ##")
                    .criterion("has_arrow", conditionsFromTag(ItemTags.ARROWS))
                    .offerTo(exporter);
        }
    }
}
