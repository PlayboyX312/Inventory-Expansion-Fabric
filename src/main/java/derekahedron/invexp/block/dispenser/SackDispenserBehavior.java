package derekahedron.invexp.block.dispenser;

import derekahedron.invexp.block.entity.DispenserBlockEntityDuck;
import derekahedron.invexp.sack.SackContents;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Dispenser Behavior for Sacks that allow sacks to dispense their selected item
 */
public class SackDispenserBehavior extends FallibleItemDispenserBehavior {

    @Override
    public ItemStack dispense(@NotNull BlockPointer pointer, ItemStack stack) {
        SackContents contents = SackContents.of(stack, pointer.world());
        // Fail dispense if invalid or empty
        if (contents == null || contents.isEmpty()) {
            setSuccess(false);
            playSound(pointer);
            spawnParticles(pointer, pointer.state().get(DispenserBlock.FACING));
            return stack;
        }

        // Set buffer to catch all inserted stacks
        List<ItemStack> usageBuffer = new ArrayList<>();
        ((DispenserBlockEntityDuck) pointer.blockEntity()).invexp$setUsageBuffer(usageBuffer);

        ItemStack selectedStack = contents.copySelectedStack();
        DispenserBehavior behavior = DispenserBlock.BEHAVIORS.getOrDefault(selectedStack.getItem(), null);
        if (behavior != null) {
            // Use dispenser behavior of selected stack if it exists
            selectedStack = behavior.dispense(pointer, selectedStack);
        }
        else {
            // Otherwise, default to regular dispensing
            selectedStack = super.dispense(pointer, selectedStack);
        }
        // Remove buffer
        ((DispenserBlockEntityDuck) pointer.blockEntity()).invexp$setUsageBuffer(null);

        // Update selected stack and try to add remainder back into sack
        contents.updateSelectedStack(selectedStack, (leftoverStack -> addStackOrSpawn(pointer, leftoverStack)));

        // Try to add inserted stacks into the sack contents.
        for (ItemStack insertedStack : usageBuffer) {
            contents.add(insertedStack);
            if (!insertedStack.isEmpty()) {
                addStackOrSpawn(pointer, insertedStack);
            }
        }
        return stack;
    }
}
