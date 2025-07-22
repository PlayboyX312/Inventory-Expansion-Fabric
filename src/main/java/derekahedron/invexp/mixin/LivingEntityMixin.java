package derekahedron.invexp.mixin;

import derekahedron.invexp.entity.player.PlayerEntityDuck;
import derekahedron.invexp.sack.SackUsage;
import net.minecraft.entity.EntityEquipment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	@Shadow @Final protected EntityEquipment equipment;

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
		LivingEntity self = (LivingEntity) (Object) this;
		if (self instanceof PlayerEntity player) {
			PlayerEntityDuck playerDuck = (PlayerEntityDuck) player;
			if (playerDuck.invexp$isUsingSack() && (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND)) {
				SackUsage usage = playerDuck.invexp$getUsageForSackStack(cir.getReturnValue());
				if (usage != null) {
					cir.setReturnValue(usage.selectedStack);
				}
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
		LivingEntity self = (LivingEntity) (Object) this;
		if (self instanceof PlayerEntity player) {
			PlayerEntityDuck playerDuck = (PlayerEntityDuck) player;
			if (playerDuck.invexp$isUsingSack() && (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND)) {
				// If the stack in the slot is a sack being used, replace there instead
				SackUsage usage = playerDuck.invexp$getUsageForSackStack(equipment.get(slot));
				if (usage != null) {
					self.onEquipStack(slot, usage.selectedStack, stack);
					usage.selectedStack = stack;
					ci.cancel();
				}
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
	 * Start player using sack before setting tracked data.
	 */
	@Inject(
			method = "onTrackedDataSet",
			at = @At("HEAD")
	)
	private void beforeTrackedDataSet(TrackedData<?> data, @NotNull CallbackInfo ci) {
		LivingEntity self = (LivingEntity) (Object) this;
		if (self instanceof PlayerEntity player &&
				player.getWorld().isClient() &&
				LivingEntity.LIVING_FLAGS.equals(data)
		) {
			((PlayerEntityDuck) player).invexp$startUsingSack();
		}
	}

	/**
	 * Stop player using sack after setting tracked data.
	 */
	@Inject(
			method = "onTrackedDataSet",
			at = @At("RETURN")
	)
	private void afterTrackedDataSet(TrackedData<?> data, @NotNull CallbackInfo ci) {
		LivingEntity self = (LivingEntity) (Object) this;
		if (self instanceof PlayerEntity player &&
				player.getWorld().isClient() &&
				LivingEntity.LIVING_FLAGS.equals(data)
		) {
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