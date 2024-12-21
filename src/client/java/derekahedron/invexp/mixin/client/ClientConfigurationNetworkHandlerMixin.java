package derekahedron.invexp.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import derekahedron.invexp.sack.SackInsertableManager;
import derekahedron.invexp.util.DataPackChangeDetector;
import net.minecraft.client.network.ClientConfigurationNetworkHandler;
import net.minecraft.network.packet.s2c.config.ReadyS2CPacket;
import net.minecraft.registry.DynamicRegistryManager;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConfigurationNetworkHandler.class)
public class ClientConfigurationNetworkHandlerMixin {

    /**
     * After receiving DataPack content from the server, if the connection is not local,
     * create a new manager and mark data pack change.
     */
    @Inject(
            method = "onReady",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/ClientConnection;transitionInbound(Lnet/minecraft/network/NetworkState;Lnet/minecraft/network/listener/PacketListener;)V"
            )
    )
    private void afterReady(
            ReadyS2CPacket packet, @NotNull CallbackInfo ci, @Local DynamicRegistryManager.Immutable immutable
    ) {
        ClientConfigurationNetworkHandler self = (ClientConfigurationNetworkHandler) (Object) this;
        if (!self.connection.isLocal()) {
            SackInsertableManager.createNewInstance(immutable);
            DataPackChangeDetector.markDirty();
        }
    }
}
