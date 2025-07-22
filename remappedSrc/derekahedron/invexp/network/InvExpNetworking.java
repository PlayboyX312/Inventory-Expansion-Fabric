package derekahedron.invexp.network;

import derekahedron.invexp.network.packet.c2s.play.SetSelectedIndexC2SPacket;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

/**
 * Initializer for network packets
 */
public class InvExpNetworking {

    /**
     * Registers modded packets
     */
    public static void initialize() {
        PayloadTypeRegistry.playC2S().register(SetSelectedIndexC2SPacket.ID, SetSelectedIndexC2SPacket.CODEC);
    }
}
