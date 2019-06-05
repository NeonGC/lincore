package ru.lodes.lincore.api.events.player;

import lombok.Getter;
import lombok.Setter;
import ru.lodes.lincore.api.event.Cancellable;
import ru.lodes.lincore.api.event.HandlerList;
import ru.lodes.lincore.api.events.modules.PlayerEvent;
import ru.lodes.lincore.data.Server;
import ru.lodes.lincore.data.User;

public class ServerSelectEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final Server server;
    @Getter
    @Setter
    private boolean cancelled = false;
    @Getter
    private String reason = "";

    public ServerSelectEvent(User player, Server server) {
        super(player);
        this.server = server;
    }

    public void setCancelled(boolean cancelled, String reason) {
        this.cancelled = cancelled;
        this.reason = reason;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
