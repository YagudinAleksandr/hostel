package uz.backend.database.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import uz.backend.common.enums.LicenseType;

import java.time.LocalDate;

/**
 * Лицензия менеджера. Часть агрегата {@link Manager}, создаётся только через корень.
 *
 * @author Aleksandr Yagudin
 */
@Getter
@Setter
@Entity
@Table(name = "license")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class License implements EntityBase<Long> {
    /**
     * Идентификатор
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Менеджер
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "manager_id", nullable = false)
    private Manager manager;

    /**
     * Тип лицензии {@link LicenseType}
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 30, nullable = false)
    private LicenseType type;

    /**
     * Номер лицензии
     */
    @Column(name = "number", length = 50, nullable = false)
    private String number;

    /**
     * Выдана
     */
    @Column(name = "issued_at", nullable = false)
    private LocalDate issuedAt;

    /**
     * Дата окончания, {@code null} — бессрочная
     */
    @Column(name = "expires_at")
    private LocalDate expiresAt;

    /**
     * Дата отзыва, {@code null} — не отозвана
     */
    @Column(name = "revoked_at")
    private LocalDate revokedAt;

    License(Manager manager, LicenseType type, String number, LocalDate issuedAt, LocalDate expiresAt) {
        this.manager = manager;
        this.type = type;
        this.number = number;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
    }

    /**
     * Действует ли лицензия на указанную дату
     *
     * @param date дата
     * @return {@code true}, если лицензия не отозвана и дата попадает в срок действия
     */
    public boolean isActiveOn(LocalDate date) {
        if (revokedAt != null && !revokedAt.isAfter(date)) {
            return false;
        }
        if (issuedAt.isAfter(date)) {
            return false;
        }
        return expiresAt == null || !expiresAt.isBefore(date);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false;
        return id != null && id.equals(((License) other).getId());
    }

    @Override
    public int hashCode() {
        return Hibernate.getClass(this).hashCode();
    }
}
