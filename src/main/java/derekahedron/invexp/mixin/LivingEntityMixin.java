package derekahedron.invexp.mixin;

import derekahedron.invexp.entity.player.PlayerEntityDuck;
import derekahedron.invexp.sack.SackUsage;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

	/**
	 * If the player is using a sack, getting stack in hand should return the sack
	 * in usage if applicable.
	 */
	@Inject(
			method = "getStackInHand",
			at = @At("RETURN"),
			cancellable = true
	)
	private void getStackInSack(Hand hand, @NotNull CallbackInfoReturnable<ItemStack> cir) {
		LivingEntity self = (LivingEntity) (Object) this;
		if (self instanceof PlayerEntity player && ((PlayerEntityDuck) player).invexp$isUsingSack()) {
			SackUsage usage = ((PlayerEntityDuck) player).invexp$getUsageForSackStack(cir.getReturnValue());
			if (usage != null) {
				cir.setReturnValue(usage.selectedStack);
			}
		}
	}

	/**
	 * If the player is using a sack, setting stack in hand should set it to the
	 * selected stack in the sack usage if applicable.
	 */
	@Inject(
			method = "setStackInHand",
			at = @At("HEAD"),
			cancellable = true
	)
	private void setStackInSack(Hand hand, ItemStack stack, @NotNull CallbackInfo ci) {
		LivingEntity self = (LivingEntity) (Object) this;
		if (self instanceof PlayerEntity player && ((PlayerEntityDuck) player).invexp$isUsingSack()) {
			SackUsage usage = ((PlayerEntityDuck) player).invexp$getUsageInHand(hand);
			if (usage != null) {
				usage.selectedStack = stack;
				ci.cancel();
			}
		}
	}

	/**
	 * Start player using sack before ticking active item stack.
	 */
	@Inject(
			method = "tickActiveItemStack",
			at = @At("HEAD")
	)
	private void beforeTickActiveItemStack(@NotNull CallbackInfo ci) {
		LivingEntity self = (LivingEntity) (Object) this;
		if (self instanceof PlayerEntity player) {
			((PlayerEntityDuck) player).invexp$startUsingSack();
		}
	}

	/**
	 * Stop player using sack after ticking active item stack.
	 */
	@Inject(
			method = "tickActiveItemStack",
			at = @At("RETURN")
	)
	private void afterTickActiveItemStack(@NotNull CallbackInfo ci) {
		LivingEntity self = (LivingEntity) (Object) this;
		if (self instanceof PlayerEntity player) {
			((PlayerEntityDuck) player).invexp$stopUsingSack();
		}
	}

	/**
	 * Start player using sack before trying to use death protector.
	 */
	@Inject(
			method = "tryUseDeathProtector",
			at = @At("HEAD")
	)
	private void beforeUseDeathProtector(DamageSource source, @NotNull CallbackInfoReturnable<Boolean> cir) {
		LivingEntity self = (LivingEntity) (Object) this;
		if (self instanceof PlayerEntity player) {
			((PlayerEntityDuck) player).invexp$startUsingSack();
		}
	}

	/**
	 * Stop player using sack after trying to use death protector.
	 */
	@Inject(
			method = "tryUseDeathProtector",
			at = @At("RETURN")
	)
	private void afterUseDeathProtector(DamageSource source, @NotNull CallbackInfoReturnable<Boolean> cir) {
		LivingEntity self = (LivingEntity) (Object) this;
		if (self instanceof PlayerEntity player) {
			((PlayerEntityDuck) player).invexp$stopUsingSack();
		}
	}
}