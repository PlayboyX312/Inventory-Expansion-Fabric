package derekahedron.invexp.render.item.model;

import com.mojang.serialization.MapCodec;
import derekahedron.invexp.sack.SackContents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.model.ResolvableModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import org.jetbrains.annotations.Nullable;

public class SackSelectedItemModel implements ItemModel {
    public static final ItemModel INSTANCE = new SackSelectedItemModel();

    /**
     * Updates renderer with the selected stack if it exists
     */
    @Override
    public void update(
            ItemRenderState state, ItemStack stack, ItemModelManager resolver,
            ModelTransformationMode transformationMode, @Nullable ClientWorld world,
            @Nullable LivingEntity user, int seed
    ) {
        ItemStack selectedStack = SackContents.selectedStackOf(user, stack);
        if (selectedStack != stack) {
            resolver.update(state, selectedStack, transformationMode, world, user, seed);
        }
    }

    @Environment(EnvType.CLIENT)
    public record Unbaked() implements ItemModel.Unbaked {
        public static final MapCodec<SackSelectedItemModel.Unbaked> CODEC;

        @Override
        public MapCodec<SackSelectedItemModel.Unbaked> getCodec() {
            return CODEC;
        }

        @Override
        public ItemModel bake(ItemModel.BakeContext context) {
            return SackSelectedItemModel.INSTANCE;
        }

        @Override
        public void resolve(ResolvableModel.Resolver resolver) {
            // Do Nothing
        }

        static {
            CODEC = MapCodec.unit(new SackSelectedItemModel.Unbaked());
        }
    }
}
