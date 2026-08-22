package uz.backend.common.enums;

/**
 * Действие над сущностью, попадающее в журнал аудита.
 *
 * @author Aleksandr Yagudin
 */
public enum AuditAction {

    /** Сущность создана */
    CREATED,

    /** Сущность изменена */
    UPDATED,

    /** Сущность удалена */
    DELETED
}
