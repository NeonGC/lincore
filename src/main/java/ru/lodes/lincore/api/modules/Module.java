package ru.lodes.lincore.api.modules;

/**
 * Интерфейс для создания задач task/sheduler
 *
 * @author NeonGC
 */
public interface Module {

    /**
     * Включение задачи
     */
    void setEnabled();

    /**
     * Выключение задачи
     */
    void setDisabled();

    /**
     * Проверка, включена ли задача
     *
     * @return
     */
    boolean isEnabled();
}
