package ru.lodes.lincore.network.packets.bukkit;

import io.netty.channel.Channel;
import ru.lodes.lincore.LinCore;
import ru.lodes.lincore.api.enums.CommandAccess;
import ru.lodes.lincore.network.PacketBuffer;
import ru.lodes.lincore.network.packets.Packet;
import ru.lodes.lincore.network.packets.State;

import java.util.ArrayList;
import java.util.List;

public class CommandListPacket extends Packet {

    private final String[] commands;

    public CommandListPacket() {
        List<String> cmds = new ArrayList<>();
        LinCore.getInstance().getPluginManager().getCommandMap().entrySet()
                .stream()
                .filter((entry) -> entry.getValue().getAccess() != CommandAccess.CORE)
                .forEach((entry) -> cmds.add(entry.getKey()));
        this.commands = cmds.toArray(new String[0]);
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws Exception {
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws Exception {
        buf.writeVarInt(State.getPacketId(this));
        buf.writeInt(commands.length);
        for (String command : commands) {
            buf.writeString(command);
        }
    }

    @Override
    public void processPacket(Channel channel) {
    }

}
