package derekahedron.invexp.item;

import derekahedron.invexp.item.tooltip.SackTooltipData;
import derekahedron.invexp.sack.SackContents;
import derekahedron.invexp.sound.InvExpSoundEvents;
import derekahedron.invexp.util.InvExpUtil;
import net.minecraft.component.ComponentMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.item.consume.UseAction;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.ClickType;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Sack Item. Stores stacks of items in a SackContentsComponent.
 * Items can be used directly from the sack and are automatically inserted into the sack
 * on pickup. Items must be of the same sack type in order to be added.
 */
public class SackItem extends Item {
    public static final int FULL_ITEM_BAR_COLOR = ColorHelper.fromFloats(1.0F, 1.0F, 0.33F, 0.33F);
    public static final int ITEM_BAR_COLOR = ColorHelper.fromFloats(1.0F, 0.44F, 0.53F, 1.0F);

    /**
     * Create a new Sack Item from the given settings
     *
     * @param settings  Settings to create the sack with
     */
    public SackItem(net.minecraft.item.Item.Settings settings) {
        super(settings);
    }

    /**
     * Adds or removes an item to/from the sack contents
     *
     * @param sackStack     the stack the cursor holds
     * @param slot          the clicked slot
     * @param clickType     how the stack was clicked
     * @param player        the player who clicked the stack
     * @return              true if the click had an effect; false otherwise
     */
    @Override
    public boolean onStackClicked(ItemStack sackStack, Slot slot, ClickType clickType, PlayerEntity player) {
        // Make sure this is actually a valid sack
        SackContents contents = SackContents.of(sackStack);
        if (contents == null) {
            return false;
        }
        ItemStack otherStack = slot.getStack();
        if (clickType == ClickType.LEFT && !otherStack.isEmpty()) {
            if (!contents.canTryInsert(otherStack)) {
                // Don't do anything if the other stack does not match the types
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
     * Adds or removes an item to/from the sack contents
     *
     * @param sackStack             the slot's stack
     * @param otherStack            the stack the cursor holds
     * @param slot                  the clicked slot
     * @param clickType             how the stack was clicked
     * @param player                player who clicked the stack
     * @param cursorStackReference  reference for setting the cursor stack
     * @return                      true if the click had an effect; false otherwise
     */
    @Override
    public boolean onClicked(
            ItemStack sackStack, ItemStack otherStack, Slot slot, ClickType clickType,
            PlayerEntity player, StackReference cursorStackReference
    ) {
        // Make sure this is actually a valid sack
        SackContents contents = SackContents.of(sackStack);
        if (contents == null) {
            return false;
        }

        if (clickType == ClickType.LEFT && !otherStack.isEmpty()) {
            if (!contents.canTryInsert(otherStack)) {
                // Don't do anything if the other stack does not match the types
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
     * Gets the use action of the selected stack
     *
     * @param sackStack     sack stack to get use action for
     * @return              use action of the selected stack
     */
    @Override
    public UseAction getUseAction(ItemStack sackStack) {
        SackContents contents = SackContents.of(sackStack);
        if (contents == null || contents.isEmpty()) {
            return super.getUseAction(sackStack);
        }
        return contents.getSelectedStack().getUseAction();
    }

    /**
     * Gets the max use time of the selected stack
     *
     * @param sackStack     sack stack to get use time for
     * @param user          user using the sack
     * @return              max use time of the selected stack
     */
    @Override
    public int getMaxUseTime(ItemStack sackStack, LivingEntity user) {
        SackContents contents = SackContents.of(sackStack);
        if (contents == null || contents.isEmpty()) {
            return super.getMaxUseTime(sackStack, user);
        }
        return contents.getSelectedStack().getMaxUseTime(user);
    }

    /**
     * Only show item bar if the sack has contents
     *
     * @param sackStack     sack stack to test
     * @return              true if the sack is not empty; false otherwise
     */
    @Override
    public boolean isItemBarVisible(ItemStack sackStack) {
        SackContents contents = SackContents.of(sackStack);
        return contents != null && !contents.isEmpty();
    }

    /**
     * Get how full the sack bar should display
     *
     * @param sackStack     sack stack to test
     * @return              int 0-13 representing how full the sack is
     */
    @Override
    public int getItemBarStep(ItemStack sackStack) {
        SackContents contents = SackContents.of(sackStack);
        if (contents == null) {
            return 0;
        }
        return Math.min(
                13,
                1 + MathHelper.multiplyFraction(contents.getFillFraction(), 12)
        );
    }

    /**
     * Display a different item bar color if the sack is full
     *
     * @param sackStack     sack stack to test
     * @return              color that the sack bar should display
     */
    @Override
    public int getItemBarColor(ItemStack sackStack) {
        SackContents contents = SackContents.of(sackStack);
        if (contents != null && contents.getTotalWeight() < contents.getMaxSackWeight()) {
            return ITEM_BAR_COLOR;
        }
        else {
            return FULL_ITEM_BAR_COLOR;
        }
    }

    /**
     * Adds quiver tooltip data to stack
     *
     * @param sackStack     sack stack to get tooltip from
     * @return              Optional tooltip data to add to the stack
     */
    @Override
    public Optional<TooltipData> getTooltipData(ItemStack sackStack) {
        if (InvExpUtil.shouldDisplayTooltip(sackStack)) {
            // Return sack tooltip data if the sack contents are valid
            SackContents contents = SackContents.of(sackStack);
            if (contents != null) {
                return Optional.of(new SackTooltipData(contents));
            }
        }
        return Optional.empty();
    }


    /**
     * Drop all sack contents when item entity is destroyed
     *
     * @param entity    ItemEntity that was destroyed
     */
    @Override
    public void onItemEntityDestroyed(@NotNull ItemEntity entity) {
        SackContents contents = SackContents.of(entity.getStack());
        if (contents == null || contents.isEmpty()) {
            return;
        }
        ItemUsage.spawnItemContents(entity, contents.popAllStacks());
    }

    /**
     * Make sure the contents are validated when ticked in a player inventory.
     * Also ticks the selected stack in the sack.
     *
     * @param sackStack     sack stack to tick
     * @param world         world the stack is ticked in
     * @param entity        the entity holding the item; usually a player
     * @param slot          slot the item is in
     * @param selected      whether the item is in the selected hotbar slot
     */
    @Override
    public void inventoryTick(ItemStack sackStack, World world, Entity entity, int slot, boolean selected) {
        if (entity instanceof PlayerEntity player) {
            SackContents contents = SackContents.of(sackStack);
            if (contents == null) {
                return;
            }
            contents.validate(player);

            // After contents are validated, try ticking the selected stack
            if (contents.isEmpty()) {
                return;
            }
            ItemStack selectedStack = contents.copySelectedStack();
            selectedStack.getItem().inventoryTick(selectedStack, world, entity, slot, selected);
            contents.updateSelectedStack(selectedStack, player::giveOrDropStack);
        }
    }

    /**
     * Play sack remove sound
     *
     * @param entity    entity to play sound from
     */
    public static void playRemoveOneSound(@NotNull Entity entity) {
        entity.playSound(
                InvExpSoundEvents.ITEM_SACK_REMOVE_ONE,
                0.8F,
                0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F
        );
    }

    /**
     * Play sack insert sound
     *
     * @param entity    entity to play sound from
     */
    public static void playInsertSound(@NotNull Entity entity) {
        entity.playSound(
                InvExpSoundEvents.ITEM_SACK_INSERT,
                0.8F,
                0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F
        );
    }

    /**
     * Play sack insert fail sound
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
