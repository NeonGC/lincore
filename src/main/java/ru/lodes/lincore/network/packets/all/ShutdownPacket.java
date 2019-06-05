package ru.lodes.lincore.network.packets.all;

import io.netty.channel.Channel;
import ru.lodes.lincore.LinCore;
import ru.lodes.lincore.network.PacketBuffer;
import ru.lodes.lincore.network.packets.Packet;
import ru.lodes.lincore.network.packets.State;

public class ShutdownPacket extends Packet {

    private int action;
    private String srv;

    @Override
    public void readPacketData(PacketBuffer buf) {
        action = buf.readInt();
        if (action == 1) {
            srv = buf.readString();
        }
    }

    @Override
    public void writePacketData(PacketBuffer buf) {
        buf.writeVarInt(State.getPacketId(this));
    }

    @Override
    public void processPacket(Channel channel) {
        if (this.action == 1) {
            LinCore.getInstance().getDataHandler().getServer(srv).sendPacket(this);
        } else {
            LinCore.getInstance().getDataHandler().getServersList().forEach(act -> act.sendPacket(this));
        }
    }

}
