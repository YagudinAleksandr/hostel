package uz.backend.manager.audit;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import uz.backend.common.enums.AuditAction;
import uz.backend.manager.entity.base.BaseEntity;

import java.time.Instant;

/**
 * Слушатель жизненного цикла аудируемых сущностей.
 * <p>
 * Подключается к {@code AuditableEntity} через {@code @EntityListeners}, поэтому
 * срабатывает для всех её наследников. Сам ничего не пишет в базу: обращение к
 * {@code EntityManager} изнутри callback-а происходит посреди flush и приводит к
 * непредсказуемому поведению. Вместо этого публикует событие, которое
 * {@link AuditEventWriter} обрабатывает уже на границе транзакции.
 *
 * @author Aleksandr Yagudin
 */
@Component
@RequiredArgsConstructor
public class AuditEntityListener {

    private final ApplicationEventPublisher publisher;

    @PostPersist
    void onCreate(Object entity) {
        publish(entity, AuditAction.CREATED);
    }

    @PostUpdate
    void onUpdate(Object entity) {
        publish(entity, AuditAction.UPDATED);
    }

    @PostRemove
    void onDelete(Object entity) {
        publish(entity, AuditAction.DELETED);
    }

    private void publish(Object entity, AuditAction action) {
        final Object id = entity instanceof BaseEntity<?> base ? base.getId() : null;

        publisher.publishEvent(new EntityChangedEvent(
                Hibernate.getClass(entity).getSimpleName(),
                id == null ? null : String.valueOf(id),
                action,
                Instant.now()));
    }
}
