package ru.lodes.lincore.network.packets.all;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import ru.lodes.lincore.LinCore;
import ru.lodes.lincore.data.Server;
import ru.lodes.lincore.network.PacketBuffer;
import ru.lodes.lincore.network.packets.Packet;
import ru.lodes.lincore.network.packets.bukkit.CommandListPacket;

import static ru.lodes.lincore.utils.GlobalConstants.MARKER_NETWORK;

@Slf4j
public class ServerConnectPacket extends Packet {

    public String server;

    @Override
    public void readPacketData(PacketBuffer buf) {
        this.server = buf.readString();
    }

    @Override
    public void writePacketData(PacketBuffer buf) {
    }

    @Override
    public void processPacket(Channel channel) {
        Server srv = new Server(this.server, channel);
        if (this.server.startsWith("bungee")) {
            LinCore.getInstance().getDataHandler().addProxy(srv);
        } else {
            LinCore.getInstance().getDataHandler().addServer(srv);
            LinCore.getInstance().getSrvManager().addServer(srv);
            channel.writeAndFlush(new CommandListPacket());
            log.info(server);

        }
        log.warn(MARKER_NETWORK, "-------------------------------");
        log.warn(MARKER_NETWORK, " Коннект с сервером {} был установлен!", srv.getName());
        log.warn(MARKER_NETWORK, " Id: {}", srv.getId());
        log.warn(MARKER_NETWORK, " Ip: {}", srv.getIp());
        log.warn(MARKER_NETWORK, " Port: {}", srv.getPort());
        log.warn(MARKER_NETWORK, "-------------------------------");
    }

}
