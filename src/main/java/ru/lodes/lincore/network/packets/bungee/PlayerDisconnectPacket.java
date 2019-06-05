package ru.lodes.lincore.network.packets.bungee;

import io.netty.channel.Channel;
import ru.lodes.lincore.LinCore;
import ru.lodes.lincore.api.events.player.PlayerDisconnectEvent;
import ru.lodes.lincore.data.Player;
import ru.lodes.lincore.data.Server;
import ru.lodes.lincore.network.PacketBuffer;
import ru.lodes.lincore.network.packets.Packet;

public class PlayerDisconnectPacket extends Packet {

    public String name;

    @Override
    public void readPacketData(PacketBuffer buf) {
        this.name = buf.readString();
    }

    @Override
    public void writePacketData(PacketBuffer buf) {
    }

    @Override
    public void processPacket(Channel channel) {
        Server srv = LinCore.getInstance().getDataHandler().getProxy(channel);
        Player player = LinCore.getInstance().getDataHandler().getUser(this.name);
        LinCore.getInstance().getPluginManager().callEvent(new PlayerDisconnectEvent(player, srv));
    }
}
