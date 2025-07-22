package derekahedron.invexp.render.item.property;

import com.mojang.serialization.MapCodec;
import derekahedron.invexp.sack.SackContents;
import derekahedron.invexp.sack.SackContentsReader;
import net.minecraft.client.render.item.property.bool.BooleanProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Item Property for if a sack has contents
 */
public class SackHasContentsProperty implements BooleanProperty {
    public static final MapCodec<SackHasContentsProperty> CODEC;

    /**
     * Returns true if the stack is a sack and has contents
     */
    @Override
    public boolean test(
            ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity,
            int seed, ItemDisplayContext displayContext) {
        SackContentsReader contents = SackContents.of(stack);
        return contents != null && !contents.isEmpty();
    }

    @Override
    public MapCodec<SackHasContentsProperty> getCodec() {
        return CODEC;
    }

    static {
        CODEC = MapCodec.unit(new SackHasContentsProperty());
    }
}
