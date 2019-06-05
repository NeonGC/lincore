package ru.lodes.lincore.network.packets;

import lombok.Getter;
import lombok.Setter;
import ru.lodes.lincore.network.PacketBuffer;

@Getter
@Setter
public abstract class CallbackPacket extends Packet {

    private int uniqueId;

    public int readUniqueId(PacketBuffer buf) {
        return uniqueId = buf.readVarInt();
    }

    public void writeUniqueId(PacketBuffer buf) {
        buf.writeVarInt(uniqueId);
    }
}
