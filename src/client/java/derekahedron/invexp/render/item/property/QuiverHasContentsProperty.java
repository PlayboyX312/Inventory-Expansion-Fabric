package derekahedron.invexp.render.item.property;

import com.mojang.serialization.MapCodec;
import derekahedron.invexp.quiver.QuiverContents;
import net.minecraft.client.render.item.property.bool.BooleanProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Item Property for if a quiver has contents
 */
public class QuiverHasContentsProperty implements BooleanProperty {
    public static final MapCodec<QuiverHasContentsProperty> CODEC;

    /**
     * Returns true if the stack is a quiver and has contents
     */
    @Override
    public boolean test(
            ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity,
            int seed, ItemDisplayContext displayContext) {
        QuiverContents contents = QuiverContents.of(stack);
        return contents != null && !contents.isEmpty();
    }

    @Override
    public MapCodec<QuiverHasContentsProperty> getCodec() {
        return CODEC;
    }

    static {
        CODEC = MapCodec.unit(new QuiverHasContentsProperty());
    }
}
