package ru.lodes.lincore.api.config.file;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.*;
import lombok.NonNull;
import ru.lodes.lincore.api.config.Configuration;
import ru.lodes.lincore.api.config.InvalidConfigurationException;
import ru.lodes.lincore.api.config.MemoryConfiguration;

public abstract class FileConfiguration extends MemoryConfiguration {

    public FileConfiguration() {
        super();
    }

    public FileConfiguration(Configuration defaults) {
        super(defaults);
    }

    public void save(@NonNull File file) throws IOException {
        Files.createParentDirs(file);
        String data = saveToString();
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), Charsets.UTF_8)) {
            writer.write(data);
        }
    }

    public void save(@NonNull String file) throws IOException {
        save(new File(file));
    }

    public abstract String saveToString();

    public void load(@NonNull File file) throws FileNotFoundException, IOException, InvalidConfigurationException {
        final FileInputStream stream = new FileInputStream(file);
        load(new InputStreamReader(stream, Charsets.UTF_8));
    }

    @Deprecated
    public void load(@NonNull InputStream stream) throws IOException, InvalidConfigurationException {
        load(new InputStreamReader(stream, Charsets.UTF_8));
    }

    public void load(Reader reader) throws IOException, InvalidConfigurationException {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader input = reader instanceof BufferedReader ? (BufferedReader) reader
                : new BufferedReader(reader)) {
            String line;
            while ((line = input.readLine()) != null) {
                builder.append(line);
                builder.append('\n');
            }
        }

        loadFromString(builder.toString());
    }

    public void load(@NonNull String file) throws IOException, InvalidConfigurationException {
        load(new File(file));
    }

    public abstract void loadFromString(String contents) throws InvalidConfigurationException;

    protected abstract String buildHeader();

    @Override
    public FileConfigurationOptions options() {
        if (options == null) {
            options = new FileConfigurationOptions(this);
        }
        return (FileConfigurationOptions) options;
    }
}
