package ru.lodes.lincore.threads;

import ru.lodes.lincore.LinCore;
import ru.lodes.lincore.api.ChatColor;
import ru.lodes.lincore.api.events.system.TPSUpdateEvent;
import ru.lodes.lincore.api.modules.TaskModule;

public class TPSTask extends TaskModule {

    private long mills;
    private static final double[] tpsArr = new double[10];
    private int index = 0;

    public TPSTask() {
        super(120L, 20);
        this.mills = 0L;
        for (int i = 0;i < 10;i++) {
            tpsArr[i] = 20.0D;
        }
    }

    public static double getTPS() {
        double tpsSum = 0.0D;
        for (double d : tpsArr) {
            tpsSum += d;
        }
        return Math.round(tpsSum / 10.0D * 100.0D) / 100.0D;
    }

    public static String getStringTPS() {
        return getColor() + String.valueOf(getTPS());
    }

    public static ChatColor getColor() {
        double tps = getTPS();
        if (tps > 17.0D) {
            return ChatColor.GREEN;
        }
        if (tps > 13.0D) {
            return ChatColor.GOLD;
        }
        return ChatColor.RED;
    }

    @Override
    public void run() {
        if (this.mills > 0L) {
            double diff = System.currentTimeMillis() - this.mills - 1000.0D;
            if (diff < 0.0D) {
                diff = Math.abs(diff);
            }
            double tps;
            if (diff == 0.0D) {
                tps = 20.0D;
            } else {
                tps = 20.0D - diff / 50.0D;
            }
            if (tps < 0.0D) {
                tps = 0.0D;
            }
            tpsArr[(this.index++)] = tps;
            if (this.index >= tpsArr.length) {
                this.index = 0;
                LinCore.getInstance().getPluginManager().callEvent(new TPSUpdateEvent(getTPS()));
            }
        }
        this.mills = System.currentTimeMillis();
    }
}
