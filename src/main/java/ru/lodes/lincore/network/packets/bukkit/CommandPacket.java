package ru.lodes.lincore.network.packets.bukkit;

import io.netty.channel.Channel;
import ru.lodes.lincore.LinCore;
import ru.lodes.lincore.api.events.player.CommandEvent;
import ru.lodes.lincore.data.Player;
import ru.lodes.lincore.network.PacketBuffer;
import ru.lodes.lincore.network.handlers.ServerHandler;
import ru.lodes.lincore.network.packets.Packet;
import ru.lodes.lincore.network.packets.State;

public class CommandPacket extends Packet {

    private String sender;
    private String command;
    private String line;
    private boolean cancelled;

    @Override
    public void readPacketData(PacketBuffer buf) {
        this.sender = buf.readString();
        this.command = buf.readString();
        this.line = buf.readString();
    }

    @Override
    public void writePacketData(PacketBuffer buf) {
        buf.writeVarInt(State.getPacketId(this));
        buf.writeBoolean(this.cancelled);
    }

    @Override
    public void processPacket(Channel channel) {
        LinCore proxy = LinCore.getInstance();
        Player sender = LinCore.getInstance().getDataHandler().getUser(this.sender);
        CommandEvent event = proxy.getPluginManager().callEvent(new CommandEvent(sender, this.command, this.line.split(" ")));
        if (cancelled = event.isCancelled()) {
            ServerHandler.sendPacket(channel, this);
            return;
        }
        if (proxy.getPluginManager().dispatchCommand(sender, this.line)) {
            cancelled = true;
            ServerHandler.sendPacket(channel, this);
        }
    }

}
