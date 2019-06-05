package ru.lodes.lincore.plugin.loader.loader;

import java.io.File;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PluginDescription {

    private String name;
    private String main;
    private String version;
    private Set<String> depends;
    private Set<String> softDepends;
    private File file = null;

}
