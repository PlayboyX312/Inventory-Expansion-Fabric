package derekahedron.invexp.block.dispenser;

import derekahedron.invexp.sack.SackContents;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;

/**
 * Dispenser Behavior for Sacks that allow sacks to dispense their selected item
 */
public class SackDispenserBehavior extends FallibleItemDispenserBehavior {

    /**
     * Dispenser behavior for sacks
     *
     * @param pointer Pointer to the dispenser
     * @param stack Sack stack that is being dispensed
     * @return Sack stack after dispense
     */
    @Override
    public ItemStack dispense(BlockPointer pointer, ItemStack stack) {
        SackContents contents = SackContents.of(stack);
        // Fail dispense if invalid or empty
        if (contents == null || contents.isEmpty()) {
            setSuccess(false);
            playSound(pointer);
            spawnParticles(pointer, pointer.state().get(DispenserBlock.FACING));
            return stack;
        }

        ItemStack selectedStack = contents.getSelectedStack().copy();
        DispenserBehavior behavior = DispenserBlock.BEHAVIORS.getOrDefault(selectedStack.getItem(), null);
        if (behavior != null) {
            // Use dispenser behavior of selected stack if it exists
            selectedStack = behavior.dispense(pointer, selectedStack);
        }
        else {
            // Otherwise, default to regular dispensing
            selectedStack = super.dispense(pointer, selectedStack);
        }

        // Update selected stack and try to add remainder back into sack
        contents.updateSelectedStack(selectedStack, (itemStack -> {
            contents.add(itemStack);
            if (!itemStack.isEmpty()) {
                addStackOrSpawn(pointer, itemStack);
            }
        }));
        return stack;
    }
}
