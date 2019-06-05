package ru.lodes.lincore.api.command;

public interface TabExecutor {

    Iterable<String> onTabComplete(CommandSender sender, String[] args);

}
