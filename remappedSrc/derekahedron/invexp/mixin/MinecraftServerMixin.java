package derekahedron.invexp.mixin;

import com.mojang.datafixers.DataFixer;
import derekahedron.invexp.sack.SackInsertableManager;
import derekahedron.invexp.util.DataPackChangeDetector;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.util.ApiServices;
import net.minecraft.world.level.storage.LevelStorage;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.Proxy;
import java.util.function.Consumer;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    /**
     * After a server is made, create a new insertable manager from the registries and mark
     * DataPackChangeDetector as dirty.
     */
    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void initialPostDataPackLoad(
            Thread serverThread, LevelStorage.Session session, ResourcePackManager dataPackManager,
            SaveLoader saveLoader, Proxy proxy, DataFixer dataFixer, ApiServices apiServices,
            WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory, @NotNull CallbackInfo ci
    ) {
        MinecraftServer self = (MinecraftServer) (Object) this;
        SackInsertableManager.createNewInstance(self.getRegistryManager());
        DataPackChangeDetector.markDirty();
    }

    /**
     * After data packs are reloaded, mark the change detector as dirty.
     */
    @ModifyArg(
            method = "reloadResources",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/concurrent/CompletableFuture;thenAcceptAsync(Ljava/util/function/Consumer;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"
            )
    )
    private @NotNull Consumer<Object> detectDataPackReload(Consumer<Object> consumer) {
        return (var) -> {
            consumer.accept(var);
            SackInsertableManager.updateInstanceDefaultSackInsertables();
            DataPackChangeDetector.markDirty();
        };
    }
}
