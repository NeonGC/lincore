package ru.lodes.lincore.plugin.loader;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.eventbus.Subscribe;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.jar.JarFile;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.lodes.lincore.LinCore;
import ru.lodes.lincore.api.ChatColor;
import ru.lodes.lincore.api.command.Command;
import ru.lodes.lincore.api.command.CommandSender;
import ru.lodes.lincore.api.command.TabExecutor;
import ru.lodes.lincore.api.event.*;
import ru.lodes.lincore.plugin.loader.loader.PluginAutoDetector;
import ru.lodes.lincore.plugin.loader.loader.PluginDescription;

@Slf4j
public class PluginManager {

    @NonNull
    private File container;
    @Getter(value = AccessLevel.PACKAGE)
    private final String pluginsFolder;
    private final PluginAutoDetector pluginAutoDetector;
    private final Map<String, Plugin> enabledPlugins = new HashMap<>();
    private Map<String, PluginDescription> toLoad = new HashMap<>();
    private final Map<String, Plugin> toEnable = new HashMap<>();
    private final List<String> supportedPlugins = new ArrayList<>();
    private final Multimap<Plugin, Listener> listenersByPlugin = ArrayListMultimap.create();
    private final Multimap<Plugin, Command> commandsByPlugin = ArrayListMultimap.create();
    private final Map<String, Command> commandMap = new HashMap<>(); //aliases

    public PluginManager(String pluginsFolder) {
        this.pluginsFolder = pluginsFolder;
        this.pluginAutoDetector = new PluginAutoDetector();
    }

    public void reloadContainer() {
        this.container = new File(pluginsFolder);
        container.mkdir();
    }

    public Multimap<Plugin, Command> getCommandsByPlugin() {
        return commandsByPlugin;
    }

    public Map<String, Command> getCommandMap() {
        return commandMap;
    }

    public Collection<Plugin> getPlugins() {
        return enabledPlugins.values();
    }

    public void registerPluginExtension(Class<? extends Plugin> pluginClass) {
        this.supportedPlugins.add(pluginClass.getName());
    }

    public void enablePlugins() {
        this.toEnable.forEach((key, value) -> {
            try {
                value.setEnabled(true);
                log.info("Enabled plugin {} version {}",
                        (value).getDescription().getName(),
                        (value).getDescription().getVersion());
                this.enabledPlugins.put(key, value);
            } catch (Exception var4) {
                log.warn("Exception encountered when enabling plugin: {}\n{}",
                        (value).getDescription().getName(),
                        var4.toString());
                this.disablePlugin(value, true);
            }
        });
        this.toEnable.clear();
//        LinCore.getInstance().getDataHandler().getServersList()
//                .forEach((entry) -> entry.sendPacket(new CommandListPacket()));
    }

    private void disablePlugin(Plugin plugin, boolean autoRemove) {
        if (this.enabledPlugins.containsKey(plugin.getDescription().getName())) {
            log.warn("Disabling plugin {}", plugin.getDescription().getName());
            try {
                plugin.setEnabled(false);
                unregisterCommands(plugin);
                unregisterListeners(plugin);
                LinCore.getInstance().getScheduler().cancel(plugin);
            } catch (Exception var4) {
                log.warn("Error while disabling {}", plugin.getDescription().getName());
            }
            if (autoRemove) {
                this.enabledPlugins.remove(plugin.getDescription().getName());
            }
        }
    }

    public void disablePlugins() {
        this.enabledPlugins.forEach((key, value) -> {
            try {
                this.disablePlugin(value, false);
            } catch (Exception var4) {
                log.warn("Exception encountered when unloading plugin: {}\n{}",
                        (value).getDescription().getName(),
                        var4.toString());
            }
        });
        this.enabledPlugins.clear();
        this.listenersByPlugin.clear();
        this.supportedPlugins.clear();
        ClassLoader.allUnload();
    }

