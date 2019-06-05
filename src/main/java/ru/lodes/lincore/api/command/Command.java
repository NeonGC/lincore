package ru.lodes.lincore.api.command;

import lombok.Getter;
import ru.lodes.lincore.api.enums.CommandAccess;

@Getter
public abstract class Command {

    private final String name;
    private final String permission;
    private final String[] aliases;
    private final String description;
    private final CommandAccess access;

    public Command(String name, String description, CommandAccess access) {
        this(name, "", description, access);
    }

    public Command(String name, String permission, String description, CommandAccess access) {
        this(name, permission, new String[0], description, access);
    }

    public Command(String name, String[] aliases, String description, CommandAccess access) {
        this(name, "", aliases, description, access);
    }

    public Command(String name, String permission, String[] aliases, String description, CommandAccess access) {
        if (name == null) {
            throw new IllegalArgumentException("Command name == null");
        }
        this.name = name;
        this.permission = permission;
        this.aliases = aliases;
        this.description = description;
        this.access = access;
    }

    public abstract void execute(CommandSender sender, String[] args);
}
