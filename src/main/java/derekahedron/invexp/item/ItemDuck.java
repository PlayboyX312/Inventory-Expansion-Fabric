package derekahedron.invexp.item;

import derekahedron.invexp.sack.SackDefaultManager;
import org.jetbrains.annotations.Nullable;

public interface ItemDuck {
    void invexp$setSackDefaults(@Nullable SackDefaultManager.SackDefaults sackDefaults);
    SackDefaultManager.SackDefaults invexp$getSackDefaults();
}
