package ru.lodes.lincore.commands;

import ru.lodes.lincore.LinCore;
import ru.lodes.lincore.api.ChatColor;
import ru.lodes.lincore.api.command.CommandSender;
import ru.lodes.lincore.api.enums.CommandAccess;
import ru.lodes.lincore.api.modules.ACommand;
import ru.lodes.lincore.data.DataHandler;

public class OnlineCommand extends ACommand {

    private int online;

    public OnlineCommand(LinCore core) {
        super(core.getPlugin(), CommandAccess.ALL, "online", "Вывод онлайна персонала/серверов <staff/proxy/bukkit>");
    }

    @Override
    public void onCheckedCommand(CommandSender sender, String[] args) {
        DataHandler data = getCore().getDataHandler();
        online = 0;
        if (args.length < 1) {
            data.getProxysList().forEach((proxy) -> online += proxy.getOnline());
            sender.sendMessage(ChatColor.GREEN + "" + online);
            return;
        }
        if (args[0].equalsIgnoreCase("staff")) {
            sender.sendMessage(ChatColor.RED + "Данная функция ещё не поддерживается");
            return;
        }
        if (args[0].equalsIgnoreCase("proxy") || args[0].equalsIgnoreCase("bungee")) {
            sender.sendMessage(ChatColor.GREEN + "Сервер : онлайн");
            data.getProxysList().forEach((proxy) -> {
                sender.sendMessage(ChatColor.GREEN + proxy.getName() + " : " + proxy.getOnline());
            });
            return;
        }
        if (args[0].equalsIgnoreCase("bukkit") || (args[0].equalsIgnoreCase("spigot"))) {
            sender.sendMessage(ChatColor.GREEN + "Сервер : онлайн");
            data.getServersList().forEach((server) -> {
                sender.sendMessage(ChatColor.GREEN + server.getName() + " : " + server.getOnline());
            });
            return;
        }
        data.getProxysList().forEach((proxy) -> online += proxy.getOnline());
        sender.sendMessage(ChatColor.GREEN + "" + online);
    }
}
