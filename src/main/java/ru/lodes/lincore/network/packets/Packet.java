package ru.lodes.lincore.network.packets;

import io.netty.channel.Channel;
import ru.lodes.lincore.api.event.Event;
import ru.lodes.lincore.api.event.HandlerList;
import ru.lodes.lincore.network.PacketBuffer;

public abstract class Packet<T> extends Event {
    
    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public abstract void readPacketData(PacketBuffer buf) throws Exception;

    public abstract void writePacketData(PacketBuffer buf) throws Exception;

    public abstract void processPacket(Channel channel);
}
