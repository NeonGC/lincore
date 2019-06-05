package ru.lodes.lincore.utils;

//Используется ТОЛЬКО для отправки на bungeecord
public class ChatUtil {

    private final String text;
    private String click;
    private String hover;

    public ChatUtil(String text) {
        this.text = "\"text\":\"" + text + "\"";
    }

    public ChatUtil setClickEvent(ClickEvent action, String value) {
        this.click = "\"clickEvent\":{\"action\":\"" + action.name().toLowerCase() + "\",\"value\":\"" + value + "\"}";
        return this;
    }

    public ChatUtil setHoverEvent(HoverEvent action, String value) {
        this.hover = "\"hoverEvent\":{\"action\":\"" + action.name()
                .toLowerCase() + "\",\"value\":[{\"text\":\"" + value + "\"}]}";
        return this;
    }

    public String getTextComponent() {
        String msg = "";
        msg += "{";
        if (this.click != null) {
            msg += this.click;
        }
        if (this.hover != null) {
            msg += "," + this.hover;
        }
        msg += "," + this.text;
        msg += "}";
        return msg;
    }

    public enum ClickEvent {
        OPEN_URL,
        OPEN_FILE,
        RUN_COMMAND,
        SUGGEST_COMMAND,
        CHANGE_PAGE
    }

    public enum HoverEvent {
        SHOW_TEXT,
        SHOW_ACHIEVEMENT,
        SHOW_ITEM,
        SHOW_ENTITY
    }
}
