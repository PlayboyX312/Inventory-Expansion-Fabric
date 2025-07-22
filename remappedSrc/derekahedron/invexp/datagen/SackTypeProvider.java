package derekahedron.invexp.datagen;

import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import derekahedron.invexp.sack.SackType;
import derekahedron.invexp.sack.SackTypes;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.registry.*;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class SackTypeProvider implements DataProvider {
    private final DataOutput output;

    public SackTypeProvider(DataOutput output) {
        this.output = output;
    }

    @Override
    public String getName() {
        return "TODO";
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        return CompletableFuture.allOf(
                writeObject(writer, SackTypes.WOOD, new SackType()),
                writeObject(writer, SackTypes.DOOR, new SackType()));
    }

    public CompletableFuture<?> writeObject(DataWriter cache, RegistryKey<SackType> key, SackType value) {
        return writeObject(output, cache, key, SackType.CODEC, value);
    }

    public static <E> CompletableFuture<?> writeObject(DataOutput output, DataWriter cache, RegistryKey<E> key, Encoder<E> encoder, E value) {
        Path path = output.getResolver(key.getRegistryRef()).resolveJson(key.getValue());
        return encoder.encodeStart(JsonOps.INSTANCE, value).mapOrElse(
                jsonElement -> DataProvider.writeToPath(cache, jsonElement, path),
                error -> CompletableFuture.failedFuture(
                        new IllegalStateException("Couldn't generate file '" + path + "': " + error.message())));
    }
}
