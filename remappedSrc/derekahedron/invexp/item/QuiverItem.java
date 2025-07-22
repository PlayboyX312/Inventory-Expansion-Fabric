package derekahedron.invexp.item;

import derekahedron.invexp.item.tooltip.QuiverTooltipData;
import derekahedron.invexp.quiver.QuiverContents;
import derekahedron.invexp.sound.InvExpSoundEvents;
import derekahedron.invexp.util.InvExpUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.ClickType;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Quiver Item. Stores stacks of arrows in a QuiverContentsComponent.
 * Arrows can be added and removed by clicking the stack, and are also inserted
 * automatically on pickup. Arrows are used directly from the quiver's selected stack.
 */
public class QuiverItem extends Item {
    public static final int FULL_ITEM_BAR_COLOR = ColorHelper.fromFloats(1.0F, 1.0F, 0.33F, 0.33F);
    public static final int ITEM_BAR_COLOR = ColorHelper.fromFloats(1.0F, 0.44F, 0.53F, 1.0F);

    /**
     * Create a new Quiver Item from the given settings
     *
     * @param settings  Settings to create the quiver with
     */
    public QuiverItem(net.minecraft.item.Item.Settings settings) {
        super(settings);
    }

    /**
     * Adds or removes an arrow item to/from the quiver contents
     *
     * @param quiverStack   the stack the cursor holds
     * @param slot          the clicked slot
     * @param clickType     how the stack was clicked
     * @param player        the player who clicked the stack
     * @return              true if the click had an effect; false otherwise
     */
    @Override
    public boolean onStackClicked(ItemStack quiverStack, Slot slot, ClickType clickType, PlayerEntity player) {
        // Make sure this is actually a valid quiver
        QuiverContents contents = QuiverContents.of(quiverStack);
        if (contents == null) {
            return false;
        }
        ItemStack otherStack = slot.getStack();
        if (clickType == ClickType.LEFT && !otherStack.isEmpty()) {
            if (!contents.canTryInsert(otherStack)) {
                // Don't do anything if the other stack is not an arrow
                return false;
            }
            else if (contents.add(slot, player) > 0) {
                // If added, play sound and update screen handler
                playInsertSound(player);
                InvExpUtil.onContentChanged(player);
                return true;
            }
            else {
                // If you cannot add, play fail sound
                playInsertFailSound(player);
                return true;
            }
        }
        else if (clickType == ClickType.RIGHT && otherStack.isEmpty()) {
            if (contents.popSelectedStack(slot)) {
                // If removed, play sound and update handler
                playRemoveOneSound(player);
                InvExpUtil.onContentChanged(player);
            }
            // Always return true so quiver stays in cursor slot
            return true;
        }
        return false;
    }

    /**
     * Adds or removes an arrow item to/from the quiver contents
     *
     * @param quiverStack           the slot's stack
     * @param otherStack            the stack the cursor holds
     * @param slot                  the clicked slot
     * @param clickType             how the stack was clicked
     * @param player                player who clicked the stack
     * @param cursorStackReference  reference for setting the cursor stack
     * @return                      true if the click had an effect; false otherwise
     */
    @Override
    public boolean onClicked(
            ItemStack quiverStack, ItemStack otherStack, Slot slot, ClickType clickType,
            PlayerEntity player, StackReference cursorStackReference
    ) {
        // Make sure this is actually a valid quiver
        QuiverContents contents = QuiverContents.of(quiverStack);
        if (contents == null) {
            return false;
        }

        if (clickType == ClickType.LEFT && !otherStack.isEmpty()) {
            if (!contents.canTryInsert(otherStack)) {
                // Don't do anything if the other stack is not an arrow
                return false;
            }
            else if (contents.add(otherStack) > 0) {
                // If added, play sound and update screen handler
                playInsertSound(player);
                InvExpUtil.onContentChanged(player);
                return true;
            }
            else {
                // If you cannot add, play fail sound
                playInsertFailSound(player);
                return true;
            }
        }
        else if (clickType == ClickType.RIGHT && otherStack.isEmpty()) {
            if (slot.canTakePartial(player)) {
                ItemStack poppedStack = contents.popSelectedStack();
                if (!poppedStack.isEmpty()) {
                    // If removed, play sound and update handler
                    cursorStackReference.set(poppedStack);
                    playRemoveOneSound(player);
                }
                // Always return true so quiver stays in cursor slot
                return true;
            }
        }
        return false;
    }

