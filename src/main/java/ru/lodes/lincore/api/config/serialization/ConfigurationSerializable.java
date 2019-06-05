package ru.lodes.lincore.api.config.serialization;

import java.util.Map;

public interface ConfigurationSerializable {

    Map<String, Object> serialize();
}
