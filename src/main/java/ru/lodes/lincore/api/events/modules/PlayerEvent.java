package ru.lodes.lincore.api.events.modules;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.lodes.lincore.api.event.Event;
import ru.lodes.lincore.data.User;

@AllArgsConstructor
@Getter
public abstract class PlayerEvent extends Event {

    protected User player;

    protected PlayerEvent(final User who, boolean async) {
        super(async);
        player = who;

    }
}
