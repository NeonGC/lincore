package ru.lodes.lincore.commands;

import lombok.extern.slf4j.Slf4j;
import ru.lodes.lincore.LinCore;
import ru.lodes.lincore.api.command.CommandSender;
import ru.lodes.lincore.api.enums.CommandAccess;
import ru.lodes.lincore.api.modules.ACommand;
import ru.lodes.lincore.data.Server;

@Slf4j
public class SrvStopCommand extends ACommand {

    public SrvStopCommand(LinCore core) {
        super(core.getPlugin(), CommandAccess.CORE, "srvstop", "Остановить сервер");
    }

    @Override
    public void onCheckedCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            log.info("Введите название сервера");
        } else {
            Server srv;
            if ((srv = getCore().getDataHandler().getServer(args[0])) != null) {
//                srv.sendPacket(new ShutdownPacket());
            } else if ((srv = getCore().getDataHandler().getProxy(args[0])) != null) {
//                srv.sendPacket(new ShutdownPacket());
            } else {
                String servers = "";
                servers = getCore().getDataHandler().getServersList().stream()
                        .map((server) -> ", " + server.getName())
                        .reduce(servers, String::concat);
                servers = getCore().getDataHandler().getProxysList().stream()
                        .map((server) -> ", " + server.getName())
                        .reduce(servers, String::concat);
                log.info("Сервера: {}", servers.replaceFirst(", ", ""));
            }
        }
    }
}
