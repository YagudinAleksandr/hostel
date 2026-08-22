package uz.backend.manager.audit;

import uz.backend.common.enums.AuditAction;

import java.time.Instant;

/**
 * Событие изменения аудируемой сущности.
 * Публикуется {@link AuditEntityListener} и потребляется {@link AuditEventWriter}.
 *
 * @param entityType простое имя класса сущности
 * @param entityId   идентификатор в строковом виде — типы ключей у сущностей разные
 * @param action     что произошло
 * @param occurredAt момент события
 */
public record EntityChangedEvent(
        String entityType,
        String entityId,
        AuditAction action,
        Instant occurredAt
) {
}
