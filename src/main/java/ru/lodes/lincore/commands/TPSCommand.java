package ru.lodes.lincore.commands;

import java.lang.management.ManagementFactory;
import lombok.extern.slf4j.Slf4j;
import ru.lodes.lincore.LinCore;
import ru.lodes.lincore.api.command.CommandSender;
import ru.lodes.lincore.api.enums.CommandAccess;
import ru.lodes.lincore.api.modules.ACommand;
import ru.lodes.lincore.threads.TPSTask;
import ru.lodes.lincore.utils.TimeUtil;

@Slf4j
public class TPSCommand extends ACommand {

    public TPSCommand(LinCore core) {
        super(core.getPlugin(),
                CommandAccess.CORE,
                "tps",
                new String[]{"gc", "mem", "memory"},
                "Получить информацию о памяти");
    }

    @Override
    public void onCheckedCommand(CommandSender sender, String[] args) {
        long tt = System.currentTimeMillis() / 1000L - ManagementFactory.getRuntimeMXBean().getStartTime() / 1000L;
        int months = (int) tt / 2592000;
        int days = (int) tt / 86400;
        int hours = (int) tt / 3600;
        int minutes = (int) (tt % 3600) / 60;
        int seconds = (int) tt % 60;
        String time = TimeUtil.plurals(months, "месяц ", "месяца ", "месяцев ")
                + TimeUtil.plurals(days, "день ", "дня ", "дней ")
                + TimeUtil.plurals(hours, "час ", "часа ", "часов ")
                + TimeUtil.plurals(minutes, "минута ", "минуты ", "минут ")
                + TimeUtil.plurals(seconds, "секунда ", "секунды ", "секунд ");

        log.info("uptime: {}", time);
        log.info("tps: {}", TPSTask.getStringTPS());
        log.info("gcmax: {}", Runtime.getRuntime().maxMemory() / 1024L / 1024L);
        log.info("gctotal: {}", Runtime.getRuntime().totalMemory() / 1024L / 1024L);
        log.info("gcfree: {}", Runtime.getRuntime().freeMemory() / 1024L / 1024L);
    }
}
