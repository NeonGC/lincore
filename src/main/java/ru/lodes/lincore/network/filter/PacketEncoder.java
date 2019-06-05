package ru.lodes.lincore.network.filter;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import ru.lodes.lincore.network.PacketBuffer;
import ru.lodes.lincore.network.packets.Packet;
import static ru.lodes.lincore.utils.GlobalConstants.MARKER_NETWORK;

@Slf4j
public class PacketEncoder extends MessageToByteEncoder<Packet<?>> {

    private final static char[] chars = "йЙцЦувВаАasdfghj6пПрРоОлЛдДжqwertyuiopЖэЭяЯчЧсСмЮ!№%*()_+Ї=-#@$^&~ЁёQWіERTїYUIOP{}ASDFGHJKL:|ZXCШІщЩзЗхХъЪфФыЫVBNM<>?[]123457890МиИтТьЬбБюkl;\\УкКеЕнНгГш/\'.\",mnbvcxz"
            .toCharArray();

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet<?> packet, ByteBuf buf) throws Exception {
        try {
            ByteBuf bb = Unpooled.buffer();
            PacketBuffer pbuf = new PacketBuffer(bb);
            packet.writePacketData(pbuf); 
//            int s = bb.readableBytes();
//            byte[] b = new byte[s];
//            System.arraycopy(bb.array(), 0, b, 0, b.length);
//            b = encode(b);
//            buf.writeBytes(b);
        } catch (Exception ex) {
            log.warn(MARKER_NETWORK, "-------------------------------");
            log.warn(MARKER_NETWORK, "Exception");
            log.warn(MARKER_NETWORK, "", ex);
            log.warn(MARKER_NETWORK, "-------------------------------");
        }
    }

    private byte[] encode(byte[] byt) {
        List<Byte> list = new ArrayList<>();
        List<Integer> ints = new ArrayList<>();
        int random = 0;
        while (random == 0 || random == 1) {
            random = new Random().nextInt(byt.length);
        }
        list.add((byte) random);
        int interval = 1;
        for (int i = byt.length;i > 0;i--) {
            if (random == interval + 1) {
                int rand = 0;
                while (ints.contains(rand)) {
                    rand = new Random().nextInt(chars.length);
                }
                ints.add(rand);
                list.add((byte) chars[rand]);
                interval = 0;
            }
            interval++;
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
