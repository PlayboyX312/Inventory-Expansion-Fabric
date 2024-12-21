package derekahedron.invexp.block.dispenser;

import derekahedron.invexp.quiver.QuiverContents;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;

/**
 * Dispenser Behavior for Quiver that allow sacks to dispense their selected arrow
 */
public class QuiverDispenserBehavior extends FallibleItemDispenserBehavior {

    /**
     * Dispenser behavior for quivers
     *
     * @param pointer Pointer to the dispenser
     * @param stack Quiver stack that is being dispensed
     * @return Quiver stack after dispense
     */
    @Override
    public final ItemStack dispense(BlockPointer pointer, ItemStack stack) {
        QuiverContents contents = QuiverContents.of(stack);
        // Fail dispense if invalid or empty
        if (contents == null || contents.isEmpty()) {
            setSuccess(false);
            playSound(pointer);
            spawnParticles(pointer, pointer.state().get(DispenserBlock.FACING));
            return stack;
        }

        ItemStack selectedStack = contents.getSelectedStack().copy();
        DispenserBehavior behavior = DispenserBlock.BEHAVIORS.getOrDefault(selectedStack.getItem(), null);
        if (behavior instanceof ProjectileDispenserBehavior projectileBehavior) {
            // Use projectile behavior if it exists
            selectedStack = projectileBehavior.dispense(pointer, selectedStack);
        }
        else {
            // If, somehow, the quiver has an item without a projectile behavior, dispense
            // regularly
            selectedStack = super.dispense(pointer, selectedStack);
        }

        // Update selected stack and try to add remainder back into quiver
        contents.updateSelectedStack(selectedStack, (itemStack -> {
            contents.add(itemStack);
            if (!itemStack.isEmpty()) {
                addStackOrSpawn(pointer, itemStack);
            }
        }));
        return stack;
    }
}
