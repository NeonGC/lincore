package ru.lodes.lincore.commands;

import ru.lodes.lincore.LinCore;
import ru.lodes.lincore.api.ChatColor;
import ru.lodes.lincore.api.command.CommandSender;
import ru.lodes.lincore.api.enums.CommandAccess;
import ru.lodes.lincore.api.modules.ACommand;
import ru.lodes.lincore.data.CoreServer;
import ru.lodes.lincore.data.DataHandler;

public class TechCommand extends ACommand {

    private final DataHandler data;

    public TechCommand(LinCore core) {
        super(core.getPlugin(), CommandAccess.CORE, "tech", "Техническая информация о серверах");
        this.data = core.getDataHandler();
    }

    @Override
    public void onCheckedCommand(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("args: reconnect/info/servers/null");
            return;
        }
        switch (args[0]) {
            case "reconnect": {
                if (args.length < 2) {
                    sendServers(sender);
                    return;
                }
                if (data.getServersMap().containsKey(args[1])) {
                    CoreServer srv = data.getServer(args[1]);
                    if (srv.getChannel().isOpen()) {
                        srv.getChannel().close().awaitUninterruptibly();
                        sender.sendMessage(ChatColor.GREEN + "Server " + args[1] + " reconnected");
                    } else {
                        sender.sendMessage(ChatColor.RED + "Channel of the server " + args[1] + " closed!");
                    }
                } else if (data.getProxysMap().containsKey(args[1])) {
                    CoreServer srv = data.getProxy(args[1]);
                    if (srv.getChannel().isOpen()) {
                        srv.getChannel().close().awaitUninterruptibly();
                        sender.sendMessage(ChatColor.GREEN + "Server " + args[1] + " reconnected");
                    } else {
                        sender.sendMessage(ChatColor.RED + "Channel of the server " + args[1] + " closed!");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Такого сервера нет в списке!");
                    sendServers(sender);
                }
                break;
            }
            case "info": {
                if (args.length < 2) {
                    sendServers(sender);
                    return;
                }
                if (data.getServersMap().containsKey(args[1])) {
                    CoreServer srv = data.getServer(args[1]);
                    sender.sendMessages(
                            ChatColor.GREEN + "Type: " + srv.getType().name(),
                            ChatColor.GREEN + "Id: " + srv.getId(),
                            ChatColor.GREEN + "Name: " + srv.getName(),
                            ChatColor.GREEN + "Ip: " + srv.getIp(),
                            ChatColor.GREEN + "Port: " + srv.getPort(),
                            ChatColor.GREEN + "Online: " + srv.getOnline());
                } else if (data.getProxysMap().containsKey(args[1])) {
                    CoreServer srv = data.getProxy(args[1]);
                    sender.sendMessages(
                            ChatColor.GREEN + "Type: " + srv.getType().name(),
                            ChatColor.GREEN + "Id: " + srv.getId(),
                            ChatColor.GREEN + "Name: " + srv.getName(),
                            ChatColor.GREEN + "Ip: " + srv.getIp(),
                            ChatColor.GREEN + "Port: " + srv.getPort(),
                            ChatColor.GREEN + "Online: " + srv.getOnline());
                } else {
                    sender.sendMessage(ChatColor.RED + "Такого сервера нет в списке!");
                    sendServers(sender);
                }
                break;
            }
            case "servers": {
                sendServers(sender);
                break;
            }
            case "null": {
                sender.sendMessage(ChatColor.RED + "Servers with null id: " + data.getServersList().stream()
                        .filter(srv -> srv.getId() == 0)
                        .map((srv) -> ", " + srv.getName())
                        .reduce(String::concat)
                        .orElse("mn")
                        .substring(2) + data.getProxysList()
                        .stream()
                        .filter(srv -> srv.getId() == 0)
                        .map((srv) -> ", " + srv.getName())
                        .reduce(String::concat)
                        .orElse(""));
                break;
            }
            default: {
                sender.sendMessage("args: reconnect/info/servers/null");
            }
        }
    }

    private void sendServers(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "servers : " + data.getServersList().stream()
                .map((srv) -> ", " + srv.getName())
                .reduce(String::concat)
                .orElse("mn")
                .substring(2));
        sender.sendMessage(ChatColor.GREEN + "proxys : " + data.getProxysList().stream()
                .map((srv) -> ", " + srv.getName())
                .reduce(String::concat)
                .orElse("mn")
                .substring(2));
    }
}
