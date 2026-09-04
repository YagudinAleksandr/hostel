package uz.backend.common.dto.dormitory;

import uz.backend.common.enums.LicenseType;
import uz.backend.common.enums.ResponsibilityType;

import java.time.LocalDateTime;

/**
 * Получение ответственного
 *
 * @param id            идентификатор
 * @param managerId     идентификатор менеджера
 * @param licenseType   тип лицензии
 * @param licenseNumber номер лицензии
 * @param role          роль
 * @param assignedAt    дата назначения
 * @author Aleksandr Yagudin
 */
public record ResponsibleResponseDto(Long id, Long managerId, LicenseType licenseType,
                                     String licenseNumber, ResponsibilityType role, LocalDateTime assignedAt) {
}
