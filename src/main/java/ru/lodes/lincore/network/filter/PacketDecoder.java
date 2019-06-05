package ru.lodes.lincore.network.filter;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import ru.lodes.lincore.network.PacketBuffer;
import ru.lodes.lincore.network.packets.Packet;
import ru.lodes.lincore.network.packets.State;
import static ru.lodes.lincore.utils.GlobalConstants.MARKER_NETWORK;

@Slf4j
public class PacketDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
        try {
            if (buf.readableBytes() != 0) {
//                int i = buf.readableBytes();
//                byte[] b = new byte[i];
//                buf.readBytes(b);
//                b = decode(b);
//                PacketBuffer pbuf = new PacketBuffer(Unpooled.wrappedBuffer(b));
                PacketBuffer pbuf = new PacketBuffer(buf);
                int id = pbuf.readVarInt();
                Packet<?> packet = State.getPacket(id);
                if (packet == null) {
                    log.info("Пакета с номером {} не существует!", id);
                    return;
                }
                packet.readPacketData(pbuf);
                out.add(packet);
            }
        } catch (Exception ex) {
            log.warn(MARKER_NETWORK, "-------------------------------");
            log.warn(MARKER_NETWORK, "Exception");
            log.warn(MARKER_NETWORK, "", ex);
            log.warn(MARKER_NETWORK, "-------------------------------");
        }
    }

    private byte[] decode(byte[] byt) {
        List<Byte> list = new ArrayList<>();
        int interval = 0;
        while (byt.length >= interval) {
            interval += byt[0];
        }
        interval -= byt[0];
        for (int i = byt.length;i > 1;i--) {
            if (interval != 0 && interval == i) {
                interval -= byt[0];
                continue;
            }
            list.add(byt[i - 1]);
        }
        Byte[] bytes = list.toArray(new Byte[0]);
        byte[] bytess = new byte[bytes.length];
        int i = 0;
        for (Byte b : bytes) {
            bytess[i++] = b;
        }
        return bytess;
    }
}
