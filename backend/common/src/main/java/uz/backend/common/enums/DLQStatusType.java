package uz.backend.common.enums;

/**
 * Статусы сообщений
 *
 * @author Aleksandr Yagudin
 */
public enum DLQStatusType {
    /**
     * Ожидание
     */
    PENDING,

    /**
     * Отправка
     */
    SENDING,

    /**
     * Отправлено
     */
    SENT,

    /**
     * Ошибка
     */
    FAILED
}
