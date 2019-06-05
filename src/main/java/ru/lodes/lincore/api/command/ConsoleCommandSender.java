package ru.lodes.lincore.api.command;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsoleCommandSender implements CommandSender {

    @Getter
    private static final ConsoleCommandSender instance = new ConsoleCommandSender();

    @Override
    public void sendMessage(String message) {
        log.info(message);
    }

    @Override
    public void sendMessages(String... messages) {
        for (String msg : messages) {
            sendMessage(msg);
        }
    }

    @Override
    public String getName() {
        return "CONSOLE";
    }
}
