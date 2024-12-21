package derekahedron.invexp.render.item.property;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.render.item.property.bool.BooleanProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import org.jetbrains.annotations.Nullable;

/**
 * Item Property for if a bundle has contents.
 * This is currently unused but can potentially be used to display
 * a different texture if a bundle is empty.
 */
public class BundleHasContentsProperty implements BooleanProperty {
    public static final MapCodec<BundleHasContentsProperty> CODEC;

    /**
     * Returns true if the stack is a bundle and has contents
     */
    @Override
    public boolean getValue(
            ItemStack bundleStack, @Nullable ClientWorld world, @Nullable LivingEntity user,
            int seed, ModelTransformationMode modelTransformationMode
    ) {
        BundleContentsComponent contents = bundleStack.get(DataComponentTypes.BUNDLE_CONTENTS);
        return contents != null && !contents.isEmpty();
    }

    @Override
    public MapCodec<BundleHasContentsProperty> getCodec() {
        return CODEC;
    }

    static {
        CODEC = MapCodec.unit(new BundleHasContentsProperty());
    }
}
