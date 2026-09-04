package uz.backend.common.dto.manager;

import uz.backend.common.enums.LicenseType;

import java.time.LocalDate;

/**
 * Лицензия
 *
 * @param id        идентификатор
 * @param type      тип
 * @param number    номер
 * @param issuedAt  дата назначения
 * @param expiresAt дата истечения
 * @param revokedAt дата отзыва
 * @author Aleksandr Yagudin
 */
public record LicenseResponseDto(Long id, LicenseType type, String number,
                                 LocalDate issuedAt, LocalDate expiresAt, LocalDate revokedAt) {
}
