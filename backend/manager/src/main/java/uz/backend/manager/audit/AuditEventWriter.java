package uz.backend.manager.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import uz.backend.manager.entity.AuditEvent;
import uz.backend.manager.repository.AuditEventRepository;

/**
 * Записывает события изменения сущностей в таблицу {@code audit_event}.
 * <p>
 * Слушает фазу {@link TransactionPhase#BEFORE_COMMIT}: запись аудита попадает
 * в ту же транзакцию, что и изменение данных. Откатилась бизнес-операция —
 * откатится и запись о ней.
 *
 * @author Aleksandr Yagudin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuditEventWriter {

    private final AuditEventRepository repository;

    /**
     * Обработчик события изменения сущности
     *
     * @param event событие
     */
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onEntityChanged(EntityChangedEvent event) {
        final AuditEvent record = new AuditEvent();
        record.setEntityType(event.entityType());
        record.setEntityId(event.entityId());
        record.setAction(event.action());
        record.setOccurredAt(event.occurredAt());

        repository.save(record);
        log.debug("Аудит: {} {} — {}", event.entityType(), event.entityId(), event.action());
    }
}
