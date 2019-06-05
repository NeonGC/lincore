package ru.lodes.lincore.network.packets.all;

import io.netty.channel.Channel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.lodes.lincore.LinCore;
import ru.lodes.lincore.data.CoreUser;
import ru.lodes.lincore.network.PacketBuffer;
import ru.lodes.lincore.network.packets.Packet;
import ru.lodes.lincore.network.packets.State;

@Slf4j
@NoArgsConstructor
public class MessagePacket extends Packet {

    public Action action;
    public String message;
    public String receiver; //Получатель
    public String sender; //Отправитель

    public MessagePacket(String message, String receiver) {
        this(message, receiver, null);
        this.action = Action.DEV;
    }

    public MessagePacket(String message, String receiver, String sender) {
        this.action = Action.PRIVATE;
        this.message = message;
        this.receiver = receiver;
        this.sender = sender;
    }

    @Override
    public void readPacketData(PacketBuffer buf) {
        String act = buf.readString();
        this.action = Action.valueOf(act);
        if (this.action == Action.DEV) {
            this.message = buf.readString();
            this.receiver = buf.readString();
        } else {
            this.message = buf.readString();
            this.receiver = buf.readString();
            this.sender = buf.readString();
        }
    }

    @Override
    public void writePacketData(PacketBuffer buf) {
        buf.writeVarInt(State.getPacketId(this));
        buf.writeString(this.action.name());
        if (this.action == Action.DEV) {
            buf.writeString(this.message);
            buf.writeString(this.receiver);
        } else {
            buf.writeString(this.message);
            buf.writeString(this.receiver);
            buf.writeString(this.sender);
        }
    }

    @Override
    public void processPacket(Channel channel) {
        if (this.action == Action.PRIVATE) {
            CoreUser receiver = LinCore.getInstance().getDataHandler().getUser(this.receiver);
            CoreUser sender = LinCore.getInstance().getDataHandler().getUser(this.sender);
            if (sender == null) {
                return;
            }
            if (!sender.isMessageEnabled()) {
                sender.sendMessage("§cУ вас отключены сообщения");
                return;
            }
            if (receiver == null) {
                sender.sendMessage("§cИгрок не в сети");
                return;
            }
            if (!receiver.isMessageEnabled()) {
                sender.sendMessage("§cУ " + this.receiver + " отключены сообщения");
                return;
            }
            if (receiver.isIgnore(sender)) {
                sender.sendMessage("§cВы занесены в чёрный список у игрока " + this.receiver);
                return;
            }
            receiver.getServer().sendPacket(this);
            sender.sendMessage("§e[§fВы§e -> §f" + this.receiver + "§e] " + this.message);
        } else {
            CoreUser receiver = LinCore.getInstance().getDataHandler().getUser(this.receiver);
            if (receiver == null) {
                log.info("Игрок {} не в сети.", this.receiver);
                return;
            }
            receiver.getServer().sendPacket(this);
        }
    }

    public enum Action {
        PRIVATE, DEV
    }
}
