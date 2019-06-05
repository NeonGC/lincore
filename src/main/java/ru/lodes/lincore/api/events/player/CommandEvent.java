package ru.lodes.lincore.api.events.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.lodes.lincore.api.command.CommandSender;
import ru.lodes.lincore.api.event.Cancellable;
import ru.lodes.lincore.api.event.Event;
import ru.lodes.lincore.api.event.HandlerList;

@RequiredArgsConstructor
public class CommandEvent extends Event implements Cancellable {

    @Getter
    private final CommandSender sender;
    @Getter
    private final String command;
    @Getter
    private final String[] args;
    @Getter
    @Setter
    private boolean cancelled = false;
    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
