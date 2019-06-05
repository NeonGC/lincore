package ru.lodes.lincore.api.config;

import java.util.Map;
import lombok.NonNull;

public class MemoryConfiguration extends MemorySection implements Configuration {

    protected Configuration defaults;
    protected MemoryConfigurationOptions options;

    public MemoryConfiguration() {
    }

    public MemoryConfiguration(Configuration defaults) {
        this.defaults = defaults;
    }

    @Override
    public void addDefault(@NonNull String path, Object value) {
        if (defaults == null) {
            defaults = new MemoryConfiguration();
        }
        defaults.set(path, value);
    }

    @Override
    public void addDefaults(@NonNull Map<String, Object> defaults) {
        defaults.forEach(this::addDefault);
    }

    @Override
    public void addDefaults(@NonNull Configuration defaults) {
        addDefaults(defaults.getValues(true));
    }

    @Override
    public void setDefaults(@NonNull Configuration defaults) {
        this.defaults = defaults;
    }

    @Override
    public Configuration getDefaults() {
        return defaults;
    }

    @Override
    public ConfigurationSection getParent() {
        return null;
    }

    @Override
    public MemoryConfigurationOptions options() {
        if (options == null) {
            options = new MemoryConfigurationOptions(this);
        }
        return options;
    }
}
