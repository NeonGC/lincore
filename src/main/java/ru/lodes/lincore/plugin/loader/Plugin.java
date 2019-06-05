package ru.lodes.lincore.plugin.loader;

import com.google.common.base.Charsets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.lodes.lincore.LinCore;
import ru.lodes.lincore.api.config.file.FileConfiguration;
import ru.lodes.lincore.api.config.file.YamlConfiguration;
import ru.lodes.lincore.api.sheduler.GroupedThreadFactory;
import ru.lodes.lincore.plugin.loader.loader.PluginDescription;

@Slf4j
public class Plugin {

    private PluginDescription description;
    private File file;
    private File configFile;
    private FileConfiguration config;
    @Getter
    private File dataFolder;
    @Getter
    private boolean enabled = false;
    private ExecutorService service;
    private static final String cfg = "config.yml";

    public void onEnable() {
    }

    public void onLoad() {
    }

    public void onDisable() {
    }

    public PluginDescription getDescription() {
        return this.description;
    }

    public final void setEnabled(final boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            if (this.enabled) {
                onEnable();
            } else {
                onDisable();
            }
        }
    }

    public LinCore getCore() {
        return LinCore.getInstance();
    }

    public FileConfiguration getConfig() {
        if (this.config == null) {
            reloadConfig();
        }
        return this.config;
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);

        final InputStream defConfigStream = getResource(cfg);
        if (defConfigStream == null) {
            return;
        }

        config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
    }

    public final InputStream getResourceAsStream(String name) {
        return this.getClass().getClassLoader().getResourceAsStream(name);
    }

    public void saveConfig() {
        try {
            getConfig().save(configFile);
        } catch (IOException ex) {
            log.error("Could not save config to {}", configFile, ex);
        }
    }

    public void saveDefaultConfig() {
        if (!configFile.exists()) {
            saveResource(cfg, false);
        }
    }

    public void saveResource(String resourcePath, boolean replace) {
        if (resourcePath == null || resourcePath.equals("")) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }
        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = getResource(resourcePath);
        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in " + file);
        }
        File outFile = new File(dataFolder, resourcePath);
        int lastIndex = resourcePath.lastIndexOf('/');
        File outDir = new File(dataFolder, resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));
        if (!outDir.exists()) {
            outDir.mkdirs();
        }
        try {
            if (!outFile.exists() || replace) {
                try (OutputStream out = new FileOutputStream(outFile)) {
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                }
                in.close();
            } else {
                log.warn("Could not save {} to {} because {} already exists.",
                        outFile.getName(),
                        outFile,
                        outFile.getName());
            }
        } catch (IOException ex) {
            log.error("Could not save {} to {}", outFile.getName(), outFile, ex);
        }
    }

    public InputStream getResource(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null");
        }
        try {
            URL url = this.getClass().getClassLoader().getResource(filename);
            if (url == null) {
                return null;
            }
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException ex) {
            return null;
        }
    }

    public void init(PluginDescription description) {
        init(null, description);
    }

    public void init(PluginManager pluginManager, PluginDescription description) {
        this.description = description;
        this.file = description.getFile();
        String plugFolder = pluginManager == null ? "/" : pluginManager.getPluginsFolder();
        this.dataFolder = new File(plugFolder, this.description.getName());
        this.configFile = new File(dataFolder, cfg);
    }

    @Deprecated
    public ExecutorService getExecutorService() {
        if (service == null) {
            String name = (description == null) ? "unknown" : description.getName();
            service = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat(name + " Pool Thread #%1$d")
                    .setThreadFactory(new GroupedThreadFactory(this, name))
                    .build());
        }
        return service;
    }
}
