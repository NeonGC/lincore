package ru.lodes.lincore;

import java.io.IOException;
import jline.console.ConsoleReader;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.lodes.lincore.api.ChatColor;
import ru.lodes.lincore.api.command.ConsoleCommandSender;
import ru.lodes.lincore.api.modules.Module;
import ru.lodes.lincore.api.sheduler.Scheduler;
import ru.lodes.lincore.commands.*;
import ru.lodes.lincore.data.DataHandler;
import ru.lodes.lincore.plugin.loader.Plugin;
import ru.lodes.lincore.plugin.loader.PluginManager;
import ru.lodes.lincore.plugin.loader.loader.PluginDescription;
import ru.lodes.lincore.threads.TPSTask;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
public class LinCore {

    @Getter
    private static LinCore instance;
    @Getter
    private Scheduler scheduler;
    @Getter
    private static ConsoleReader consoleReader;
    @Getter
    private final ConsoleCommandSender consoleSender = ConsoleCommandSender.getInstance();
    @Getter
    private PluginManager pluginManager;
    @Getter
    private Plugin plugin;
    @Getter
    private DataHandler dataHandler;
    private boolean consoleReaderEnabled;
    private Thread primaryThread;
    private Module[] modules;

    void start() throws IOException {
        long start = System.currentTimeMillis();
        log.info("Запускаю ЛинКор");
        instance = this;
        consoleReader = new ConsoleReader();
        consoleReader.setExpandEvents(false);
        dataHandler = new DataHandler();
        setCorePlugin();
        setConfig();
        scheduler = new Scheduler();
        pluginManager = new PluginManager("plugins/");
        pluginManager.detectPlugins();
        pluginManager.loadPlugins();
        pluginManager.enablePlugins();
        startConsoleReader();
        modules = new Module[]{new TPSTask()};
        for (Module mod : modules) {
            mod.setEnabled();
        }
        registerCommands();
        log.info("ЛинКор запущен ({} ms)", (System.currentTimeMillis() - start));
    }

    private void setConfig() {
        plugin.getConfig().addDefault("proxy_server.port", 10000);
        plugin.getConfig().addDefault("proxy_server.port", 10000);
        //
        plugin.getConfig().addDefault("mysql.mg.host", "localhost");
        plugin.getConfig().addDefault("mysql.mg.port", 3306);
        plugin.getConfig().addDefault("mysql.mg.user", "user");
        plugin.getConfig().addDefault("mysql.mg.pass", "password");
        plugin.getConfig().addDefault("mysql.mg.db", "database");
        //
        plugin.getConfig().addDefault("log-commands", false);
        plugin.saveConfig();
    }

    private void registerCommands() {
        pluginManager.registerCommand(plugin, new HelpCommand(this));
        pluginManager.registerCommand(plugin, new TechCommand(this));
        pluginManager.registerCommand(plugin, new OnlineCommand(this));
        pluginManager.registerCommand(plugin, new ReloadCommand(this));
        pluginManager.registerCommand(plugin, new SrvStopCommand(this));
        pluginManager.registerCommand(plugin, new StopCommand(this));
        pluginManager.registerCommand(plugin, new TPSCommand(this));
        log.info("Команды зарегистрированы");
    }

    private void startConsoleReader() {
        consoleReaderEnabled = true;
        this.primaryThread = new Thread(() -> {
            try {
                String line;
                while (consoleReaderEnabled && (line = consoleReader.readLine(">")) != null) {
                    log.info(line);
                    if (!pluginManager.dispatchCommand(consoleSender, line)) {
                        consoleSender.sendMessage(ChatColor.RED + "Команда не найдена. Введите `help` для вывода помощи");
                    }
                }
            } catch (Throwable th) {
                log.warn("", th);
            }
        }, "ConsoleThread");
        this.primaryThread.start();
    }

    private void setCorePlugin() {
        Plugin plugin = new Plugin();
        PluginDescription description = new PluginDescription();
        plugin.setEnabled(true);
        description.setName("LinCore");
        plugin.init(description);
        this.plugin = plugin;
    }

    public boolean isPrimaryThread() {
        return Thread.currentThread().equals(this.primaryThread);
    }

    public void stop() {
        log.warn("Остановка прокси сервера");
        for (Module mod : modules) {
            mod.setDisabled();
        }
        pluginManager.disablePlugins();
        Runtime.getRuntime().exit(0);
    }
}
