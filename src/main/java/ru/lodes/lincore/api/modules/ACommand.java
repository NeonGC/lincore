package ru.lodes.lincore.api.modules;

import ru.lodes.lincore.LinCore;
import ru.lodes.lincore.api.ChatColor;
import ru.lodes.lincore.api.command.Command;
import ru.lodes.lincore.api.command.CommandSender;
import ru.lodes.lincore.api.enums.CommandAccess;
import ru.lodes.lincore.data.User;
import ru.lodes.lincore.plugin.loader.Plugin;

public abstract class ACommand<Plug extends Plugin> extends Command {

    private final Plug core;

    private final String onAccess = ChatColor.RED + "Доступ к этой команде из консоли запрещён!";
    private final String onAccessConsole = ChatColor.RED + "Доступ к этой команде разрешён только из консоли!";
    private final String onAccessPerm = ChatColor.RED + "Недостаточно прав для выполнения данной команды!";

    public ACommand(Plug core, CommandAccess access, String name, String description) {
        super(name, description, access);
        this.core = core;
    }

    public ACommand(Plug core, CommandAccess access, String name, String permission, String description) {
        super(name, permission, description, access);
        this.core = core;
    }

    public ACommand(Plug core, CommandAccess access, String name, String[] aliases, String description) {
        super(name, aliases, description, access);
        this.core = core;
    }

    public ACommand(Plug core,
            CommandAccess access,
            String name,
            String permission,
            String[] aliases,
            String description) {
        super(name, permission, aliases, description, access);
        this.core = core;
    }

    public LinCore getCore() {
        return LinCore.getInstance();
    }

    public Plug getPlugin() {
        return this.core;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if ((!(sender instanceof User)) && (this.getAccess() == CommandAccess.PLAYER)) {
            return;
        } else if ((sender instanceof User) && (this.getAccess() == CommandAccess.CORE)) {
            return;
//        } else {
//            if ((getPermission() != null) && (!sender.hasPermission(getPermission()))) {
//                sender.sendMessage(onAccessPerm);
//                return;
//            }
        }
        onCheckedCommand(sender, args);
    }

    public abstract void onCheckedCommand(CommandSender paramCommandSender, String[] paramArrayOfString);

}
