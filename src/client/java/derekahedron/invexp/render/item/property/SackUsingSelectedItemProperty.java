package derekahedron.invexp.render.item.property;


import com.mojang.serialization.MapCodec;
import derekahedron.invexp.sack.SackContents;
import net.minecraft.client.render.item.property.bool.BooleanProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import org.jetbrains.annotations.Nullable;

/**
 * Item Property for if a sacks selected item is being used
 */
public class SackUsingSelectedItemProperty implements BooleanProperty {
    public static final MapCodec<SackUsingSelectedItemProperty> CODEC;

    /**
     * Returns true if the selected stack of a sack is being used
     */
    @Override
    public boolean getValue(
            ItemStack sackStack, @Nullable ClientWorld world, @Nullable LivingEntity user,
            int seed, ModelTransformationMode modelTransformationMode
    ) {
        if (user instanceof PlayerEntity player && player.isUsingItem()) {
            ItemStack selectedStack = SackContents.selectedStackOf(player, sackStack);
            return selectedStack != sackStack && selectedStack == player.getActiveItem();
        }
        return false;
    }

    @Override
    public MapCodec<SackUsingSelectedItemProperty> getCodec() {
        return CODEC;
    }

    static {
        CODEC = MapCodec.unit(new SackUsingSelectedItemProperty());
    }
}
