package uz.backend.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.backend.common.enums.DLQStatusType;
import uz.backend.database.entity.OutboxEvent;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Репозиторий внешних событий {@link OutboxEvent}
 *
 * @author Aleksandr Yagudin
 */
public interface OutboxRepository extends JpaRepository<OutboxEvent, UUID> {
    /**
     * Получение списка внешних событий по статусам и количеству попыток
     *
     * @param statuses    статусы {@link DLQStatusType}
     * @param maxAttempts количество попыток
     * @return список внешних событий {@link OutboxEvent}
     */
    List<OutboxEvent> findByStatusInAndSendAttemptsLessThan(List<DLQStatusType> statuses,
                                                            int maxAttempts);

    /**
     * Поиск по статусу
     *
     * @param status статус {@link DLQStatusType}
     * @return список внешних событий
     */
    List<OutboxEvent> findByStatus(DLQStatusType status);

    /**
     * Получение списка событий по идентификаторам
     *
     * @param ids идентификаторы
     * @return список событий {@link OutboxEvent}
     */
    List<OutboxEvent> findByIdIn(List<UUID> ids);

    /**
     * Получение списка событий по статусу и времени до
     *
     * @param status статус внешнего события {@link DLQStatusType}
     * @param before время события до
     * @return список внешних событий {@link OutboxEvent}
     */
    List<OutboxEvent> findByStatusAndCreatedAtBefore(DLQStatusType status, Instant before);
}
