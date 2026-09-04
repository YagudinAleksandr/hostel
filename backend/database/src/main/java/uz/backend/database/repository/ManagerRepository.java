package uz.backend.database.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import uz.backend.database.entity.Manager;

import java.util.Optional;

/**
 * Репозиторий агрегата {@link Manager}
 *
 * @author Aleksandr Yagudin
 */
public interface ManagerRepository extends JpaRepository<Manager, Long> {
    /**
     * Получение менеджера с лицензиями
     *
     * @param id идентификатор
     * @return менеджер с лицензиями
     */
    @EntityGraph(attributePaths = "licenses")
    Optional<Manager> findWithLicensesById(Long id);
}
