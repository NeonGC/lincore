package ru.lodes.lincore.commands;

import ru.lodes.lincore.LinCore;
import ru.lodes.lincore.api.command.CommandSender;
import ru.lodes.lincore.api.enums.CommandAccess;
import ru.lodes.lincore.api.modules.ACommand;

public class StopCommand extends ACommand {

    public StopCommand(LinCore core) {
        super(core.getPlugin(), CommandAccess.CORE, "stop", "Остановить прокси сервер");
    }

    @Override
    public void onCheckedCommand(CommandSender sender, String[] args) {
        getCore().stop();
    }
}
