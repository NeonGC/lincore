//package ru.lodes.lincore.network.packets.bungee;
//
//import io.netty.channel.Channel;
//import ru.lodes.lincore.network.PacketBuffer;
//import ru.lodes.lincore.network.packets.Packet;
//import ru.lodes.lincore.network.packets.State;
//
//public class TestMSGPacket extends Packet {
//
//    @Override
//    public void readPacketData(PacketBuffer buf) throws Exception {
//    }
//
//    @Override
//    public void writePacketData(PacketBuffer buf) throws Exception {
//        buf.writeVarInt(State.getPacketId(this));
//        String str1 = "";
//        String str2 = ""; 
//        for(int i = 1; i <= 6000; i++) {
//            str1 += "a";
//            str2 += "a";
//            if (str1.getBytes().length == 120) {
//                buf.writeString(str1);
//            }
//            if (str2.getBytes().length == 12000) {
//                buf.writeString(str2);
//            }
//        }
//    }
//
//    @Override
//    public void processPacket(Channel channel) {
//    }
//    
//}
