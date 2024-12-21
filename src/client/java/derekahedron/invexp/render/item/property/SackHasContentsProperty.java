package derekahedron.invexp.render.item.property;

import com.mojang.serialization.MapCodec;
import derekahedron.invexp.sack.SackContents;
import net.minecraft.client.render.item.property.bool.BooleanProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
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
    public boolean getValue(
            ItemStack sackStack, @Nullable ClientWorld world, @Nullable LivingEntity user,
            int seed, ModelTransformationMode modelTransformationMode
    ) {
        SackContents contents = SackContents.of(sackStack);
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
