package derekahedron.invexp.sack;

import derekahedron.invexp.component.types.SackContentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import org.apache.commons.lang3.math.Fraction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ImmutableSackContents implements SackContentsReader {
    public final ItemStack sackStack;
    public SackContentsComponent component;

    private ImmutableSackContents(@NotNull ItemStack sackStack, @NotNull SackContentsComponent component) {
        this.sackStack = sackStack;
        this.component = component;
    }

    public static @Nullable ImmutableSackContents of(@Nullable ItemStack sackStack) {
        SackContentsComponent component = SackContentsComponent.getComponent(sackStack);
        if (component == null) {
            return null;
        }
        return new ImmutableSackContents(sackStack, component);
    }

    @Override
    public @NotNull ItemStack getSackStack() {
        return sackStack;
    }

    @Override
    public @NotNull List<RegistryEntry<SackType>> getSackTypes() {
        return component.sackTypes;
    }

    @Override
    public Fraction getTotalWeight() {
        return component.getTotalWeight();
    }

    @Override
    public @NotNull List<ItemStack> getStacks() {
        return component.getStacks();
    }

    @Override
    public int getSelectedIndex() {
        return component.getSelectedIndex();
    }
}
