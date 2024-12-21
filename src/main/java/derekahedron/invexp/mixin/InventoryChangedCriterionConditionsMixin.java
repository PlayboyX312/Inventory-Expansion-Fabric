package derekahedron.invexp.mixin;

import derekahedron.invexp.sack.SackContents;
import derekahedron.invexp.sack.SackItemPredicate;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.item.ItemPredicate;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(InventoryChangedCriterion.Conditions.class)
public class InventoryChangedCriterionConditionsMixin {

    /**
     * Modify the predicate list so all predicates also test sack contents
     */
    @ModifyVariable(
            method = "matches",
            at = @At("STORE")
    )
    private @NotNull List<ItemPredicate> modifyPredicateList(@NotNull List<ItemPredicate> list) {
        List<ItemPredicate> modified = new ObjectArrayList<>(list.size());
        for (ItemPredicate predicate : list) {
            modified.add(new SackItemPredicate(predicate));
        }
        return modified;
    }

    /**
     * Modify the item to be tested to a nested item that will pass the test
     */
    @ModifyArg(
            method = "matches",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/predicate/item/ItemPredicate;test(Lnet/minecraft/item/ItemStack;)Z"
            ),
            index = 0
    )
    private ItemStack changeTestItem(ItemStack stack) {
        SackContents contents = SackContents.of(stack);
        if (contents != null && !contents.isEmpty()) {
            InventoryChangedCriterion.Conditions self = (InventoryChangedCriterion.Conditions) (Object) this;
            for (ItemStack nestedStack : contents.getStacks()) {
                if (self.items().getFirst().test(nestedStack)) {
                    return nestedStack;
                }
            }
        }
        return stack;
    }
}
