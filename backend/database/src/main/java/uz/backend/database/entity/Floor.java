package uz.backend.database.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

/**
 * Сущность этажа
 *
 * @author Aleksandr Yagudin
 */
@Getter
@Setter
@Entity
@Table(name = "floor",
        uniqueConstraints = @UniqueConstraint(name = "ux_floor_entrance_name",
                columnNames = {"entrance_id", "name"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Floor implements EntityBase<Long> {
    /**
     * Идентификатор
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Подъезд
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "entrance_id", nullable = false)
    private Entrance entrance;

    /**
     * Название этажа
     * <p>Уникально в пределах подъезда</p>
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Этаж
     *
     * @param entrance подъезд
     * @param name     название
     */
    Floor(Entrance entrance, String name) {
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Название не может быть пустым");
        }
        this.entrance = entrance;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        return id != null && id.equals(((Floor) o).getId());
    }

    @Override
    public int hashCode() {
        return Hibernate.getClass(this).hashCode();
    }
}
