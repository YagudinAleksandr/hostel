package uz.backend.manager.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

import uz.backend.manager.audit.AuditEntityListener;

import java.time.Instant;

/**
 * Сущность аудита
 *
 * @author Aleksandr Yagudin
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditEntityListener.class)
public abstract class AuditableEntity {
    /**
     * Дата создания
     */
    @Column(name = "createdAt", nullable = false)
    private Instant createdAt;

    /**
     * Дата изменения
     */
    @Column(name = "updatedAt", nullable = true)
    private Instant updatedAt;

    @PrePersist
    private void prePersist() {
        this.createdAt = Instant.now();
    }

    @PreUpdate
    private void preUpdate() {
        this.updatedAt = Instant.now();
    }
}
