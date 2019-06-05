package ru.lodes.lincore.data;

import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CoreServer extends Server {

    private int online = 0;
    private Channel channel;

//    public void sendPacket(Packet<?> packet) {
//        ServerHandler.sendPacket(channel, packet);
//    }
//
//    public void sendAll(Packet<?> packet) {
//        ServerHandler.sendPacketAll(packet);
//    }
}
