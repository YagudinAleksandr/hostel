package uz.backend.database.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.backend.common.enums.LicenseType;

import java.time.LocalDate;
import java.util.*;

/**
 * Агрегат менеджера
 *
 * @author Aleksandr Yagudin
 */
@Getter
@Setter
@Entity
@Table(name = "manager")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Manager implements EntityBase<Long> {
    /**
     * Идентификатор
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ФИО менеджера
     */
    @Column(name = "full_name", nullable = false)
    private String fullName;

    /**
     * Должность
     */
    @Column(name = "job_title", nullable = false)
    private String jobTitle;

    /**
     * Версия
     */
    @Version
    @Column(name = "version", nullable = false)
    private long version;

    @Column(name = "phone")
    private String phone;

    /**
     * Список лицензий
     */
    @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<License> licenses = new LinkedHashSet<>();

    public Manager(String fullName, String phone, String jobTitle) {
        this.fullName = requireFullName(fullName);
        this.phone = phone;
        this.jobTitle = jobTitle;
    }

    /**
     * Выдать лицензию
     *
     * @param type      тип лицензии
     * @param number    номер лицензии
     * @param issuedAt  дата выдачи
     * @param expiresAt дата окончания, {@code null} — бессрочная
     * @return выданная лицензия
     */
    public License issueLicense(LicenseType type, String number, LocalDate issuedAt, LocalDate expiresAt) {
        Objects.requireNonNull(type, "Тип лицензии обязателен");
        Objects.requireNonNull(issuedAt, "Дата выдачи обязательна");
        if (expiresAt != null && expiresAt.isBefore(issuedAt)) {
            throw new IllegalArgumentException("Дата окончания раньше даты выдачи");
        }
        if (activeLicense(type).isPresent()) {
            throw new IllegalStateException("Действующая лицензия этого типа уже есть");
        }

        License license = new License(this, type, number, issuedAt, expiresAt);
        licenses.add(license);
        return license;
    }

    /**
     * Отозвать лицензию
     *
     * @param number номер лицензии
     */
    public void revokeLicense(String number) {
        License license = licenses.stream()
                .filter(l -> l.getNumber().equals(number))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Лицензия " + number + " не найдена"));

        if (license.getRevokedAt() != null) {
            throw new IllegalStateException("Лицензия уже отозвана");
        }
        license.setRevokedAt(LocalDate.now());
    }

    /**
     * Найти действующую лицензию нужного типа
     *
     * @param type тип лицензии
     * @return лицензия, если она есть и действует сегодня
     */
    public Optional<License> activeLicense(LicenseType type) {
        LocalDate today = LocalDate.now();
        return licenses.stream()
                .filter(l -> l.getType() == type && l.isActiveOn(today))
                .findFirst();
    }

    /**
     * Сменить ФИО
     *
     * @param fullName новое ФИО
     */
    public void rename(String fullName) {
        this.fullName = requireFullName(fullName);
    }

    /**
     * Смена места работы
     *
     * @param jobTitle новое название места работы
     */
    public void changeJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    /**
     * Получение списка лицензий
     *
     * @return список лицензий {@link License}
     */
    public Set<License> getLicenses() {
        return Collections.unmodifiableSet(licenses);
    }

    /**
     * Проверка заполненности ФИО
     *
     * @param value значение
     * @return ФИО
     */
    private static String requireFullName(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ФИО менеджера обязательно");
        }
        return value;
    }
}
