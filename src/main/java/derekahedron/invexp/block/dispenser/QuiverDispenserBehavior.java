package derekahedron.invexp.block.dispenser;

import derekahedron.invexp.quiver.QuiverContents;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;
import org.jetbrains.annotations.NotNull;

/**
 * Dispenser Behavior for Quiver that allow sacks to dispense their selected arrow
 */
public class QuiverDispenserBehavior extends FallibleItemDispenserBehavior {

    @Override
    public final ItemStack dispense(@NotNull BlockPointer pointer, ItemStack stack) {
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
