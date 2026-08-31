package uz.backend.common.enums;

/**
 * Типы событий
 *
 * @author Aleksandr Yagudin
 */
public enum EventType {
    /**
     * Создание
     */
    CREATED,

    /**
     * Обновление
     */
    UPDATED,

    /**
     * Удаление
     */
    DELETED,

    /**
     * Блокировка
     */
    BLOCKED,

    /**
     * Разблокировка
     */
    UNBLOCKED
}
