package derekahedron.invexp.mixin.client;

import derekahedron.invexp.component.InvExpDataComponentTypes;
import derekahedron.invexp.component.types.SackContentsComponent;
import derekahedron.invexp.sack.SackContents;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Unique
    private SackContentsComponent openContentsComponent;

    /**
     * Check if a sack is selected. If so, store the components so we don't have to repeat the calculation.
     * Then make a scissor area to prevent the item from rendering outside the sack.
     */
    @Inject(
            method = "renderHotbarItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;getBobbingAnimationTime()I"
            )
    )
    private void calculateSackContentsComponent(
            DrawContext context, int x, int y, RenderTickCounter tickCounter, PlayerEntity player, ItemStack stack,
            int seed, @NotNull CallbackInfo ci
    ) {
        openContentsComponent = null;
        InGameHud self = (InGameHud) (Object) this;
        if (self.client == null || self.client.player == null || (stack != self.client.player.getMainHandStack() && stack != self.client.player.getOffHandStack())) {
            return;
        }
        SackContentsComponent component = SackContentsComponent.getComponent(stack);
        if (component != null && !component.stacks.isEmpty()) {
            openContentsComponent = component;
            context.enableScissor(x, y - 16, x + 16, y + 16);
        }
    }

    /**
     * If there is a non-null contents component, we want to render the
     * selected stack instead. We do this instead of rendering the component via
     * the model as that would make the sack overlay affected by the
     * bobbing animation, which looks odd.
     */
    @ModifyArg(
            method = "renderHotbarItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;III)V"
            )
    )
    private ItemStack renderSelectedStack(ItemStack stack) {
        InGameHud self = (InGameHud) (Object) this;
        if (openContentsComponent != null) {
            return SackContents.selectedStackOf(self.client.player, stack);
        }
        return stack;
    }

    /**
     * Renders the sack overlay and the count of the matching items in the sacks.
     */
    @Inject(
            method = "renderHotbarItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawStackOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;II)V"
            )
    )
    private void renderSackCount(
            DrawContext context, int x, int y, RenderTickCounter tickCounter, PlayerEntity player, ItemStack stack,
            int seed, @NotNull CallbackInfo ci
    ) {
        if (openContentsComponent != null) {
            // Close scissor area
            context.disableScissor();

            // Copy sack with empty open contents and render that
            ItemStack empty = stack.copy();
            empty.set(InvExpDataComponentTypes.SACK_CONTENTS, SackContentsComponent.EMPTY_OPEN);
            context.drawItem(empty, x, y, seed);

            // Gather total count of nested items that match the selected stack
            ItemStack selectedStack = openContentsComponent.getSelectedStack();
            int maxCount = selectedStack.getMaxCount();
            int count = 0;
            for (ItemStack nestedStack : openContentsComponent.stacks) {
                if (ItemStack.areItemsAndComponentsEqual(nestedStack, selectedStack)) {
                    count += nestedStack.getCount();
                    if (count > maxCount) {
                        // If max count is surpassed, return early
                        break;
                    }
                }
            }

            // Do not render for counts 1 and below
            if (count <= 1) {
                return;
            }

            // If count surpasses max count, use max count but render as yellow
            String countLabel;
            if (count <= maxCount) {
                countLabel = String.valueOf(count);
            }
            else {
                countLabel = Formatting.YELLOW + String.valueOf(maxCount);
            }

            // Render count
            InGameHud self = (InGameHud) (Object) this;
            TextRenderer renderer = self.client.textRenderer;
            context.getMatrices().push();
            context.getMatrices().translate(0.0D, 0.0D, 200.0F);
            context.drawText(renderer, countLabel, x + 19 - 2 - renderer.getWidth(countLabel), y, 0xFFFFFF, true);
            context.getMatrices().pop();
        }
    }
}
