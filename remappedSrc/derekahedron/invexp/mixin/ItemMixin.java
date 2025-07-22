package derekahedron.invexp.mixin;

import derekahedron.invexp.item.ItemDuck;
import derekahedron.invexp.sack.SackInsertableManager;
import net.minecraft.item.Item;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Item.class)
public class ItemMixin implements ItemDuck {
    @Unique
    private SackInsertableManager.DefaultSackInsertable defaultSackInsertable;

    @Override
    public void invexp$setDefaultSackInsertable(@Nullable SackInsertableManager.DefaultSackInsertable defaultSackInsertable) {
        this.defaultSackInsertable = defaultSackInsertable;
    }

    @Override
    public SackInsertableManager.DefaultSackInsertable invexp$getDefaultSackInsertable() {
        return defaultSackInsertable;
    }
}
