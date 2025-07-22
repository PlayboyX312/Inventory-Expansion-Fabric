package derekahedron.invexp.render.item.property;

import com.mojang.serialization.MapCodec;
import derekahedron.invexp.component.types.SackContentsComponent;
import net.minecraft.client.render.item.property.bool.BooleanProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Item Property for if a sack is rendered as open
 */
public class SackIsOpenProperty implements BooleanProperty {
    public static final MapCodec<SackIsOpenProperty> CODEC;

    /**
     * Returns true if the stack is a sack and is rendered as open
     */
    @Override
    public boolean test(
            ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity,
            int seed, ItemDisplayContext displayContext) {
        SackContentsComponent component = SackContentsComponent.getComponent(stack);
        return component != null && component.isOpen;
    }

    @Override
    public MapCodec<SackIsOpenProperty> getCodec() {
        return CODEC;
    }

    static {
        CODEC = MapCodec.unit(new SackIsOpenProperty());
    }
}
