package ru.lodes.lincore.api.event;

import java.util.*;
import ru.lodes.lincore.plugin.loader.Plugin;
import ru.lodes.lincore.plugin.loader.RegisteredListener;

public class HandlerList {

    private RegisteredListener[] handlers = null;

    private final EnumMap<EventPriority, ArrayList<RegisteredListener>> handlerslots;

    private static final ArrayList<HandlerList> allLists = new ArrayList<>();

    public static void bakeAll() {
        synchronized (allLists) {
            allLists.forEach(HandlerList::bake);
        }
    }

    public static void unregisterAll() {
        synchronized (allLists) {
            allLists.forEach((h) -> {
                synchronized (h) {
                    h.handlerslots.values().forEach(ArrayList::clear);
                    h.handlers = null;
                }
            });
        }
    }

    public static void unregisterAll(Plugin plugin) {
        synchronized (allLists) {
            allLists.forEach((h) -> h.unregister(plugin));
        }
    }

    public static void unregisterAll(Listener listener) {
        synchronized (allLists) {
            allLists.forEach((h) -> h.unregister(listener));
        }
    }

    public HandlerList() {
        handlerslots = new EnumMap<>(EventPriority.class);
        for (EventPriority o : EventPriority.values()) {
            handlerslots.put(o, new ArrayList<>());
        }
        synchronized (allLists) {
            allLists.add(this);
        }
    }

    public synchronized void register(RegisteredListener listener) {
        if (handlerslots.get(listener.getPriority()).contains(listener)) {
            throw new IllegalStateException(
                    "This listener is already registered to priority " + listener.getPriority().toString());
        }
        handlers = null;
        handlerslots.get(listener.getPriority()).add(listener);
    }

    public void registerAll(Collection<RegisteredListener> listeners) {
        listeners.forEach(this::register);
    }

    public synchronized void unregister(RegisteredListener listener) {
        if (handlerslots.get(listener.getPriority()).remove(listener)) {
            handlers = null;
        }
    }

    public synchronized void unregister(Plugin plugin) {
        boolean changed = false;
        for (List<RegisteredListener> list : handlerslots.values()) {
            for (ListIterator<RegisteredListener> i = list.listIterator();i.hasNext();) {
                if (i.next().getPlugin().equals(plugin)) {
                    i.remove();
                    changed = true;
                }
            }
        }
        if (changed) {
            handlers = null;
        }
    }

    public synchronized void unregister(Listener listener) {
        boolean changed = false;
        for (List<RegisteredListener> list : handlerslots.values()) {
            for (ListIterator<RegisteredListener> i = list.listIterator();i.hasNext();) {
                if (i.next().getListener().equals(listener)) {
                    i.remove();
                    changed = true;
                }
            }
        }
        if (changed) {
            handlers = null;
        }
    }

    public synchronized void bake() {
        if (handlers != null) {
            return; // don't re-bake when still valid
        }
        List<RegisteredListener> entries = new ArrayList<>();
        handlerslots.forEach((key, value) -> entries.addAll(value));
        handlers = entries.toArray(new RegisteredListener[0]);
    }

    public RegisteredListener[] getRegisteredListeners() {
        RegisteredListener[] handlers;
        while ((handlers = this.handlers) == null) {
            bake(); // This prevents fringe cases of returning null
        }
        return handlers;
    }

    public static ArrayList<RegisteredListener> getRegisteredListeners(Plugin plugin) {
        ArrayList<RegisteredListener> listeners = new ArrayList<>();
        synchronized (allLists) {
            allLists.stream()
                    .flatMap(handlerList -> handlerList.handlerslots.values().stream())
                    .flatMap(Collection::stream)
                    .filter(registeredListener -> registeredListener.getPlugin().equals(plugin))
                    .forEachOrdered(listeners::add);
        }
        return listeners;
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<HandlerList> getHandlerLists() {
        synchronized (allLists) {
            return (ArrayList<HandlerList>) allLists.clone();
        }
    }
}
