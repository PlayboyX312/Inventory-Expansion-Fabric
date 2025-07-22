package derekahedron.invexp.mixin;

import derekahedron.invexp.sack.SackContents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    /**
     * Map content packets are sent to players holding a map.
     * This changes it to also send content packets to players with a map in a sack.
     */
    @Inject(
            method = "sendMapPacket",
            at = @At("HEAD")
    )
    private void sendMapPacketToSackContents(ItemStack stack, @NotNull CallbackInfo ci) {
        SackContents contents = SackContents.of(stack);
        if (contents != null && !contents.isEmpty()) {
            ServerPlayerEntity self = (ServerPlayerEntity) (Object) this;
            for (ItemStack nestedStack : contents.getStacks()) {
                if (nestedStack.contains(DataComponentTypes.MAP_ID)) {
                    self.sendMapPacket(nestedStack);
                }
            }
        }
    }
}
