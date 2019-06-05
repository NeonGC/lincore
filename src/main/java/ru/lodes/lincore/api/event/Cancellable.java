package ru.lodes.lincore.api.event;

public interface Cancellable {

    boolean isCancelled();

    void setCancelled(boolean var1);
}
