package ru.lodes.lincore.api.modules;

import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.lodes.lincore.LinCore;

import java.util.concurrent.TimeUnit;

/**
 * Абстрактный класс для разных потоков
 *
 * @author NeonGC
 */
@NoArgsConstructor
public abstract class TaskModule extends Thread implements Module {

    @Getter
    private boolean enabled = false;
    protected int taskid = -2;
    protected final Thread th = this;
    protected long delay = 20L, period = 20L;

    public TaskModule(long delay, long period) {
        this.delay = delay;
        this.period = period;
    }

    protected int startTask() {
        return LinCore.getInstance().getScheduler()
                .schedule(LinCore.getInstance().getPlugin(), this, delay, period, TimeUnit.SECONDS).getId();
    }

    @Override
    public void setEnabled() {
        this.enabled = true;
        this.taskid = startTask();
    }

    @Override
    public void setDisabled() {
        this.enabled = false;
        if (taskid != 0) {
            LinCore.getInstance().getScheduler().cancel(this.taskid);
        } else {
            th.interrupt();
        }
    }

    @Override
    public abstract void run();
}
