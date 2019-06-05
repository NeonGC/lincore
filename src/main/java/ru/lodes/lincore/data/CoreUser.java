package ru.lodes.lincore.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.lodes.lincore.LinCore;
import ru.lodes.lincore.api.command.CommandSender;
import ru.lodes.lincore.utils.ChatUtil;

@Data
@AllArgsConstructor
public class CoreUser extends User implements CommandSender {

    private CoreUser reply;
    private Server proxy;

    public void updateAlive() {
        lastAlive = System.currentTimeMillis();
    }

//    public Player(String nickname, Server proxy, String ip) {
//        this.name = nickname;
//        this.proxy = proxy;
//        this.ignore = new ArrayList();
//        this.reply = null;
//        this.ip = ip;
//        //loadInfo();
//    }
    @Override
    public void sendMessage(String message) {
//        this.proxy.sendPacket(new MessagePacket(message, this.name));
    }

    public void sendMessage(ChatUtil util) {
//        this.proxy.sendPacket(new MessagePacket(util.getTextComponent(), this.name));
    }

    @Override
    public void sendMessages(String... messages) {
        for (String msg : messages) {
            sendMessage(msg);
        }
    }

    public void sendMessage(ChatUtil... message) {
        StringBuilder msg = new StringBuilder();
        msg.append("{\"extra\":[");
        int utils = 0;
        for (ChatUtil util : message) {
            utils += 1;
            msg.append(utils == 1 ? util.getTextComponent() : "," + util.getTextComponent());
        }
        msg.append("]}");
        sendMessage(msg.toString());
    }

    public void redirect(String server) {
        Server srv = LinCore.getInstance().getDataHandler().getServer(server);
        if (srv == null) {
//            return;
        }
//        this.proxy.sendPacket(new RedirectPacket(srv.getId(), this.name));
    }

    public void redirect(int server) {
        Server srv = LinCore.getInstance().getDataHandler().getServer(server);
        if (srv == null) {
//            return;
        }
//        this.proxy.sendPacket(new RedirectPacket(server, this.name));
    }

    public boolean isIgnore(String name) {
        return this.ignored.contains(name.toLowerCase());
    }

    public boolean isIgnore(CoreUser player) {
        return this.isIgnore(player.getName());
    }

    public void addToIgnore(CoreUser player) {
        addToIgnore(player.getName());
    }

    public void addToIgnore(String name) {
        ignored.add(name);
    }

    public boolean isOnline() {
        return LinCore.getInstance()
                .getDataHandler()
                .getUser(name) != null && System.currentTimeMillis() - lastAlive < 30000;
    }
}
