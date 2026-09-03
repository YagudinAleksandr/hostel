package uz.backend.database.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import uz.backend.database.entity.Dormitory;

import java.util.Optional;

/**
 * Репозиторий общежития {@link uz.backend.database.entity.Dormitory}
 *
 * @author Aleksandr Yagudin
 */
public interface DormitoryRepository extends JpaRepository<Dormitory, Long> {
    /**
     * Загрузка общежития со структурой
     *
     * @param id идентификатор
     * @return Общежитие {@link Dormitory}
     */
    @EntityGraph(attributePaths = {"entrances", "entrances.floors"})
    Optional<Dormitory> findWithStructureById(Long id);
}
