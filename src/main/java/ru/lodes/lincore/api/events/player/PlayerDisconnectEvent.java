package ru.lodes.lincore.api.events.player;

import lombok.Getter;
import ru.lodes.lincore.api.event.HandlerList;
import ru.lodes.lincore.api.events.modules.PlayerEvent;
import ru.lodes.lincore.data.User;
import ru.lodes.lincore.data.Server;

public class PlayerDisconnectEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final Server proxy;

    public PlayerDisconnectEvent(User player, Server proxy) {
        super(player);
        this.proxy = proxy;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
