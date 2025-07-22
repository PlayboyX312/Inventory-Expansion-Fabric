package derekahedron.invexp.component.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import derekahedron.invexp.component.InvExpDataComponentTypes;
import derekahedron.invexp.item.QuiverItem;
import derekahedron.invexp.quiver.QuiverHelper;
import derekahedron.invexp.sack.SackDefaultManager;
import derekahedron.invexp.util.DataPackChangeDetector;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.tag.ItemTags;
import org.apache.commons.lang3.math.Fraction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


/**
 * Stores data for quiver contents: stacks and selected index. Data is immutable,
 * so modifying is done through QuiverContents. Also stores extra data that is dependent on
 * DataPacks. That data should not be encoded.
 */
public class QuiverContentsComponent {
    public static final Codec<QuiverContentsComponent> CODEC;
    public static final PacketCodec<RegistryByteBuf, QuiverContentsComponent> PACKET_CODEC;
    public static final QuiverContentsComponent DEFAULT;

    public final List<ItemStack> stacks;
    public final int selectedIndex;
    private final ExtraData extraData;

    /**
     * Creates a new QuiverContentsComponent from the given values.
     *
     * @param stacks            list of stacks to give the component
     * @param selectedIndex     selected index of the component
     */
    public QuiverContentsComponent(@NotNull List<ItemStack> stacks, int selectedIndex) {
        this.stacks = stacks;
        this.selectedIndex = selectedIndex;
        this.extraData = new ExtraData();
    }

    /**
     * Creates a new empty QuiverContentsComponent.
     */
    public QuiverContentsComponent() {
        this(List.of(), -1);
    }

    /**
     * Gets stacks for the component.
     *
     * @return  list of stacks in the component
     */
    public @NotNull List<ItemStack> getStacks() {
        return stacks;
    }

    /**
     * Gets selected index for the component.
     *
     * @return  selected index of the component
     */
    public int getSelectedIndex() {
        return selectedIndex;
    }

    /**
     * Gets the total occupancy stored in the extra data, re-syncing if necessary.
     *
     * @return  total occupancy of the component
     */
    public @NotNull Fraction getTotalOccupancy() {
        extraData.sync();
        return extraData.totalOccupancy;
    }

    /**
     * Gets the validity of the component, re-syncing if necessary.
     *
     * @return  if the component is valid
     */
    public boolean isValid() {
        extraData.sync();
        return extraData.isValid;
    }

    /**
     * Checks if the component has stacks.
     *
     * @return  if the component is empty
     */
    public boolean isEmpty() {
        return stacks.isEmpty();
    }

    /**
     * Returns the selected stack stored in the component
     *
     * @return  the selected stack; EMPTY if there is none
     */
    public @NotNull ItemStack getSelectedStack() {
        if (isEmpty()) {
            return ItemStack.EMPTY;
        }
        return stacks.get(selectedIndex);
    }

    /**
     * Checks for equality between QuiverContentComponents
     *
     * @return  true if the objects are equal; false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        else if (o instanceof QuiverContentsComponent component) {
            // First check sizes of both components
            if (selectedIndex != component.selectedIndex || stacks.size() != component.stacks.size()) {
                return false;
            }
            // Then check stacks
            for (int i = 0; i < stacks.size(); i++) {
                if (!ItemStack.areEqual(stacks.get(i), component.stacks.get(i))) {
                    return false;
                }
            }
            // If no discrepancies are found, objects are equal
            return true;
        }
        else {
            return false;
        }
    }

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ItemStack.CODEC.listOf().fieldOf("contents").forGetter(QuiverContentsComponent::getStacks),
                Codec.INT.optionalFieldOf("selected_index", -1).forGetter(QuiverContentsComponent::getSelectedIndex)
        ).apply(instance, QuiverContentsComponent::new));
        PACKET_CODEC = PacketCodec.tuple(
                ItemStack.PACKET_CODEC.collect(PacketCodecs.toList()), QuiverContentsComponent::getStacks,
                PacketCodecs.INTEGER, QuiverContentsComponent::getSelectedIndex,
                QuiverContentsComponent::new
        );
        DEFAULT = new QuiverContentsComponent();
    }

    /**
     * Helper for getting the QuiverContentsComponent from a valid stack.
     *
     * @param stack     stack to get the component from
     * @return          QuiverContentsComponent of the stack; null if invalid
     */
    public static @Nullable QuiverContentsComponent getComponent(@Nullable ItemStack stack) {
        if (stack != null && !stack.isEmpty() && stack.getItem() instanceof QuiverItem) {
            return stack.get(InvExpDataComponentTypes.QUIVER_CONTENTS);
        }
        else {
            return null;
        }
    }

    /**
     * Stores validity and total occupancy of the component. Validity is reliant on DataPack values.
     * If component defaults become reliant on data pack values, total occupancy will also be reliant on
     * DataPack values.
     */
    private class ExtraData extends DataPackChangeDetector {
        Fraction totalOccupancy;
        boolean isValid;

        /**
         * Evaluate the validity and total occupancy of the component.
         */
        @Override
        public void evaluate() {
            totalOccupancy = Fraction.ZERO;
            isValid = true;

            if (SackDefaultManager.getInstance() == null) {
                isValid = false;
                return;
            }

            for (ItemStack nestedStack : stacks) {
                totalOccupancy = totalOccupancy.add(QuiverHelper.getOccupancyOfStack(nestedStack));
                // Makes sure all items are arrows
                if (!nestedStack.isIn(ItemTags.ARROWS)) {
                    isValid = false;
                }
            }
        }
    }
}
