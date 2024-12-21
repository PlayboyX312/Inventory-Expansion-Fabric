package derekahedron.invexp.sack;

import net.minecraft.item.ItemStack;
import net.minecraft.predicate.item.ItemPredicate;
import org.jetbrains.annotations.NotNull;

/**
 * Special predicate that also tests all items inside the sack as well
 */
public class SackItemPredicate extends ItemPredicate {

    /**
     * Creates a SackItemPredicate from an existing predicate. This predicate tests the
     * given predicate on a stack as well as the sack contents inside a stack.
     *
     * @param predicate     predicate to copy from
     */
    public SackItemPredicate(@NotNull ItemPredicate predicate) {
        super(predicate.items(), predicate.count(), predicate.components(), predicate.subPredicates());
    }

    /**
     * Test the predicate on the stack and all of its contents, returning
     * true if any match.
     *
     * @param stack the input argument
     * @return      true if the predicate matches the stack or any of its contents
     */
    @Override
    public boolean test(ItemStack stack) {
        if (super.test(stack)) {
            return true;
        }
        SackContents contents = SackContents.of(stack);
        if (contents != null && !contents.isEmpty()) {
            for (ItemStack nestedStack : contents.getStacks()) {
                if (nestedStack.isEmpty() && super.test(nestedStack)) {
                    return true;
                }
            }
        }
        return false;
    }
}
