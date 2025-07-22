package derekahedron.invexp.sack;

import derekahedron.invexp.InventoryExpansion;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Stores information related to the active usage of a sack. The values here are modified
 * as the player uses a sack, and when the player is done using the sack, the sack is updated.
 * Acts as a buffer for modifying the stack in hand.
 * Also stores item stack instances when the user is not using a sack. This is done so ItemStacks
 * like the active item stack remain the same instance when the user is not using the sack.
 */
public class SackUsage {
    public final ItemStack sackStack;
    public ItemStack selectedStack;
    public ItemStack originalSelectedStack;

    /**
     * Creates an all-new sack usage from sack contents.
     *
     * @param contents  contents to create the usage from
     */
    public SackUsage(@NotNull SackContentsReader contents) {
        this(contents, null);
    }

    /**
     * Creates a new sack usage from an existing sack contents, as well as re-using the given
     * selectedStack instance if it matches the selected stack in the contents.
     *
     * @param contents                  contents to create the usage from
     * @param previousSelectedStack     existing selected stack to possibly carry over from
     */
    public SackUsage(@NotNull SackContentsReader contents, @Nullable ItemStack previousSelectedStack) {
        sackStack = contents.getSackStack();
        selectedStack = contents.copySelectedStack(previousSelectedStack);
        originalSelectedStack = selectedStack.copy();
    }

    /**
     * Applies changes made to the selected stack to the contents
     *
     * @param leftoverStackConsumer     handle leftover stack
     */
    public void update(@NotNull Entity entity, @NotNull Consumer<ItemStack> leftoverStackConsumer) {
        ItemStack leftoverStack = ItemStack.EMPTY;
        SackContents contents = SackContents.of(sackStack, entity.getWorld());
        if (contents == null) {
            InventoryExpansion.LOGGER.warn("Contents of {} is invalid when updating usage!", sackStack);
            return;
        }

        // Original selected stack should never be empty, but we can handle it if it is
        if (originalSelectedStack.isEmpty()) {
            if (!selectedStack.isEmpty()) {
                SackContents.Builder builder = contents.getBuilder();
                leftoverStack = selectedStack.copy();
                if (builder.add(leftoverStack, 0) > 0) {
                    builder.apply();
                }
            }
        }
        else {
            if (selectedStack.isEmpty()) {
                // Remove original selected stack if selected stack is now empty
                SackContents.Builder builder = contents.getBuilder();
                if (builder.remove(originalSelectedStack) != originalSelectedStack.getCount()) {
                    InventoryExpansion.LOGGER.warn("ItemStack {} not fully removed.", originalSelectedStack);
                }
                builder.apply();
            }
            else if (ItemStack.areItemsAndComponentsEqual(originalSelectedStack, selectedStack)) {
                // If stacks match, add/remove difference
                int countDiff = selectedStack.getCount() - originalSelectedStack.getCount();
                if (countDiff > 0) {
                    // Add stacks for positive difference
                    SackContents.Builder builder = contents.getBuilder();
                    leftoverStack = selectedStack.copyWithCount(countDiff);
                    if (builder.add(leftoverStack, 0) > 0) {
                        builder.apply();
                    }
                }
                else if (countDiff < 0) {
                    // Remove stacks for negative difference
                    SackContents.Builder builder = contents.getBuilder();
                    if (builder.remove(originalSelectedStack, -countDiff) != -countDiff) {
                        InventoryExpansion.LOGGER.warn(
                                "Count difference ({}) for {} not fully removed",
                                -countDiff, originalSelectedStack
                        );
                    }
                    builder.apply();
                }
            }
            else {
                // Stack is fully replaced
                SackContents.Builder builder = contents.getBuilder();
                leftoverStack = selectedStack.copy();
                if (builder.replaceSelectedStack(leftoverStack, originalSelectedStack.getCount())) {
                    builder.apply();
                }
            }
        }
        // Make new selected stacks
        selectedStack = contents.copySelectedStack(selectedStack);
        originalSelectedStack = selectedStack.copy();

        if (!leftoverStack.isEmpty()) {
            leftoverStackConsumer.accept(leftoverStack);
        }
    }
}