    /**
     * Only show item bar if the quiver has contents
     *
     * @param quiverStack   quiver stack to test
     * @return              true if the quiver is not empty; false otherwise
     */
    @Override
    public boolean isItemBarVisible(ItemStack quiverStack) {
        QuiverContents contents = QuiverContents.of(quiverStack);
        return contents != null && !contents.isEmpty();
    }

    /**
     * Get how full the quiver bar should display
     *
     * @param quiverStack   quiver stack to test
     * @return              int 0-13 representing how full the quiver is
     */
    @Override
    public int getItemBarStep(ItemStack quiverStack) {
        QuiverContents contents = QuiverContents.of(quiverStack);
        if (contents == null) {
            return 0;
        }
        return Math.min(
                13,
                1 + MathHelper.multiplyFraction(contents.getFillFraction(), 12)
        );
    }

    /**
     * Display a different item bar color if the quiver is full
     *
     * @param quiverStack   quiver stack to test
     * @return              color that the quiver bar should display
     */
    @Override
    public int getItemBarColor(ItemStack quiverStack) {
        QuiverContents contents = QuiverContents.of(quiverStack);
        if (contents == null || contents.getTotalOccupancy().compareTo(contents.getMaxQuiverOccupancy()) < 0) {
            return ITEM_BAR_COLOR;
        }
        else {
            return FULL_ITEM_BAR_COLOR;
        }
    }

    /**
     * Adds quiver tooltip data to stack
     *
     * @param quiverStack   quiver stack to get tooltip from
     * @return              Optional tooltip data to add to the stack
     */
    @Override
    public Optional<TooltipData> getTooltipData(ItemStack quiverStack) {
        if (InvExpUtil.shouldDisplayTooltip(quiverStack)) {
            // Return quiver tooltip data if the quiver contents are valid
            QuiverContents contents = QuiverContents.of(quiverStack);
            if (contents != null) {
                return Optional.of(new QuiverTooltipData(contents));
            }
        }
        return Optional.empty();
    }

    /**
     * Drop all quiver contents when item entity is destroyed
     *
     * @param entity    ItemEntity that was destroyed
     */
    @Override
    public void onItemEntityDestroyed(@NotNull ItemEntity entity) {
        QuiverContents contents = QuiverContents.of(entity.getStack());
        if (contents == null || contents.isEmpty()) {
            return;
        }
        ItemUsage.spawnItemContents(entity, contents.popAllStacks());
    }

    /**
     * Make sure the contents are validated when ticked in a player inventory.
     *
     * @param quiverStack   quiver stack to tick
     * @param world         world the stack is ticked in
     * @param entity        the entity holding the item; usually a player
     * @param slot          slot the item is in
     * @param selected      whether the item is in the selected hotbar slot
     */
    @Override
    public void inventoryTick(ItemStack quiverStack, World world, Entity entity, int slot, boolean selected) {
        if (entity instanceof PlayerEntity player) {
            QuiverContents contents = QuiverContents.of(quiverStack);
            if (contents == null) {
                return;
            }
            contents.validate(player);
        }
    }

    /**
     * Play quiver remove sound
     *
     * @param entity    entity to play sound from
     */
    public static void playRemoveOneSound(@NotNull Entity entity) {
        entity.playSound(
                InvExpSoundEvents.ITEM_QUIVER_REMOVE_ONE,
                0.8F,
                0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F
        );
    }

    /**
     * Play quiver insert sound
     *
     * @param entity    entity to play sound from
     */
    public static void playInsertSound(@NotNull Entity entity) {
        entity.playSound(
                InvExpSoundEvents.ITEM_QUIVER_INSERT,
                0.8F,
                0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F
        );
    }

    /**
     * Play quiver insert fail sound
     *
     * @param entity    entity to play sound from
     */
    public static void playInsertFailSound(@NotNull Entity entity) {
        entity.playSound(
                InvExpSoundEvents.ITEM_SACK_INSERT_FAIL,
                1.0F,
                1.0F
        );
    }
}
