package ru.lodes.lincore.plugin.loader.loader;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.StringMemberValue;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PluginAutoDetector {

    public PluginDescription checkPlugin(List<String> additionalPluginMains, JarFile jarFile) {
        Enumeration<JarEntry> jarEntries = jarFile.entries();
        try {
            while (jarEntries.hasMoreElements()) {
                ClassFile classFile;
                JarEntry jarEntry = jarEntries.nextElement();
                if (jarEntry == null
                        || !jarEntry.getName().endsWith(".class")
                        || !(classFile = new ClassFile(
                                new DataInputStream(
                                        jarFile.getInputStream(jarEntry)
                                )
                        )).getSuperclass().equals("ru.lodes.lincore.plugin.loader.Plugin")
                        && !additionalPluginMains.contains(classFile.getSuperclass())) {
                    continue;
                }
                log.info("Found entry class candidate: {}", classFile.getClass().getName());
                PluginDescription pluginDescription = new PluginDescription();
                AnnotationsAttribute visible = (AnnotationsAttribute) classFile.getAttribute("RuntimeVisibleAnnotations");
                for (Annotation annotation : visible.getAnnotations()) {
                    switch (annotation.getTypeName()) {
                        case "ru.lodes.lincore.plugin.loader.annotations.Description": {
                            MemberValue[] values;
                            pluginDescription.setName(((StringMemberValue) annotation.getMemberValue("name")).getValue());
                            pluginDescription.setVersion(((StringMemberValue) annotation.getMemberValue("version")).getValue());
                            ArrayMemberValue arrayMemberValue = (ArrayMemberValue) annotation.getMemberValue("dependencies");
                            if (arrayMemberValue != null) {
                                values = arrayMemberValue.getValue();
                                String[] depend = new String[values.length];
                                for (int g = 0;g < values.length;++g) {
                                    depend[g] = ((StringMemberValue) values[g]).getValue();
                                }
                                pluginDescription.setDepends(this.newHashSet((String[]) depend));
                            } else {
                                pluginDescription.setDepends(new HashSet<>());
                            }
                            arrayMemberValue = (ArrayMemberValue) annotation.getMemberValue("softdependencies");
                            if (arrayMemberValue != null) {
                                values = arrayMemberValue.getValue();
                                String[] depends = new String[values.length];
                                for (int i = 0;i < values.length;++i) {
                                    depends[i] = ((StringMemberValue) values[i]).getValue();
                                }
                                pluginDescription.setSoftDepends(this.newHashSet(depends));
                                continue;
                            }
                            pluginDescription.setSoftDepends(new HashSet<>());
                        }
                        default:;
                    }
                }
                if (pluginDescription.getName() == null) {
                    return null;
                }
                pluginDescription.setMain(classFile.getName());
                log.info("Loaded full description: {}", pluginDescription);
                return pluginDescription;
            }
            return null;
        } catch (IOException e) {
            log.warn("Could not load Plugin. File {} is corrupted {}", jarFile, e.toString(), e);
            return null;
        }
    }

    private Set<String> newHashSet(String[] depends) {
        HashSet<String> set = new HashSet<>();
        Collections.addAll(set, depends);
        return set;
    }
}
