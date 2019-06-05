package ru.lodes.lincore.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.lodes.lincore.LinCore;
import ru.lodes.lincore.network.filter.ServerPipeline;

import static ru.lodes.lincore.utils.GlobalConstants.MARKER_NETWORK;

@Slf4j
public class ContainerServer {

    @Getter
    private Channel channel;
    private EventLoopGroup producer;
    private EventLoopGroup consumer;
    private Class<? extends ServerSocketChannel> serverSocketChannel;

    public void init(int port) {
        boolean hasEpoll = Epoll.isAvailable();
        if (hasEpoll) {
            producer = new EpollEventLoopGroup();
            consumer = new EpollEventLoopGroup();
            serverSocketChannel = EpollServerSocketChannel.class;
        } else {
            producer = new NioEventLoopGroup();
            consumer = new NioEventLoopGroup();
            serverSocketChannel = NioServerSocketChannel.class;
        }
        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(this.producer, this.consumer)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .channel(this.serverSocketChannel)
                    .childHandler(new ServerPipeline());
            log.info(MARKER_NETWORK, "Сервер запущен");
            channel = bootstrap.bind(port).channel();
        } catch (Exception ex) {
            log.warn(MARKER_NETWORK, "", ex);
            this.stop();
        }
    }

    public void stop() {
        if (isRunning()) {
            LinCore.getInstance().getDataHandler().getServersList()
                    .forEach((server) -> server.getChannel().disconnect());
            log.warn(MARKER_NETWORK, "Остановка сервера");
            producer.shutdownGracefully();
            consumer.shutdownGracefully();
        }
    }

    public boolean isRunning() {
        if (producer != null && consumer != null) {
            return !producer.isShutdown();
        } else {
            return false;
        }
    }
}
