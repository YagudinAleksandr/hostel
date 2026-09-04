package uz.backend.database.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uz.backend.common.enums.LicenseType;
import uz.backend.common.enums.ResponsibilityType;
import uz.backend.common.vo.Address;

import java.util.*;

/**
 * Агрегат общежития
 *
 * @author Aleksandr Yagudin
 */
@Getter
@Setter
@Entity
@Table(name = "dormitory")
public class Dormitory implements EntityBase<Long> {
    /**
     * Идентификатор
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Общежитие
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "address", nullable = false)
    private Address address;

    /**
     * Оптимистичная блокировка
     */
    @Version
    @Column(name = "version", nullable = false)
    private long version;

    /**
     * Список подъездов
     */
    @OneToMany(mappedBy = "dormitory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Entrance> entrances = new ArrayList<>();

    /**
     * Ответсвенный
     */
    @OneToMany(mappedBy = "dormitory", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Responsible> responsibles = new LinkedHashSet<>();

    /**
     * Только для Hibernate: восстановление объекта из базы.
     */
    protected Dormitory() {
    }

    /**
     * @param name    название общежития
     * @param address адрес
     */
    public Dormitory(String name, Address address) {
        this.name = requireName(name);
        this.address = Objects.requireNonNull(address, "Адрес обязателен");
    }

    /**
     * Переименование
     *
     * @param name новое название
     */
    public void rename(String name) {
        this.name = requireName(name);
    }

    /**
     * Смена адреса
     *
     * @param address новый адрес
     */
    public void relocate(Address address) {
        this.address = Objects.requireNonNull(address, "Адрес обязателен");
    }

    /**
     * Добавление нового подъезда
     *
     * @param name название
     * @return подъезд {@link Entrance}
     */
    public Entrance addEntrance(String name) {
        if (findEntrance(name).isPresent()) {
            throw new IllegalStateException("Подъезд " + name + " уже существует");
        }
        final Entrance entrance = new Entrance(this, name);
        entrances.add(entrance);
        return entrance;
    }

    /**
     * Удаляет подъезд
     *
     * @param name название
     */
    public void removeEntrance(String name) {
        final Entrance entrance = entrance(name);
        entrances.remove(entrance);
    }

    /**
     * Получение подъезда
     *
     * @param name название
     * @return подъезд
     */
    public Entrance entrance(String name) {
        return findEntrance(name)
                .orElseThrow(() -> new IllegalArgumentException("Подъезд " + name + " не найден"));
    }

    /**
     * Поиск подъезда
     *
     * @param name название
     * @return подъезд
     */
    public Optional<Entrance> findEntrance(String name) {
        return entrances.stream()
                .filter(e -> Objects.equals(e.getName(), name))
                .findFirst();
    }

    /**
     * Получить список подъездов
     *
     * @return список подъездов
     */
    public List<Entrance> getEntrances() {
        return Collections.unmodifiableList(entrances);
    }

    /**
     * Назначить менеджера ответственным
     *
     * @param managerId     идентификатор менеджера
     * @param licenseType   тип лицензии на момент назначения
     * @param licenseNumber номер лицензии на момент назначения
     * @param role          роль ответственного
     * @return назначение
     */
    public Responsible assign(Long managerId, LicenseType licenseType,
                              String licenseNumber, ResponsibilityType role) {
        Objects.requireNonNull(managerId, "Менеджер обязателен");
        Objects.requireNonNull(role, "Роль обязательна");

        if (responsibles.stream().anyMatch(r -> managerId.equals(r.getManagerId()))) {
            throw new IllegalStateException("Менеджер уже назначен ответственным");
        }
        if (role == ResponsibilityType.MAIN
                && responsibles.stream().anyMatch(r -> r.getRole() == ResponsibilityType.MAIN)) {
            throw new IllegalStateException("Главный ответственный уже назначен");
        }

        Responsible responsible = new Responsible(this, managerId, licenseType, licenseNumber, role);
        responsibles.add(responsible);
        return responsible;
    }

    /**
     * Снять менеджера с ответственности
     *
     * @param managerId идентификатор менеджера
     */
    public void unassign(Long managerId) {
        if (!responsibles.removeIf(r -> managerId.equals(r.getManagerId()))) {
            throw new IllegalArgumentException("Менеджер " + managerId + " не назначен ответственным");
        }
    }

    /**
     * Получить ответственного
     *
     * @return ответственный {@link Responsible}
     */
    public Set<Responsible> getResponsibles() {
        return Collections.unmodifiableSet(responsibles);
    }

    /**
     * Проверка заполненности названия
     *
     * @param name название
     * @return название
     */
    private static String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Название общежития обязательно");
        }
        return name;
    }
}
