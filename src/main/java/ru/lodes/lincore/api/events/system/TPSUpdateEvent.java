package ru.lodes.lincore.api.events.system;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.lodes.lincore.api.event.Event;
import ru.lodes.lincore.api.event.HandlerList;

@RequiredArgsConstructor
public class TPSUpdateEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final double tps;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
