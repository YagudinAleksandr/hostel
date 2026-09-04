package uz.backend.common.dto.manager;

import java.util.List;

/**
 * Менеджер
 *
 * @param id       идентификатор
 * @param fullName ФИО
 * @param phone    номер телефона
 * @param jobTitle должность
 * @param licenses лицензия
 * @param version  версия
 * @author Aleksandr Yagudin
 */
public record ManagerResponseDto(Long id, String fullName, String phone, String jobTitle,
                                 List<LicenseResponseDto> licenses, Long version) {
}
