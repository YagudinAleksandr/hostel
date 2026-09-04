package uz.backend.database.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import uz.backend.common.enums.LicenseType;
import uz.backend.common.enums.ResponsibilityType;

import java.time.LocalDateTime;

/**
 * Назначение менеджера ответственным за общежитие.
 * Часть агрегата {@link Dormitory}, создаётся только через корень.
 *
 * @author Aleksandr Yagudin
 */
@Getter
@Setter
@Entity
@Table(name = "dormitory_responsible",
        uniqueConstraints = @UniqueConstraint(
                name = "ux_dormitory_responsible_manager",
                columnNames = {"dormitory_id", "manager_id"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Responsible implements EntityBase<Long> {
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
     * Менеджер из чужого агрегата: только идентификатор корня, без {@code @ManyToOne}
     */
    @Column(name = "manager_id", nullable = false)
    private Long managerId;

    /**
     * Тип лицензии на момент назначения — зафиксированный факт, а не ссылка
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "license_type", length = 30, nullable = false)
    private LicenseType licenseType;

    /**
     * Номер лицензии на момент назначения
     */
    @Column(name = "license_number", length = 50, nullable = false)
    private String licenseNumber;

    /**
     * Роль ответственного {@link ResponsibilityType}
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 30, nullable = false)
    private ResponsibilityType role;

    /**
     * Дата добавления
     */
    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    /**
     * Ответственный за общежитие
     *
     * @param dormitory     общежитие
     * @param managerId     идентификатор менеджера
     * @param licenseType   тип лицензии
     * @param licenseNumber номер лицензии
     * @param role          роль
     */
    Responsible(Dormitory dormitory, Long managerId, LicenseType licenseType,
                String licenseNumber, ResponsibilityType role) {
        this.dormitory = dormitory;
        this.managerId = managerId;
        this.licenseType = licenseType;
        this.licenseNumber = licenseNumber;
        this.role = role;
    }

    @PrePersist
    void onPersist() {
        assignedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false;
        return id != null && id.equals(((Responsible) other).getId());
    }

    @Override
    public int hashCode() {
        return Hibernate.getClass(this).hashCode();
    }
}
