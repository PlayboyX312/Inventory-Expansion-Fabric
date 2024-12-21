package derekahedron.invexp.mixin;

import derekahedron.invexp.sack.SackContents;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(TemptGoal.class)
public class TemptGoalMixin {

    /**
     * Modify the argument here to test for the held item in the sack.
     */
    @ModifyArgs(
            method = "isTemptedBy",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/function/Predicate;test(Ljava/lang/Object;)Z"
            )
    )
    private void isTemptedBySelectedStack(@NotNull Args args) {
        if (args.get(0) instanceof ItemStack stack) {
            args.set(0, SackContents.selectedStackOf(stack));
        }
    }
}
