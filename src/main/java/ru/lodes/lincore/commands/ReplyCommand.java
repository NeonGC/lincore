package ru.lodes.lincore.commands;

import java.util.Arrays;
import java.util.StringJoiner;
import ru.lodes.lincore.LinCore;
import ru.lodes.lincore.api.ChatColor;
import ru.lodes.lincore.api.command.CommandSender;
import ru.lodes.lincore.api.enums.CommandAccess;
import ru.lodes.lincore.api.modules.ACommand;
import ru.lodes.lincore.data.CoreUser;

public class ReplyCommand extends ACommand {

    private final String usage = ChatColor.RED + "/reply <сообщение>";
    private final String noMessage = ChatColor.RED + "Введите сообщение.";
    private final String noReply = ChatColor.RED + "Отвечать некому.";

    public ReplyCommand(LinCore core) {
        super(core.getPlugin(), CommandAccess.PLAYER, "reply", "r", "Ответ на последнее присланное сообщение");
    }

    @Override
    public void onCheckedCommand(CommandSender sender, String[] args) {
        if (((CoreUser) sender).getReply() != null) {
            sender.sendMessage(noReply);
            return;
        }
        if (args.length == 0) {
            sender.sendMessage(usage);
            return;
        } else if (args.length == 1) {
            sender.sendMessage(noMessage);
            return;
        }

        StringJoiner sj = new StringJoiner(" ");
        Arrays.stream(args).skip(1).forEach(sj::add);

//        new MessagePacket(sj.toString(), args[0], sender.getName()).processPacket(null);
    }
}
