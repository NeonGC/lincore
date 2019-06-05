package ru.lodes.lincore.api.events.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.lodes.lincore.api.event.Event;
import ru.lodes.lincore.api.event.HandlerList;
import ru.lodes.lincore.data.Server;

@RequiredArgsConstructor
public class PlayerLoginEvent extends Event {

    @Getter
    private final String name;
    @Getter
    private final Server proxy;
    @Getter
    private final String ip;
    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
