package derekahedron.invexp.item;

import derekahedron.invexp.sack.SackInsertableManager;
import org.jetbrains.annotations.Nullable;

public interface ItemDuck {
    void invexp$setDefaultSackInsertable(@Nullable SackInsertableManager.DefaultSackInsertable defaultSackInsertable);
    SackInsertableManager.DefaultSackInsertable invexp$getDefaultSackInsertable();
}
