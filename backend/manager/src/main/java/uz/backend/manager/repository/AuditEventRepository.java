package uz.backend.manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.backend.manager.entity.AuditEvent;

import java.util.List;

/**
 * Доступ к журналу аудита.
 *
 * @author Aleksandr Yagudin
 */
public interface AuditEventRepository extends JpaRepository<AuditEvent, Long> {

    List<AuditEvent> findAllByEntityTypeAndEntityIdOrderByIdAsc(String entityType, String entityId);
}
