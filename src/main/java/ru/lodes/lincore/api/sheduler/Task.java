package ru.lodes.lincore.api.sheduler;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.lodes.lincore.plugin.loader.Plugin;

@Slf4j
public class Task implements Runnable, ScheduledTask {

    @Getter
    private final Scheduler sched;
    @Getter
    private final int id;
    @Getter
    private final Plugin owner;
    @Getter
    private final Runnable task;
    @Getter
    private final long delay;
    @Getter
    private final long period;
    private final AtomicBoolean running = new AtomicBoolean(true);

    public Task(Scheduler sched, int id, Plugin owner, Runnable task, long delay, long period, TimeUnit unit) {
        this.sched = sched;
        this.id = id;
        this.owner = owner;
        this.task = task;
        this.delay = unit.toMillis(delay);
        this.period = unit.toMillis(period);
    }

    @Override
    public void cancel() {
        boolean wasRunning = running.getAndSet(false);
        if (wasRunning) {
            sched.cancel0(this);
        }
    }

    @Override
    public void run() {
        if (delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        while (running.get()) {
            try {
                task.run();
            } catch (Throwable t) {
                log.error("Task {} encountered an exception", this, t);
            }
            // If we have a period of 0 or less, only run once
            if (period <= 0) {
                break;
            }
            try {
                Thread.sleep(period);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        cancel();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Task)) {
            return false;
        }
        Task other = (Task) o;
        if (!other.canEqual(this)) {
            return false;
        }
        Object this$sched = getSched();
        Object other$sched = other.getSched();
        if (this$sched == null ? other$sched != null : !this$sched.equals(other$sched)) {
            return false;
        }
        if (getId() != other.getId()) {
            return false;
        }
        Object this$owner = getOwner();
        Object other$owner = other.getOwner();
        if (this$owner == null ? other$owner != null : !this$owner.equals(other$owner)) {
            return false;
        }
        Object this$task = getTask();
        Object other$task = other.getTask();
        if (this$task == null ? other$task != null : !this$task.equals(other$task)) {
            return false;
        }
        if (getDelay() != other.getDelay()) {
            return false;
        }
        if (getPeriod() != other.getPeriod()) {
            return false;
        }
        Object this$running = getRunning();
        Object other$running = other.getRunning();
        return this$running == null ? other$running == null : this$running.equals(other$running);
    }

    protected boolean canEqual(Object other) {
        return other instanceof Task;
    }

    @Override
    public int hashCode() {
        int result = 1;
        Object $sched = getSched();
        result = result * 59 + ($sched == null ? 43 : $sched.hashCode());
        result = result * 59 + getId();
        Object $owner = getOwner();
        result = result * 59 + ($owner == null ? 43 : $owner.hashCode());
        Object $task = getTask();
        result = result * 59 + ($task == null ? 43 : $task.hashCode());
        long $delay = getDelay();
        result = result * 59 + (int) ($delay >>> 32 ^ $delay);
        long $period = getPeriod();
        result = result * 59 + (int) ($period >>> 32 ^ $period);
        Object $running = getRunning();
        result = result * 59 + ($running == null ? 43 : $running.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "BungeeTask(sched=" + getSched() + ", id=" + getId() + ", owner=" + getOwner() + ", task=" + getTask() + ", delay=" + getDelay() + ", period=" + getPeriod() + ", running=" + getRunning() + ")";
    }

    public AtomicBoolean getRunning() {
        return this.running;
    }
}
