package derekahedron.invexp.component.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import derekahedron.invexp.component.InvExpDataComponentTypes;
import derekahedron.invexp.item.SackItem;
import derekahedron.invexp.sack.SackDefaultManager;
import derekahedron.invexp.sack.SackType;
import derekahedron.invexp.sack.SacksHelper;
import derekahedron.invexp.util.DataPackChangeDetector;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.math.Fraction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Stores data for sack contents: stacks, selected index, and sack types. Data is immutable,
 * so modifying is done through SackContents. Also stores extra data that is dependent on
 * DataPacks. That data should not be encoded.
 */
public class SackContentsComponent {
    public static final Codec<SackContentsComponent> CODEC;
    public static final PacketCodec<RegistryByteBuf, SackContentsComponent> PACKET_CODEC;
    public static final SackContentsComponent DEFAULT;
    public static final SackContentsComponent EMPTY_OPEN;

    public final List<RegistryEntry<SackType>> sackTypes;
    public final List<RegistryKey<SackType>> sackTypeKeys;
    public final List<ItemStack> stacks;
    public final int selectedIndex;
    private final ExtraData extraData;

    // This boolean is only used for rendering the item in the inventory.
    // Should be set to true only when rendering.
    public boolean isOpen = false;

    /**
     * Creates a new SackContentsComponent from the given values.
     *
     * @param sackTypes         sack types to start the component with
     * @param stacks            list of stacks to give the component
     * @param selectedIndex     selected index of the component
     */
    public SackContentsComponent(
            @NotNull List<RegistryEntry<SackType>> sackTypes, @NotNull List<ItemStack> stacks, int selectedIndex
    ) {
        this(
                sackTypes.stream().filter((sackType) -> sackType.getKey().isPresent()).toList(),
                sackTypes.stream().map(RegistryEntry::getKey).filter(Optional::isPresent).map(Optional::get).toList(),
                stacks, selectedIndex);
    }

    public SackContentsComponent(
            @NotNull List<RegistryEntry<SackType>> sackTypes, @NotNull List<RegistryKey<SackType>> sackTypeKeys, @NotNull List<ItemStack> stacks, int selectedIndex
    ) {
        this.sackTypes = sackTypes;
        this.sackTypeKeys = sackTypeKeys;
        this.stacks = stacks;
        this.selectedIndex = selectedIndex;
        this.extraData = new ExtraData();
    }

    /**
     * Creates a new empty SackContentsComponent.
     */
    public SackContentsComponent() {
        this(List.of(), List.of(), -1);
    }

    /**
     * Gets sack types for the component.
     *
     * @return  list of sack types in the component
     */
    public @NotNull List<RegistryEntry<SackType>> getSackTypes() {
        return sackTypes;
    }

    public @NotNull List<RegistryKey<SackType>> getSackTypeKeys() {
        return sackTypeKeys;
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
     * Gets the total weight stored in the extra data, re-syncing if necessary.
     *
     * @return  total weight of the component
     */
    public Fraction getTotalWeight() {
        extraData.sync();
        return extraData.totalWeight;
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
     * Checks for equality between SackContentComponents
     *
     * @return  true if the objects are equal; false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        // Never allow the EMPTY_OPEN instance to be equal to something else
        else if (this == EMPTY_OPEN || o == EMPTY_OPEN) {
            return false;
        }
        else if (o instanceof SackContentsComponent component) {
            // First check sizes of both components
            if (selectedIndex != component.selectedIndex ||
                    stacks.size() != component.stacks.size() ||
                    sackTypes.size() != component.sackTypes.size()) {
                return false;
            }
            // Then check sack types stored
            for (int i = 0; i < sackTypes.size(); i++) {
                if (sackTypes.get(i) != component.sackTypes.get(i)) {
                    return false;
                }
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
                SackType.ENTRY_CODEC.listOf().optionalFieldOf("sack_types", List.of())
                        .forGetter(SackContentsComponent::getSackTypes),
                ItemStack.CODEC.listOf().fieldOf("contents")
                        .forGetter(SackContentsComponent::getStacks),
                Codec.INT.optionalFieldOf("selected_index", -1)
                        .forGetter(SackContentsComponent::getSelectedIndex)
        ).apply(instance, SackContentsComponent::new));
        PACKET_CODEC = PacketCodec.tuple(
                SackType.ENTRY_PACKET_CODEC.collect(PacketCodecs.toList()), SackContentsComponent::getSackTypes,
                ItemStack.PACKET_CODEC.collect(PacketCodecs.toList()), SackContentsComponent::getStacks,
                PacketCodecs.INTEGER, SackContentsComponent::getSelectedIndex,
                SackContentsComponent::new
        );
        DEFAULT = new SackContentsComponent();
        EMPTY_OPEN = new SackContentsComponent();
        EMPTY_OPEN.isOpen = true;
    }

    /**
     * Helper for getting the SackContentsComponent from a valid stack.
     *
     * @param stack     stack to get the component from
     * @return          SackContentsComponent of the stack; null if invalid
     */
    public static @Nullable SackContentsComponent getComponent(@Nullable ItemStack stack) {
        if (stack != null && !stack.isEmpty() && stack.getItem() instanceof SackItem) {
            return stack.get(InvExpDataComponentTypes.SACK_CONTENTS);
        } else {
            return null;
        }
    }

    /**
     * Stores validity and total weight of the component. Both are reliant on the DataPack values.
     */
    private class ExtraData extends DataPackChangeDetector {
        Fraction totalWeight;
        boolean isValid;

        /**
         * Evaluate the validity and total weight of the component.
         */
        @Override
        public void evaluate() {
            totalWeight = Fraction.ZERO;
            isValid = true;

            if (SackDefaultManager.getInstance() == null) {
                isValid = false;
                return;
            }

            // Because references can be decentralized, we use identifiers to check for type equality
            HashSet<Identifier> typeIdsInSack = new HashSet<>();
            HashSet<Identifier> typeIds = new HashSet<>(
                    getSackTypeKeys().stream().map(RegistryKey::getValue).filter(Objects::nonNull).toList()
            );
            for (ItemStack stack : stacks) {
                totalWeight = totalWeight.add(SacksHelper.getSackWeightOfStack(stack));
                // Make sure type exists and is in component
                Identifier typeId = SacksHelper.getSackTypeIdentifier(stack);
                if (typeId != null) {
                    isValid &= typeIds.contains(typeId);
                    typeIdsInSack.add(typeId);
                }
                else {
                    isValid = false;
                }
            }
            // Make sure the expected types and found types are equal
            isValid &= typeIds.equals(typeIdsInSack);
        }
    }
}
