package ru.lodes.lincore.commands;

import lombok.extern.slf4j.Slf4j;
import ru.lodes.lincore.LinCore;
import ru.lodes.lincore.api.command.CommandSender;
import ru.lodes.lincore.api.enums.CommandAccess;
import ru.lodes.lincore.api.modules.ACommand;

@Slf4j
public class ReloadCommand extends ACommand {

    public ReloadCommand(LinCore core) {
        super(core.getPlugin(), CommandAccess.CORE, "reload", "Перезагрузка всех плагинов");
    }

    @Override
    public void onCheckedCommand(CommandSender sender, String[] args) {
        getCore().getPluginManager().disablePlugins();
        getCore().getPluginManager().detectPlugins();
        getCore().getPluginManager().loadPlugins();
        getCore().getPluginManager().enablePlugins();
        log.info("Перезагрузка всех плагинов завершена");
    }
}
