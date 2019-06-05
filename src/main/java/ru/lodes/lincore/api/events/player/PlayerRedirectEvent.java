package ru.lodes.lincore.api.events.player;

import lombok.Getter;
import lombok.Setter;
import ru.lodes.lincore.api.event.Cancellable;
import ru.lodes.lincore.api.event.HandlerList;
import ru.lodes.lincore.api.events.modules.PlayerEvent;
import ru.lodes.lincore.data.User;
import ru.lodes.lincore.data.Server;

public class PlayerRedirectEvent extends PlayerEvent implements Cancellable {

    @Getter
    private final Server server;
    @Getter
    @Setter
    private boolean cancelled = false;
    private static final HandlerList handlers = new HandlerList();

    public PlayerRedirectEvent(User player, Server server) {
        super(player);
        this.server = server;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
