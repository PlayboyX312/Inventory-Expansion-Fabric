package derekahedron.invexp.mixin;

import derekahedron.invexp.item.ItemDuck;
import derekahedron.invexp.sack.SackDefaultManager;
import net.minecraft.item.Item;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Item.class)
public class ItemMixin implements ItemDuck {
    @Unique
    private SackDefaultManager.SackDefaults sackDefaults;

    @Override
    public void invexp$setSackDefaults(@Nullable SackDefaultManager.SackDefaults sackDefaults) {
        this.sackDefaults = sackDefaults;
    }

    @Override
    public SackDefaultManager.SackDefaults invexp$getSackDefaults() {
        return sackDefaults;
    }
}
