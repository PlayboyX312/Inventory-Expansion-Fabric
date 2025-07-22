package derekahedron.invexp.network.packet.c2s.play;

import derekahedron.invexp.util.InvExpUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

/**
 * Payload for client updating selected index of sack or quiver
 *
 * @param slotId            slot id of stack
 * @param selectedIndex     new selected index
 */
public record SetSelectedIndexC2SPacket(int slotId, int selectedIndex) implements CustomPayload {
    public static final CustomPayload.Id<SetSelectedIndexC2SPacket> ID;
    public static final PacketCodec<PacketByteBuf, SetSelectedIndexC2SPacket> CODEC;

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    static {
        ID = new CustomPayload.Id<>(InvExpUtil.identifier("set_selected_index"));
        CODEC = PacketCodec.tuple(
                PacketCodecs.INTEGER, SetSelectedIndexC2SPacket::slotId,
                PacketCodecs.INTEGER, SetSelectedIndexC2SPacket::selectedIndex,
                SetSelectedIndexC2SPacket::new
        );
    }
}
