package uz.backend.database.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * Обработанные события
 *
 * @author Aleksandr Yagudin
 */
@Getter
@Setter
@Entity
@Table(name = "processed_event",
        uniqueConstraints = @UniqueConstraint(columnNames = {"event_id", "consumer_name"}))
public class ProcessedEvent implements EntityBase<UUID> {
    /**
     * Идентификатор
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Идентификатор события
     */
    @Column(name = "event_id", nullable = false, updatable = false)
    private UUID eventId;

    /**
     * Название слушателя
     */
    @Column(name = "consumer_name", nullable = false, updatable = false)
    private String consumerName;

    /**
     * Дата обработки
     */
    @Column(name = "processed_at", nullable = false, updatable = false)
    private Instant processedAt;

    @PrePersist
    private void prePersist() {
        this.processedAt = Instant.now();
    }
}
