package derekahedron.invexp.mixin;

import derekahedron.invexp.entity.player.PlayerEntityDuck;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

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