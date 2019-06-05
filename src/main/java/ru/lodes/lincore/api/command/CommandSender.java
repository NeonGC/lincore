package ru.lodes.lincore.api.command;

public interface CommandSender {

    void sendMessage(String message);

    void sendMessages(String... messages);

    String getName();
}
