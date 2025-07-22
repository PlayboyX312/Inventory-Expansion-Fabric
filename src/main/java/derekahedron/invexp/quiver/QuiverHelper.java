package derekahedron.invexp.quiver;

import derekahedron.invexp.component.InvExpDataComponentTypes;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.math.Fraction;
import org.jetbrains.annotations.NotNull;

/**
 * Contains helper functions and default values for data components related to quivers.
 */
public class QuiverHelper {
    public static final Fraction DEFAULT_MAX_QUIVER_OCCUPANCY = Fraction.ZERO;
    public static final int DEFAULT_MAX_QUIVER_STACKS = 0;

    /**
     * Return the maximum occupancy fraction this item can hold as a quiver.
     *
     * @param stack     stack to test
     * @return          maximum quiver occupancy this item can hold as a quiver
     */
    public static @NotNull Fraction getMaxQuiverOccupancy(@NotNull ItemStack stack) {
        return stack.getOrDefault(InvExpDataComponentTypes.MAX_QUIVER_OCCUPANCY, DEFAULT_MAX_QUIVER_OCCUPANCY);
    }

    /**
     * Return the maximum number of stacks this item can hold as a quiver.
     *
     * @param stack     stack to test
     * @return          maximum stacks this item can hold as a quiver
     */
    public static int getMaxQuiverStacks(@NotNull ItemStack stack) {
        return stack.getOrDefault(InvExpDataComponentTypes.MAX_QUIVER_STACKS, DEFAULT_MAX_QUIVER_STACKS);
    }

    /**
     * Returns an occupancy fraction one of these items takes up.
     *
     * @param stack     stack to test
     * @return          how much of a stack one of these items takes up
     */
    public static @NotNull Fraction getOccupancy(@NotNull ItemStack stack) {
        return Fraction.getFraction(1, stack.getMaxCount());
    }


    /**
     * Returns an occupancy fraction this stack takes up.
     *
     * @param stack     stack to test
     * @return          how much of a stack the given stack takes up
     */
    public static @NotNull Fraction getOccupancyOfStack(@NotNull ItemStack stack) {
        return Fraction.getFraction(stack.getCount(), stack.getMaxCount());
    }
}
