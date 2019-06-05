package ru.lodes.lincore.network.packets.all;

import io.netty.channel.Channel;
import lombok.NoArgsConstructor;
import ru.lodes.lincore.LinCore;
import ru.lodes.lincore.data.DataHandler;
import ru.lodes.lincore.data.Server;
import ru.lodes.lincore.network.PacketBuffer;
import ru.lodes.lincore.network.packets.Packet;
import ru.lodes.lincore.network.packets.State;

@NoArgsConstructor
public class OnlinePacket extends Packet {

    public int action;//1-ALL(c), 2-CALLBACK(s), 3-BUNGEE(c), 4-BUKKIT(c), 5-DEV(c), 6-RETURN_ALL(s)
    public String player;
    public String server;
    public int online;

    public OnlinePacket(int action) {
        this.action = action;
    }

    @Override
    public void readPacketData(PacketBuffer buf) {
        this.action = buf.readInt();
        if (this.action != 5) {
            this.player = buf.readString();
        }
        if (this.action == 5) {
            this.online = buf.readInt();
        }
    }

    @Override
    public void writePacketData(PacketBuffer buf) {
        buf.writeVarInt(State.getPacketId(this));
        buf.writeInt(this.action);
        if (this.action != 5) {
            buf.writeString(this.player);
        }
        if (this.action == 6) {
            buf.writeInt(this.online);
        }
        if (this.action == 2) {
            buf.writeString(this.server);
            buf.writeInt(this.online);
        }
    }

    @Override
    public void processPacket(Channel channel) {
        DataHandler data = LinCore.getInstance().getDataHandler();
        Server s = data.getServer(channel) == null ? data.getProxy(channel) : data.getServer(channel);
        switch (this.action) {
            case 1:
                this.action = 6;
                data.getProxysList().forEach((proxy) -> this.online += proxy.getOnline());
                s.sendPacket(this);
                break;
            case 3:
                this.action = 2;
                data.getProxysList().forEach((proxy) -> {
                    this.server = proxy.getName();
                    this.online = proxy.getOnline();
                    s.sendPacket(this);
                });
                break;
            case 4:
                this.action = 2;
                data.getServersList().forEach((srv) -> {
                    this.server = srv.getName();
                    this.online = srv.getOnline();
                    s.sendPacket(this);
                });
                break;
            case 5:
            default:
                s.setOnline(this.online);
                break;
        }
    }

}