    @SuppressWarnings("unchecked")
    private boolean enablePlugin(Map<PluginDescription, Boolean> pluginStatuses,
            Stack<PluginDescription> dependStack,
            PluginDescription plugin) {
        if (pluginStatuses.containsKey(plugin)) {
            return pluginStatuses.get(plugin);
        }
        HashSet<String> dependencies = new HashSet<>();
        dependencies.addAll(plugin.getDepends());
        dependencies.addAll(plugin.getSoftDepends());
        boolean status = true;
        for (String dependName : dependencies) {
            Boolean dependStatus;
            PluginDescription depend = this.toLoad.containsKey(dependName)
                    ? this.toLoad.get(dependName)
                    : (this.enabledPlugins.containsKey(dependName)
                    ? this.enabledPlugins.get(dependName).getDescription()
                    : null);
            dependStatus = depend != null ? pluginStatuses.get(depend) : Boolean.FALSE;
            if (dependStatus == null) {
                if (dependStack.contains(depend)) {
                    StringBuilder dependencyGraph = new StringBuilder();
                    dependStack.forEach(element -> dependencyGraph.append(element.getName()).append(" -> "));
                    dependencyGraph.append(plugin.getName()).append(" -> ").append(dependName);
                    log.warn("Circular dependency detected: {}", dependencyGraph);
                    status = false;
                } else {
                    dependStack.push(plugin);
                    dependStatus = this.enablePlugin(pluginStatuses, dependStack, depend);
                    dependStack.pop();
                }
            }
            if (Boolean.FALSE.equals(dependStatus) && !plugin.getSoftDepends().contains(dependName)) {
                log.warn("{} (required by {}) is unavailable", dependName, plugin.getName());
                status = false;
            }
            if (status) {
                continue;
            }
            break;
        }
        if (status) {
            try (ClassLoader cl = new ClassLoader(new URL[]{plugin.getFile().toURI().toURL()})) {
                cl.created();
                Class loadedClass = cl.loadClass(plugin.getMain());
                Plugin clazz = (Plugin) loadedClass.getConstructor(new Class[0]).newInstance(new Object[0]);
                clazz.init(this, plugin);
                this.toEnable.put(plugin.getName(), clazz);
                clazz.onLoad();
                log.info("Loaded plugin {} version {}", plugin.getName(), plugin.getVersion());
            } catch (Exception var13) {
                log.warn("Error enabling plugin {}\n{}", plugin.getName(), var13);
            }
        }
        pluginStatuses.put(plugin, status);
        return status;
    }

    public void detectPlugins() {
        reloadContainer();
        File[] filesToLoad;
        Preconditions.checkArgument(this.container.isDirectory(), "Must load from a directory");
        if (this.toLoad == null) {
            this.toLoad = new HashMap<>();
        }
        if ((filesToLoad = this.container.listFiles()) != null) {
            for (File file : filesToLoad) {
                if (!file.isFile() || !file.getName().endsWith(".jar")) {
                    continue;
                }
                try {
                    JarFile jar = new JarFile(file);
                    Throwable var7 = null;
                    try {
                        @NonNull PluginDescription desc = this.pluginAutoDetector.checkPlugin(this.supportedPlugins, jar);
                        if (this.enabledPlugins.containsKey(desc.getName())) {
                            continue;
                        }
                        desc.setFile(file);
                        this.toLoad.put(desc.getName(), desc);
                    } catch (Throwable var16) {
                        var7 = var16;
                        throw var16;
                    }
                    finally {
                        if (var7 != null) {
                            try {
                                jar.close();
                            } catch (IOException var17) {
                                var7.addSuppressed(var17);
                            }
                        } else {
                            jar.close();
                        }
                    }
                } catch (Exception var19) {
                    log.warn("Could not load plugin from file {}\n{}", file, var19);
                }
            }
        }
    }

    public void loadPlugins() {
        Map<PluginDescription, Boolean> pluginStatuses = new HashMap<>();
        this.toLoad.forEach((key, plugin) -> {
            if (!(this.enablePlugin(pluginStatuses, new Stack<>(), plugin))) {
                log.warn("Failed to enable {}", key);
            }
        });
        this.toLoad.clear();
    }

    public void init(PluginManager aThis, PluginDescription plugin) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public <T extends Event> T callEvent(T event) {
        long start = System.nanoTime();
        if (event.isAsynchronous()) {
            if (Thread.holdsLock(this)) {
                throw new IllegalStateException(event.getEventName() + " cannot be triggered asynchronously from inside synchronized code.");
            }
            if (LinCore.getInstance().isPrimaryThread()) {
                throw new IllegalStateException(event.getEventName() + " cannot be triggered asynchronously from primary server thread.");
            }
            fireEvent(event);
        } else {
            synchronized (this) {
                fireEvent(event);
            }
        }
        long elapsed = start - System.nanoTime();
        if (elapsed > 250000) {
            log.warn("Event {} took more {}ns to process!", event, elapsed);
        }
        return event;
    }

    private void fireEvent(Event event) {
        HandlerList handlers = event.getHandlers();
        RegisteredListener[] listeners = handlers.getRegisteredListeners();

        for (RegisteredListener registration : listeners) {
            if (!registration.getPlugin().isEnabled()) {
                continue;
            }

            try {
                registration.callEvent(event);
            } catch (EventException ex) {
                log.error("Could not pass event {} to {}",
                        event.getEventName(),
                        registration.getPlugin().getDescription().getName(),
                        ex);
            }
        }
    }

    private HandlerList getEventListeners(Class<? extends Event> type) {
        try {
            Method method = getRegistrationClass(type).getDeclaredMethod("getHandlerList");
            method.setAccessible(true);
            return (HandlerList) method.invoke(null);
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
            throw new IllegalPluginAccessException(e.toString());
        }
    }

