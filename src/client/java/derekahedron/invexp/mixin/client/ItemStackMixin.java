package derekahedron.invexp.mixin.client;

import derekahedron.invexp.gui.tooltip.SackTooltipComponent;
import derekahedron.invexp.sack.SackDefaultManager;
import derekahedron.invexp.sack.SackType;
import derekahedron.invexp.sack.SacksHelper;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.math.Fraction;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Unique
    private static final boolean SHOW_TOOLTIP = false;
    @Unique
    private static final Text SACK_DATA_TOOLTIP = Text.translatable("item.invexp.sack.data_tooltip")
            .formatted(Formatting.DARK_GRAY);

    @Inject(
            method = "appendTooltip",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/component/MergedComponentMap;size()I"
            )
    )
    private void addSackTypeTooltip(Item.TooltipContext context, TooltipDisplayComponent displayComponent, @Nullable PlayerEntity player, TooltipType type, Consumer<Text> textConsumer, CallbackInfo ci) {
        if (SHOW_TOOLTIP && type.isAdvanced() && SackDefaultManager.getInstance() != null) {
            ItemStack self = (ItemStack) (Object) this;
            RegistryKey<SackType> sackType = SacksHelper.getSackType(self);
            if (sackType != null) {
                textConsumer.accept(SACK_DATA_TOOLTIP);
                textConsumer.accept(Text.literal(sackType.getValue().toString()).formatted(Formatting.DARK_GRAY));
                Fraction sackWeight = SacksHelper.getSackWeight(self);
                if (!sackWeight.equals(Fraction.getFraction(1, 64))) {
                    textConsumer.accept(SackTooltipComponent.formatWeight(sackWeight).formatted(Formatting.DARK_GRAY));
                }
            }
        }
    }
}
