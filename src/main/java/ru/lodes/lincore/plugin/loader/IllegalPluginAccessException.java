package ru.lodes.lincore.plugin.loader;

public class IllegalPluginAccessException extends RuntimeException {

    public IllegalPluginAccessException() {
    }

    public IllegalPluginAccessException(String msg) {
        super(msg);
    }
}
