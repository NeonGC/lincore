package ru.lodes.lincore.api.events.player;

import lombok.Getter;
import ru.lodes.lincore.api.event.HandlerList;
import ru.lodes.lincore.api.events.modules.PlayerEvent;
import ru.lodes.lincore.data.User;
import ru.lodes.lincore.data.Server;

public class PlayerQuitEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final Server server;

    public PlayerQuitEvent(User player, Server server) {
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
