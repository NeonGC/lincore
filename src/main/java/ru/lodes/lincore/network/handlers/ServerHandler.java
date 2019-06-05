package ru.lodes.lincore.network.handlers;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import ru.lodes.lincore.LinCore;
import ru.lodes.lincore.data.DataHandler;
import ru.lodes.lincore.data.Server;
import ru.lodes.lincore.network.ThreadQuickExitException;
import ru.lodes.lincore.network.packets.CallbackPacket;
import ru.lodes.lincore.network.packets.Packet;
import static ru.lodes.lincore.utils.GlobalConstants.MARKER_NETWORK;

@Slf4j
public class ServerHandler extends SimpleChannelInboundHandler<Packet<?>> {

    private static final ChannelGroup channels = new DefaultChannelGroup("all-connected", GlobalEventExecutor.INSTANCE);
    private Channel channel;
    private static final HashMap<Integer, callback> callback = new HashMap<>();

    public void closeChannel() {
        if (this.channel.isOpen()) {
            this.channel.close().awaitUninterruptibly();
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext channel) throws Exception {
        super.channelActive(channel);
        this.channel = channel.channel();
        channels.add(channel.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext channel) throws Exception {
        log.warn(MARKER_NETWORK, "-------------------------------");
        log.warn(MARKER_NETWORK, " Коннект с сервером {} был разорван!", disconnect(channel));
        log.warn(MARKER_NETWORK, "-------------------------------");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channel, Throwable cause) throws Exception {
        log.warn(MARKER_NETWORK, "-------------------------------");
        log.warn(MARKER_NETWORK, " Коннект с сервером {} был разорван с ошибкой!", disconnect(channel));
        log.warn(MARKER_NETWORK, "", cause);
        log.warn(MARKER_NETWORK, "-------------------------------");
    }

    private String disconnect(ChannelHandlerContext channel) {
        DataHandler data = LinCore.getInstance().getDataHandler();
        Server srv;
        boolean proxy = false;
        if (data.getServer(channel.channel()) == null) {
            srv = data.getProxy(channel.channel());
            proxy = true;
        } else {
            srv = data.getServer(channel.channel());
        }
        channels.remove(channel.channel());
        closeChannel();
        if (proxy) {
            data.removeProxy(channel.channel());
        } else {
            data.removeServer(channel.channel());
        }
        LinCore.getInstance().getSrvManager().removeServer(srv);
        return srv == null ? "ERROR" : srv.getName();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channel, Packet<?> packet) throws IOException {
        if (this.channel.isOpen()) {
            try {
                LinCore.getInstance().getPluginManager().callEvent(packet);
                packet.processPacket(channel.channel());
                if (packet instanceof CallbackPacket) {
                    callback handle = callback.get(((CallbackPacket) packet).getUniqueId());
                    if (handle == null) {
                        return;
                    }
                    handle.returned(packet);
                    callback.remove(((CallbackPacket) packet).getUniqueId());
                }
            } catch (ThreadQuickExitException ex) {
                log.warn(MARKER_NETWORK, "-------------------------------");
                log.warn(MARKER_NETWORK, "ThreadQuickExitException");
                log.warn(MARKER_NETWORK, "", ex);
                log.warn(MARKER_NETWORK, "-------------------------------");
            }
        }
    }

    public static void sendPacketAll(Packet<?> packet) {
        channels.writeAndFlush(packet);
    }

    public static void sendPacket(Channel channel, Packet<?> packet) {
        channel.writeAndFlush(packet);
    }

    private static void sendCallbackPacket0(Channel channel, CallbackPacket packet, callback handler) {
        LinCore.getInstance().getScheduler().runAsync(LinCore.getInstance().getPlugin(), () -> {
            Random rand = new Random();
            int a = 0;
            while (callback.containsKey(a)) {
                a = rand.nextInt() * 12345678 / rand.nextInt(999999999) + rand.hashCode();
            }
            packet.setUniqueId(a);
            callback.put(a, handler);
            channel.writeAndFlush(packet);
        });
    }

    public static void sendCallbackPacket(Channel channel, CallbackPacket packet, callback handler) {
        sendCallbackPacket0(channel, packet, handler);
    }

    public static void sendWaitingCallbackPacket(Channel channel, CallbackPacket packet, callback handler) {
        sendCallbackPacket0(channel, packet, handler);
    }

    public interface callback {

        void returned(Packet<?> value);
    }
}
