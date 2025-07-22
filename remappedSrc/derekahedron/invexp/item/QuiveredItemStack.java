package derekahedron.invexp.item;

import derekahedron.invexp.InventoryExpansion;
import derekahedron.invexp.quiver.QuiverContents;
import net.minecraft.component.MergedComponentMap;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * ItemStack that points back to the quiver it came from. Will remove itself from the Quiver
 * when the count is decreased
 */
public class QuiveredItemStack extends ItemStack {
    public final ItemStack quiverStack;

    /**
     * Creates a new ItemStack that points back to the quiver it came from
     *
     * @param stack         Stack in the quiver to make a copy of
     * @param quiverStack   Quiver that should be modified when this stack is changed
     */
    public QuiveredItemStack(@NotNull ItemStack stack, @NotNull ItemStack quiverStack) {
        super(stack.getItem(), stack.getCount(), (MergedComponentMap) stack.getComponents());
        this.quiverStack = quiverStack;
    }

    /**
     * When the count is decreased, remove that many arrows from the quiver
     *
     * @param   count new count
     */
    @Override
    public void setCount(int count) {
        int countDiff = count - getCount();
        if (countDiff < 0) {
            QuiverContents contents = QuiverContents.of(quiverStack);
            if (contents != null) {
                contents.remove(copyWithCount(-countDiff));
            }
        }
        else if (countDiff > 0) {
            InventoryExpansion.LOGGER.warn("QuiveredItemStack count increased unexpectedly! Potential loss of items");
        }
        super.setCount(count);
    }
}
