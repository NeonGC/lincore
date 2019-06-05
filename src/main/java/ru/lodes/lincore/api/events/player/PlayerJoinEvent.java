package ru.lodes.lincore.api.events.player;

import lombok.Getter;
import ru.lodes.lincore.api.event.HandlerList;
import ru.lodes.lincore.api.events.modules.PlayerEvent;
import ru.lodes.lincore.data.User;
import ru.lodes.lincore.data.Server;

public class PlayerJoinEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final Server server;

    public PlayerJoinEvent(User player, boolean async, Server server) {
        super(player, async);
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
