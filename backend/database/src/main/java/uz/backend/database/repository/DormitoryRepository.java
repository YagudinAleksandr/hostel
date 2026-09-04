package uz.backend.database.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.backend.database.entity.Dormitory;

import java.util.List;
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

    /**
     * Получить общежитие с ответственным
     *
     * @param id идентификатор
     * @return общежитие с ответственным
     */
    @EntityGraph(attributePaths = "responsibles")
    Optional<Dormitory> findWithResponsiblesById(Long id);

    /**
     * Общежития, за которые отвечает менеджер
     *
     * @param managerId идентификатор менеджера
     * @return общежития
     */
    @Query("select r.dormitory from Responsible r where r.managerId = :managerId")
    List<Dormitory> findAllByResponsibleManagerId(@Param("managerId") Long managerId);
}
