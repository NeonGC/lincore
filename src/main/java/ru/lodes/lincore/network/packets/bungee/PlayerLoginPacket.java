package ru.lodes.lincore.network.packets.bungee;

import io.netty.channel.Channel;
import ru.lodes.lincore.LinCore;
import ru.lodes.lincore.api.events.player.PlayerLoginEvent;
import ru.lodes.lincore.data.Server;
import ru.lodes.lincore.network.PacketBuffer;
import ru.lodes.lincore.network.packets.Packet;

public class PlayerLoginPacket extends Packet {

    public String name;
    public String ip;

    @Override
    public void readPacketData(PacketBuffer buf) {
        this.name = buf.readString();
        this.ip = buf.readString();
    }

    @Override
    public void writePacketData(PacketBuffer buf) {
    }

    @Override
    public void processPacket(Channel channel) {
        Server srv = LinCore.getInstance().getDataHandler().getProxy(channel);
        LinCore.getInstance().getPluginManager().callEvent(new PlayerLoginEvent(this.name, srv, this.ip));
    }
}
