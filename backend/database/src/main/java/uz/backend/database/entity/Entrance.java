package uz.backend.database.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Подъезда
 *
 * @author Aleksandr Yagudi
 */
@Getter
@Setter
@Table(
        name = "entrance",
        uniqueConstraints = @UniqueConstraint(
                name = "ux_entrance_dormitory_number",
                columnNames = {"dormitory_id", "number"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Entrance implements EntityBase<Long> {
    /**
     * Идентификатор
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Общежитие
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dormitory_id", nullable = false)
    private Dormitory dormitory;

    /**
     * Название
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Список этажей
     */
    @OneToMany(mappedBy = "entrance", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Floor> floors = new ArrayList<>();

    /**
     * Подъезд
     *
     * @param dormitory общежитие {@link Dormitory}
     * @param name      название
     */
    Entrance(Dormitory dormitory, String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Название не может быть пустым");
        }
        this.dormitory = dormitory;
        this.name = name;
    }

    /**
     * Создание этажа
     *
     * @param name название
     * @return этаж {@link Floor}
     */
    public Floor addFloor(String name) {
        if (findFloor(name).isPresent()) {
            throw new IllegalArgumentException("Этаж " + name + " в подъезде " + this.name + " уже существует");
        }

        final Floor floor = new Floor(this, name);
        floors.add(floor);
        return floor;
    }

    /**
     * Удаление этажа
     *
     * @param name название
     */
    public void removeFloor(String name) {
        floors.remove(floor(name));
    }

    /**
     * Этаж
     *
     * @param name название
     * @return этаж {@link Floor}
     */
    public Floor floor(String name) {
        return findFloor(name)
                .orElseThrow(() -> new IllegalArgumentException("Этаж " + name + " не найден"));
    }

    /**
     * Найти этаж
     *
     * @param name название
     * @return Этаж {@link Floor}
     */
    public Optional<Floor> findFloor(String name) {
        return floors.stream().filter(floor -> floor.getName().equals(name)).findFirst();
    }

    /**
     * Получить этажи
     *
     * @return список этажей {@link Floor}
     */
    public List<Floor> getFloors() {
        return Collections.unmodifiableList(floors);
    }

    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false;
        return id != null && id.equals(((Entrance) other).getId());
    }

    @Override
    public int hashCode() {
        return Hibernate.getClass(this).hashCode();
    }
}
