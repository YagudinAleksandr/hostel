package uz.backend.manager.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uz.backend.common.enums.Country;
import uz.backend.manager.entity.base.AuditableEntity;
import uz.backend.manager.entity.base.BaseEntity;

/**
 * Сущность адреса
 *
 * @author Aleksandr Yagudin
 */
@Getter
@Setter
@Entity
@Table(name = "address")
public class Address extends AuditableEntity implements BaseEntity<Long> {
    /**
     * Идентификатор
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Индекс
     */
    @Column(name = "zipCode")
    private String zipCode;

    /**
     * Страна
     */
    @Column(name = "country", nullable = false)
    @Enumerated(EnumType.STRING)
    private Country country;

    /**
     * Адрес
     */
    @Column(name = "address", nullable = false)
    private String address;
}
