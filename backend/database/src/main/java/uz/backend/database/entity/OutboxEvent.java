package uz.backend.database.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uz.backend.common.enums.DLQStatusType;
import uz.backend.common.enums.EventType;

import java.time.Instant;
import java.util.UUID;

/**
 * Внешние события
 */
@Getter
@Setter
@Entity
@Table(name = "outbox_event")
public class OutboxEvent implements EntityBase<UUID> {
    /**
     * Идентификатор
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Тип события {@link EventType}
     */
    @Column(name = "event_type", nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private EventType eventType;

    /**
     * Сущность события
     */
    @Column(name = "event_entity", updatable = false)
    private String eventEntity;

    /**
     * Идентификатор агрегата
     */
    @Column(name = "aggregated_id", updatable = false)
    private UUID aggregatedId;

    /**
     * Тело события
     */
    @Column(name = "payload", nullable = false, updatable = false)
    private String payload;

    /**
     * Количество попыток
     */
    @Column(name = "send_attempts", nullable = false, columnDefinition = "1")
    private int sendAttempts;

    /**
     * Последняя ошибка
     */
    @Column(name = "last_error")
    private String lastError;

    /**
     * Статус {@link DLQStatusType}
     */
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private DLQStatusType status;

    /**
     * Дата создания
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Дата последней попытки
     */
    @Column(name = "last_attempt")
    private Instant lastAttempt;

    @PrePersist
    private void prePersist() {
        this.createdAt = Instant.now();
    }

    @PreUpdate
    private void preUpdate() {
        this.lastAttempt = Instant.now();
    }
}
