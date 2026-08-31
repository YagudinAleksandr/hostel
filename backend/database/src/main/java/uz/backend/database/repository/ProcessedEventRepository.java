package uz.backend.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.backend.database.entity.ProcessedEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Репозиторий обработанных событий {@link ProcessedEvent}
 *
 * @author Aleksandr Yagudin
 */
public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, UUID> {
    /**
     * Проверка существования обработанного события по идентификатору события и имени слушателя
     *
     * @param eventId      идентификатор внешнего события
     * @param consumerName имя слушателя
     * @return {@code true} - существует, {@code false} - отсутствует
     */
    boolean existsByEventIdAndConsumerName(UUID eventId, String consumerName);

    /**
     * Удаление обработанного события по времени до
     *
     * @param before время до
     * @return идентификатор
     */
    int deleteByProcessedAtBefore(Instant before);
}
