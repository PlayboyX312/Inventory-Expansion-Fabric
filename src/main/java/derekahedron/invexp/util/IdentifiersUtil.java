package derekahedron.invexp.util;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class IdentifiersUtil {

    public static @NotNull Identifier biomesOPlenty(@NotNull String id) {
        return Identifier.of("biomesoplenty", id);
    }

    public static @NotNull Identifier storageDrawers(@NotNull String id) {
        return Identifier.of("storagedrawers", id);
    }

    public static @NotNull Identifier trailierTales(@NotNull String id) {
        return Identifier.of("trailiertales", id);
    }

    public static @NotNull Identifier waystones(@NotNull String id) {
        return Identifier.of("waystones", id);
    }

    public static @NotNull Identifier wilderWild(@NotNull String id) {
        return Identifier.of("wilderwild", id);
    }
}
