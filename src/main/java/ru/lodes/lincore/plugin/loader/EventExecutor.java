package ru.lodes.lincore.plugin.loader;

import ru.lodes.lincore.api.event.Event;
import ru.lodes.lincore.api.event.Listener;

public interface EventExecutor {

    void execute(Listener listener, Event event) throws EventException;
}
