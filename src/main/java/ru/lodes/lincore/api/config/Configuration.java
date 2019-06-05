package ru.lodes.lincore.api.config;

import java.util.Map;

public interface Configuration extends ConfigurationSection {

    @Override
    void addDefault(String path, Object value);

    void addDefaults(Map<String, Object> defaults);

    void addDefaults(Configuration defaults);

    void setDefaults(Configuration defaults);

    Configuration getDefaults();

    ConfigurationOptions options();
}
