package uz.backend.manager.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uz.backend.common.enums.AuditAction;
import uz.backend.manager.entity.base.BaseEntity;

import java.time.Instant;

/**
 * Запись журнала аудита: что за сущность, какая, что с ней произошло и когда.
 * <p>
 * Намеренно не наследует {@code AuditableEntity} — иначе запись аудита сама
 * порождала бы событие аудита.
 *
 * @author Aleksandr Yagudin
 */
@Getter
@Setter
@Entity
@Table(name = "audit_event")
public class AuditEvent implements BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Простое имя класса сущности */
    @Column(name = "entity_type", nullable = false)
    private String entityType;

    /** Идентификатор сущности в строковом виде */
    @Column(name = "entity_id")
    private String entityId;

    /** Что произошло */
    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 32)
    private AuditAction action;

    /** Когда произошло */
    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;
}
