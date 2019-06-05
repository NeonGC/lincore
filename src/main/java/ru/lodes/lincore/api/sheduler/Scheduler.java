package ru.lodes.lincore.api.sheduler;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.NonNull;
import ru.lodes.lincore.plugin.loader.Plugin;

public class Scheduler implements TaskScheduler {

    private final Object lock = new Object();
    private final AtomicInteger taskCounter = new AtomicInteger();
    private final IntObjectMap<Task> tasks = new IntObjectHashMap<>();
    private final Multimap<Plugin, Task> tasksByPlugin = Multimaps.synchronizedMultimap(HashMultimap.create());
    private final Unsafe unsafe = Plugin::getExecutorService;

    @Override
    public void cancel(int id) {
        Task task = tasks.get(id);
        Preconditions.checkArgument(task != null, "No task with id %s", id);

        task.cancel();
    }

    void cancel0(Task task) {
        synchronized (lock) {
            tasks.remove(task.getId());
            tasksByPlugin.values().remove(task);
        }
    }

    @Override
    public void cancel(ScheduledTask task) {
        task.cancel();
    }

    @Override
    public int cancel(Plugin plugin) {
        Set<ScheduledTask> toRemove = new HashSet<>(tasksByPlugin.get(plugin));
        toRemove.forEach(this::cancel);
        return toRemove.size();
    }

    @Override
    public ScheduledTask runAsync(Plugin owner, Runnable task) {
        return schedule(owner, task, 0, TimeUnit.MILLISECONDS);
    }

    @Override
    public ScheduledTask schedule(Plugin owner, Runnable task, long delay, TimeUnit unit) {
        return schedule(owner, task, delay, 0, unit);
    }

    @Override
    public ScheduledTask schedule(@NonNull Plugin owner, @NonNull Runnable task, long delay, long period, TimeUnit unit) {
        Task prepared = new Task(this, taskCounter.getAndIncrement(), owner, task, delay, period, unit);

        synchronized (lock) {
            tasks.put(prepared.getId(), prepared);
            tasksByPlugin.put(owner, prepared);
        }

        owner.getExecutorService().execute(prepared);
        return prepared;
    }

    @Override
    public Unsafe unsafe() {
        return unsafe;
    }
}
