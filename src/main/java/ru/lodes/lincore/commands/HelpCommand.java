package ru.lodes.lincore.commands;

import lombok.extern.slf4j.Slf4j;
import ru.lodes.lincore.LinCore;
import ru.lodes.lincore.api.command.CommandSender;
import ru.lodes.lincore.api.enums.CommandAccess;
import ru.lodes.lincore.api.modules.ACommand;

@Slf4j
public class HelpCommand extends ACommand {

    public HelpCommand(LinCore core) {
        super(core.getPlugin(), CommandAccess.CORE, "help", "");
    }

    @Override
    public void onCheckedCommand(CommandSender sender, String[] args) {
        getCore().getPluginManager().getCommandsByPlugin().values().stream()
                .filter((entry) -> !entry.getName().equals("help"))
                .forEach((entry) -> log.info("{} - {}", entry.getName(), entry.getDescription()));
    }
}
