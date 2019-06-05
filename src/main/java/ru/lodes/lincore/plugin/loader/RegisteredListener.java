package ru.lodes.lincore.plugin.loader;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.lodes.lincore.api.event.Cancellable;
import ru.lodes.lincore.api.event.Event;
import ru.lodes.lincore.api.event.EventPriority;
import ru.lodes.lincore.api.event.Listener;

@AllArgsConstructor
public class RegisteredListener {

    @Getter
    private final Listener listener;
    @Getter
    private final EventPriority priority;
    @Getter
    private final Plugin plugin;
    private final EventExecutor executor;
    @Getter
    private final boolean ignoringCancelled;

    public void callEvent(final Event event) throws EventException {
        if (event instanceof Cancellable) {
            if (((Cancellable) event).isCancelled() && isIgnoringCancelled()) {
                return;
            }
        }
        executor.execute(listener, event);
    }
}
