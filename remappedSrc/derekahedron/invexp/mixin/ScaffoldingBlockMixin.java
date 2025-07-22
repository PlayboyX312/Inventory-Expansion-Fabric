package derekahedron.invexp.mixin;

import derekahedron.invexp.sack.SackContents;
import net.minecraft.block.BlockState;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.block.ScaffoldingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ScaffoldingBlock.class)
public class ScaffoldingBlockMixin {

    /**
     * Scaffolding has a different outline shape if you are holding a scaffolding block.
     * We want to also display that shape if you are holding a sack with scaffolding as a selected stack.
     */
    @Inject(
            method = "getOutlineShape",
            at = @At("HEAD"),
            cancellable = true
    )
    private void getOutlineShapeForSack(
            BlockState state, BlockView world, BlockPos pos, ShapeContext context,
            @NotNull CallbackInfoReturnable<VoxelShape> cir
    ) {
        if (context instanceof EntityShapeContext shapeContext) {
            SackContents contents = SackContents.of(shapeContext.heldItem);
            if (contents != null && !contents.isEmpty() && contents.getSelectedStack().isOf(state.getBlock().asItem())) {
                cir.setReturnValue(VoxelShapes.fullCube());
            }
        }
    }
}
