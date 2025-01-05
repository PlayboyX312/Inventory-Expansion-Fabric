package derekahedron.invexp.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import derekahedron.invexp.entity.player.PlayerEntityDuck;
import derekahedron.invexp.quiver.QuiverContents;
import derekahedron.invexp.sack.SackContents;
import derekahedron.invexp.sack.SackUsage;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.function.Predicate;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements PlayerEntityDuck {

    private @Unique boolean usingSack = false;
    private @Unique SackUsage mainHandSackUsage;
    private @Unique SackUsage offHandSackUsage;

    /**
     * If the player is using a sack, getting the equipped stack should return the stack
     * in usage if applicable.
     */
    @Inject(
            method = "getEquippedStack",
            at = @At("RETURN"),
            cancellable = true
    )
    private void getEquippedStackInSack(EquipmentSlot slot, @NotNull CallbackInfoReturnable<ItemStack> cir) {
        if (usingSack && (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND)) {
            SackUsage usage = invexp$getUsageForSackStack(cir.getReturnValue());
            if (usage != null) {
                cir.setReturnValue(usage.selectedStack);
            }
        }
    }

    /**
     * If the player is using a sack, equipping the stack in that slot should set it to the
     * selected stack in the sack usage if applicable.
     */
    @Inject(
            method = "equipStack",
            at = @At("HEAD"),
            cancellable = true
    )
    private void equipStackInSack(EquipmentSlot slot, ItemStack stack, @NotNull CallbackInfo ci) {
        if (usingSack && (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND)) {
            PlayerEntity self = (PlayerEntity) (Object) this;
            // Get the stack that is being held
            ItemStack heldStack;
            switch (slot) {
                case MAINHAND -> heldStack = self.getInventory().getMainHandStack();
                case OFFHAND -> heldStack = self.getInventory().offHand.getFirst();
                default -> throw new IllegalArgumentException("Invalid slot " + slot);
            }
            // If the stack in the slot is a sack being used, replace there instead
            SackUsage usage = invexp$getUsageForSackStack(heldStack);
            if (usage != null) {
                self.onEquipStack(slot, usage.selectedStack, stack);
                usage.selectedStack = stack;
                ci.cancel();
            }
        }
    }

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
            SackContents contents = SackContents.of(heldStack);
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
                usage.update(leftoverStacks::add);
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
     * Gets the sack usage related to the item in the given hand.
     *
     * @param hand  hand to get usage for
     * @return      SackUsage for the given hand; null if there is none
     */
    @Override
    public SackUsage invexp$getUsageInHand(@NotNull Hand hand) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        ItemStack heldStack;
        switch (hand) {
            case MAIN_HAND -> heldStack = self.getMainHandStack();
            case OFF_HAND -> heldStack = self.getOffHandStack();
            default -> throw new IllegalArgumentException("Invalid hand " + hand);
        }
        return invexp$getUsageForSackStack(heldStack);
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