    private Class<? extends Event> getRegistrationClass(Class<? extends Event> clazz) {
        try {
            clazz.getDeclaredMethod("getHandlerList");
            return clazz;
        } catch (NoSuchMethodException e) {
            if (clazz.getSuperclass() != null
                    && !clazz.getSuperclass().equals(Event.class)
                    && Event.class.isAssignableFrom(clazz.getSuperclass())) {
                return getRegistrationClass(clazz.getSuperclass().asSubclass(Event.class));
            } else {
                throw new IllegalPluginAccessException("Unable to find handler list for event " + clazz.getName() + ". Static getHandlerList method required!");
            }
        }
    }

    public void registerEvent(Class<? extends Event> event,
            Listener listener,
            EventPriority priority,
            EventExecutor executor,
            Plugin plugin) {
        registerEvent(event, listener, priority, executor, plugin, false);
    }

    public void registerEvent(Class<? extends Event> event,
            @NonNull Listener listener,
            @NonNull EventPriority priority,
            @NonNull EventExecutor executor,
            @NonNull Plugin plugin,
            boolean ignoreCancelled) {
        if (!plugin.isEnabled()) {
            throw new IllegalPluginAccessException("Plugin attempted to register " + event + " while not enabled");
        }
        getEventListeners(event)
                .register(new RegisteredListener(listener, priority, plugin, executor, ignoreCancelled));
    }
    
    public void registerListener(Plugin plugin, Listener listener) {
        for (Method method : listener.getClass().getDeclaredMethods()) {
            Preconditions.checkArgument(!method.isAnnotationPresent(Subscribe.class),
                    "Listener %s has registered using deprecated subscribe annotation! Please update to @EventHandler.",
                    listener);
        }
        if (!plugin.isEnabled()) {
            throw new IllegalPluginAccessException("Plugin attempted to register " + listener + " while not enabled");
        }
        new EventBus(listener, plugin).createRegisteredListeners()
                .forEach((key, value) -> getEventListeners(getRegistrationClass(key)).registerAll(value));
        this.listenersByPlugin.put(plugin, listener);
    }

    public void registerListener(Listener listener) {
        registerListener(LinCore.getInstance().getPlugin(), listener);
    }

    public void unregisterListener(Listener listener) {
        HandlerList.unregisterAll(listener);
        this.listenersByPlugin.values().remove(listener);
    }

    public void unregisterListeners(Plugin plugin) {
        Iterator<Listener> it = this.listenersByPlugin.get(plugin).iterator();
        while (it.hasNext()) {
            HandlerList.unregisterAll(it.next());
            it.remove();
        }
    }

    public void registerCommand(Plugin plugin, Command command) {
        commandMap.put(command.getName().toLowerCase(), command);
        for (String alias : command.getAliases()) {
            commandMap.put(alias.toLowerCase(), command);
        }
        commandsByPlugin.put(plugin, command);
    }

    public void unregisterCommand(Command command) {
        commandMap.values().clear();
        commandsByPlugin.values().remove(command);
    }

    public void unregisterCommands(Plugin plugin) {
        while (commandsByPlugin.containsKey(plugin)) {
            commandsByPlugin.removeAll(plugin).forEach((cmd) -> {
                Collection<String> cmds = new ArrayList<>();
                commandMap.entrySet().stream()
                        .filter((entry) -> entry.getValue() == cmd)
                        .forEach((entry) -> cmds.add(entry.getKey()));
                cmds.forEach(commandMap::remove);
            });
        }
    }

    public boolean dispatchCommand(CommandSender sender, String commandLine) {
        return dispatchCommand(sender, commandLine, null);
    }

    public boolean dispatchCommand(CommandSender sender, String commandLine, List<String> tabResults) {
        String[] split = commandLine.split(" ", -1);
        if (split.length == 0) {
            return false;
        }

        String commandName = split[0].toLowerCase();
        Command command = commandMap.get(commandName);
        if (command == null) {
            return false;
        }

//        String permission = command.getPermission();
//        if (permission != null && !permission.isEmpty() && !sender.hasPermission(permission)) {
//            if (tabResults == null) {
//                sender.sendMessage(proxy.getTranslation("no_permission"));
//            }
//            return true;
//        }
        String[] args = Arrays.copyOfRange(split, 1, split.length);
        try {
            if (tabResults == null) {
                if (LinCore.getInstance().getPlugin().getConfig().getBoolean("log-commands")) {
                    log.info("{} выполнил команду: /{}", sender.getName(), commandLine);
                }
                command.execute(sender, args);
            } else if (commandLine.contains(" ") && command instanceof TabExecutor) {
                for (String s : ((TabExecutor) command).onTabComplete(sender, args)) {
                    tabResults.add(s);
                }
            }
        } catch (Exception ex) {
            sender.sendMessage(ChatColor.RED + "Во время выполнения этой команды произошла внутренняя ошибка, "
                    + "пожалуйста, проверьте консоль...");
            log.warn("Ошибка при выполнении команды", ex);
        }
        return true;
    }
}
