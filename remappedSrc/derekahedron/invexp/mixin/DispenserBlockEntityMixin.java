package derekahedron.invexp.mixin;

import derekahedron.invexp.block.entity.DispenserBlockEntityDuck;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

/**
 * Adds custom field on dispenser. Stores a usage buffer that collects inserted stacks
 * instead of actually adding them. This is used to collect leftover stacks when a sack is being used.
 */
@Mixin(DispenserBlockEntity.class)
public class DispenserBlockEntityMixin implements DispenserBlockEntityDuck {

    @Unique
    List<ItemStack> usageBuffer;

    @Override
    public @Nullable List<ItemStack> invexp$getUsageBuffer() {
        return usageBuffer;
    }

    @Override
    public void invexp$setUsageBuffer(@Nullable List<ItemStack> usageBuffer) {
        this.usageBuffer = usageBuffer;
    }
}
