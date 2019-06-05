package ru.lodes.lincore.network.packets.bungee;

import io.netty.channel.Channel;
import ru.lodes.lincore.LinCore;
import ru.lodes.lincore.api.events.player.ServerSelectEvent;
import ru.lodes.lincore.data.Player;
import ru.lodes.lincore.data.Server;
import ru.lodes.lincore.network.PacketBuffer;
import ru.lodes.lincore.network.handlers.ServerHandler;
import ru.lodes.lincore.network.packets.CallbackPacket;
import ru.lodes.lincore.network.packets.State;

public class ServerSelectCallbackPacket extends CallbackPacket {

    private String server;
    private String player;
    private String reason;
    private boolean cancelled;

    @Override
    public void readPacketData(PacketBuffer buf) {
        this.readUniqueId(buf);
        this.server = buf.readString();
        this.player = buf.readString();
    }

    @Override
    public void writePacketData(PacketBuffer buf) {
        buf.writeVarInt(State.getPacketId(this));
        this.writeUniqueId(buf);
        buf.writeBoolean(this.cancelled);
        if (this.cancelled) {
            buf.writeString(this.reason);
        }
    }

    @Override
    public void processPacket(Channel channel) {
        LinCore proxy = LinCore.getInstance();
        Player player = proxy.getDataHandler().getUser(this.player);
        Server server = proxy.getDataHandler().getServer(this.server);
        ServerSelectEvent event = proxy.getPluginManager().callEvent(new ServerSelectEvent(player, server));
        if (this.cancelled = event.isCancelled()) {
            this.reason = event.getReason();
            ServerHandler.sendPacket(channel, this);
        }
    }

}
