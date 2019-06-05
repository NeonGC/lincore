package ru.lodes.lincore.api.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.lodes.lincore.plugin.loader.EventException;
import ru.lodes.lincore.plugin.loader.EventExecutor;
import ru.lodes.lincore.plugin.loader.Plugin;
import ru.lodes.lincore.plugin.loader.RegisteredListener;

@Slf4j
@AllArgsConstructor
public class EventBus {
    
    @NonNull
    private final Listener listener;
    @NonNull
    private final Plugin plugin;
    private final Map<Class<? extends Event>, Set<RegisteredListener>> ret = new HashMap<>();

    public Map<Class<? extends Event>, Set<RegisteredListener>> createRegisteredListeners() {
        ret.clear();
        Set<Method> methods;
        try {
            Method[] publicMethods = listener.getClass().getMethods();
            Method[] privateMethods = listener.getClass().getDeclaredMethods();
            methods = new HashSet<>(publicMethods.length + privateMethods.length, 1.0f);
            methods.addAll(Arrays.asList(publicMethods));
            methods.addAll(Arrays.asList(privateMethods));
        } catch (NoClassDefFoundError e) {
            log.error("Plugin {} has failed to register events for {} because {} does not exist.",
                    plugin.getDescription().getName(),
                    listener.getClass(),
                    e.getMessage());
            return ret;
        }
        methods.stream()
                .filter((method) -> method.getParameterTypes().length == 1)
                .filter((method) -> method.getAnnotation(EventHandler.class) != null)
                // Do not register bridge or synthetic methods to avoid event duplication
                .filter((method) -> !method.isBridge())
                .filter((method) -> !method.isSynthetic())
                .forEachOrdered((method) -> {
                    method.setAccessible(true);
                    checkEventHandlers(method);
        });
        return ret;
    }
    
    private void checkEventHandlers(@NonNull Method method){
        final EventHandler eh = method.getAnnotation(EventHandler.class);
        final Class<?> checkClass;
        if (!Event.class.isAssignableFrom(checkClass = method.getParameterTypes()[0])) {
            log.error("{} attempted to register an invalid EventHandler method signature \"{}\" in {}",
                    plugin.getDescription().getName(),
                    method.toGenericString(),
                    listener.getClass());
            return;
        }
        final Class<? extends Event> eventClass = checkClass.asSubclass(Event.class);
        ret.computeIfAbsent(eventClass, k -> new HashSet<>()).add(new RegisteredListener(listener, eh.priority(), plugin, setExecutor(eventClass, method), eh.ignoreCancelled()));
    }
    
    private EventExecutor setExecutor(final Class eventClass, Method method) {
        return (Listener listener1, Event event) -> {
            try {
                if (!eventClass.isAssignableFrom(event.getClass())) {
                    return;
                }
                method.invoke(listener1, event);
            } catch (InvocationTargetException ex) {
                throw new EventException(ex.getCause());
            } catch (IllegalAccessException | IllegalArgumentException t) {
                throw new EventException(t);
            }
        };
    }
}
