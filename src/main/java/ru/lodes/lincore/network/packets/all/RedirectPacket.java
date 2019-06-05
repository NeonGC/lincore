package ru.lodes.lincore.network.packets.all;

import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.lodes.lincore.LinCore;
import ru.lodes.lincore.api.events.player.PlayerRedirectEvent;
import ru.lodes.lincore.data.Player;
import ru.lodes.lincore.data.Server;
import ru.lodes.lincore.network.PacketBuffer;
import ru.lodes.lincore.network.packets.Packet;
import ru.lodes.lincore.network.packets.State;

@AllArgsConstructor
@NoArgsConstructor
public class RedirectPacket extends Packet {

    public int server;
    public String player;

    @Override
    public void readPacketData(PacketBuffer buf) {
        this.server = buf.readInt();
        this.player = buf.readString();
    }

    @Override
    public void writePacketData(PacketBuffer buf) {
        buf.writeVarInt(State.getPacketId(this));
        buf.writeInt(server);
        buf.writeString(player);
    }

    @Override
    public void processPacket(Channel channel) {
        Player player = LinCore.getInstance().getDataHandler().getUser(this.player);
        Server server = LinCore.getInstance().getDataHandler().getServer(this.server);
        if (player == null) {
            return;
        }
        if (server == null) {
            return;
        }
        LinCore.getInstance().getPluginManager().callEvent(new PlayerRedirectEvent(player, server));
        player.getProxy().sendPacket(this);
    }
}
