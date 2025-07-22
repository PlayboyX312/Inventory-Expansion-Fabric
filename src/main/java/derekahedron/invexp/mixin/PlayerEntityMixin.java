package derekahedron.invexp.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import derekahedron.invexp.entity.player.PlayerEntityDuck;
import derekahedron.invexp.quiver.QuiverContents;
import derekahedron.invexp.sack.SackContents;
import derekahedron.invexp.sack.SackContentsReader;
import derekahedron.invexp.sack.SackUsage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.function.Predicate;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements PlayerEntityDuck {

    private @Unique boolean usingSack = false;
    private @Unique SackUsage mainHandSackUsage;
    private @Unique SackUsage offHandSackUsage;


    /**
     * Check if player is currently using sack.
     *
     * @return  true if the player is using a sack; false otherwise
     */
    @Override
    public boolean invexp$isUsingSack() {
        return usingSack;
    }

    /**
     * Start player using sack by creating usages for the items in the main hand and
     * offhand. Merge previous usages if they are of the same sack stack.
     */
    @Override
    public void invexp$startUsingSack() {
        if (usingSack) {
            invexp$stopUsingSack();
        }

        PlayerEntity self = (PlayerEntity) (Object) this;
        SackUsage[] usages = new SackUsage[Hand.values().length];
        for (int i = 0; i < Hand.values().length; i++) {
            ItemStack heldStack = self.getStackInHand(Hand.values()[i]);
            SackContentsReader contents = SackContents.of(heldStack);
            if (contents != null && !contents.isEmpty()) {
                SackUsage usage = invexp$getUsageForSackStack(heldStack);
                if (usage != null) {
                    usages[i] = new SackUsage(contents, usage.selectedStack);
                }
                else {
                    usages[i] = new SackUsage(contents);
                }
            }
        }

        for (int i = 0; i < Hand.values().length; i++) {
            setUsageByHand(Hand.values()[i], usages[i]);
        }

        usingSack = true;
    }

    /**
     * Stop player using sack. Updates sack usages with the new selected stacks.
     */
    @Override
    public void invexp$stopUsingSack() {
        if (!usingSack) {
            return;
        }

        ArrayList<ItemStack> leftoverStacks = new ArrayList<>();
        PlayerEntity self = (PlayerEntity) (Object) this;
        for (Hand hand : Hand.values()) {
            SackUsage usage = getUsageByHand(hand);
            if (usage != null) {
                usage.update(self, leftoverStacks::add);
            }
        }

        usingSack = false;
        for (ItemStack leftoverStack : leftoverStacks) {
            if (!leftoverStack.isEmpty()) {
                self.giveOrDropStack(leftoverStack);
            }
        }
    }

    /**
     * Gets the usage for the player with a sack stack matching the given stack.
     *
     * @param sackStack     sack stack to get usage for
     * @return              sack usage associated with the sack stack; null if there is none
     */
    @Override
    public SackUsage invexp$getUsageForSackStack(ItemStack sackStack) {
        for (Hand hand : Hand.values()) {
            SackUsage usage = getUsageByHand(hand);
            if (usage != null && usage.sackStack == sackStack) {
                return usage;
            }
        }
        return null;
    }


    /**
     * Gets the usage for the player with a selected stack matching the given stack.
     *
     * @param selectedStack     selected stack to get usage for
     * @return                  sack usage associated with the selected stack; null if there is none
     */
    @Override
    public SackUsage invexp$getUsageForSelectedStack(ItemStack selectedStack) {
        for (Hand hand : Hand.values()) {
            SackUsage usage = getUsageByHand(hand);
            if (usage != null && usage.selectedStack == selectedStack) {
                return usage;
            }
        }
        return null;
    }

    /**
     * Sets usage for the given hand.
     *
     * @param hand      hand to set the usage for
     * @param usage     usage to set in the hand
     */
    @Unique
    private void setUsageByHand(@NotNull Hand hand, @Nullable SackUsage usage) {
        switch (hand) {
            case MAIN_HAND -> mainHandSackUsage = usage;
            case OFF_HAND -> offHandSackUsage = usage;
            default -> throw new IllegalArgumentException("Invalid hand " + hand);
        }
    }

    /**
     * Gets usage for the given hand.
     *
     * @param hand  hand to get the usage for
     * @return      usage in the given hand
     */
    @Unique
    private SackUsage getUsageByHand(@NotNull Hand hand) {
        switch (hand) {
            case MAIN_HAND -> {
                return mainHandSackUsage;
            }
            case OFF_HAND -> {
                return offHandSackUsage;
            }
            default -> throw new IllegalArgumentException("Invalid hand " + hand);
        }
    }

    /**
     * Returns new QuiveredItemStack for a quiver with a selected item that matches the
     * given predicate.
     */
    @Inject(
            method = "getProjectileType",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/RangedWeaponItem;getProjectiles()Ljava/util/function/Predicate;",
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    private void getQuiveredProjectile(
            ItemStack stack, @NotNull CallbackInfoReturnable<ItemStack> cir, @Local Predicate<ItemStack> predicate
    ) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        for (int i = 0; i < self.getInventory().size(); i++) {
            QuiverContents contents = QuiverContents.of(self.getInventory().getStack(i));
            if (contents != null) {
                ItemStack projectile = contents.getProjectileStack(predicate);
                if (!projectile.isEmpty()) {
                    cir.setReturnValue(projectile);
                    return;
                }
            }
        }
    }
}
