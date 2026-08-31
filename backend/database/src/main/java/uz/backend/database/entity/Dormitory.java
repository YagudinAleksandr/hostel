package uz.backend.database.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
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

    public Dormitory() {
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
