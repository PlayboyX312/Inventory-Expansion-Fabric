package derekahedron.invexp.mixin.client;

import derekahedron.invexp.component.types.SackContentsComponent;
import derekahedron.invexp.sack.ImmutableSackContents;
import derekahedron.invexp.util.ContainerItemSlotDragger;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public class HandledScreenMixin {

    @Unique
    private Slot currentSlot;
    @Unique
    private boolean hasMoved;
    @Unique
    private SackContentsComponent openContentsComponent;

    /**
     * Start dragging a container item.
     */
    @Inject(
            method = "mouseClicked",
            at = @At("HEAD")
    )
    private void startDraggingContainer(
            double mouseX, double mouseY, int button, @NotNull CallbackInfoReturnable<Boolean> cir
    ) {
        HandledScreen<?> self = (HandledScreen<?>) (Object) this;
        if (ContainerItemSlotDragger.of(self.getScreenHandler().getCursorStack()) != null) {
            currentSlot = self.getSlotAt(mouseX, mouseY);
            hasMoved = false;
        }
    }

    /**
     * Handle hovering when dragging a container item.
     */
    @Inject(
            method = "mouseDragged",
            at = @At("HEAD"),
            cancellable = true
    )
    private void dragContainer(
            double mouseX, double mouseY, int button, double deltaX, double deltaY,
            @NotNull CallbackInfoReturnable<Boolean> cir
    ) {
        HandledScreen<?> self = (HandledScreen<?>) (Object) this;
        // Vanilla checks for dragging
        if (self.heldButtonType == 2 ||
                !self.cursorDragging ||
                self.touchDragSlotStart != null ||
                self.client == null ||
                self.client.options.touchscreen.getValue()) {
            return;
        }

        // Make sure there is a valid slot
        Slot slot = self.getSlotAt(mouseX, mouseY);
        if (slot == null) {
            return;
        }

        // Check that there is a valid dragger
        ItemStack cursorStack = self.getScreenHandler().getCursorStack();
        ContainerItemSlotDragger dragger = ContainerItemSlotDragger.of(cursorStack);
        if (dragger == null) {
            return;
        }

        // If current slot is not set, update current slot
        if (currentSlot == null) {
            currentSlot = slot;
        }
        else if (currentSlot != slot) {
            if (!hasMoved) {
                hasMoved = true;
                self.cursorDragSlots.clear();
                dragger.onHover(currentSlot, self);
            }
            currentSlot = slot;
            dragger.onHover(currentSlot, self);
        }
        cir.setReturnValue(true);
        cir.cancel();
    }

    /**
     * Reset values when stop dragging container
     */
    @Inject(
            method = "mouseReleased",
            at = @At("HEAD"),
            cancellable = true
    )
    private void finishDraggingContainer(
            double mouseX, double mouseY, int button, @NotNull CallbackInfoReturnable<Boolean> cir
    ) {
        HandledScreen<?> self = (HandledScreen<?>) (Object) this;
        if (hasMoved && ContainerItemSlotDragger.of(self.getScreenHandler().getCursorStack()) != null) {
            hasMoved = false;
            currentSlot = null;
            self.cursorDragging = false;
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    /**
     * If the stack is being hovered over, mark the component as open before rendering, and store the component
     * as a unique variable.
     */
    @Inject(
            method = "drawSlot",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawItem(Lnet/minecraft/item/ItemStack;III)V"
            )
    )
    private void drawOpenSack(DrawContext context, @NotNull Slot slot, @NotNull CallbackInfo ci) {
        openContentsComponent = null;
        HandledScreen<?> self = (HandledScreen<?>) (Object) this;
        ItemStack stack = slot.getStack();
        if (slot.disablesDynamicDisplay() ||
                self.client == null ||
                self.focusedSlot == null ||
                self.focusedSlot.getStack() != stack ||
                !self.getScreenHandler().canInsertIntoSlot(slot)) {
            return;
        }
        ItemStack cursorStack = self.getScreenHandler().getCursorStack();
        ImmutableSackContents contents = ImmutableSackContents.of(stack);
        // Do not display as open if the sack is empty or if you cannot insert a non-empty cursor stack
        if (contents == null || contents.isEmpty() || (!cursorStack.isEmpty() && !contents.canTryInsert(cursorStack))) {
            return;
        }
        openContentsComponent = contents.component;
        openContentsComponent.isOpen = true;
    }

    /**
     * Close the component after rendering as open.
     */
    @Inject(
            method = "drawSlot",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawItem(Lnet/minecraft/item/ItemStack;III)V",
                    shift = At.Shift.AFTER
            )
    )
    private void finishDrawingOpenSack(DrawContext context, Slot slot, @NotNull CallbackInfo ci) {
        if (openContentsComponent != null) {
            openContentsComponent.isOpen = false;
        }
        openContentsComponent = null;
    }
}
