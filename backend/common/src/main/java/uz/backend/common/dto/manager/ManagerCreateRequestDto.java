package uz.backend.common.dto.manager;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Запрос на создание менеджера
 *
 * @param fullName ФИО менеджера
 * @param phone    номер телефона
 * @param jobTitle должность
 * @author Aleksandr Yagudin
 */
public record ManagerCreateRequestDto(@NotBlank @Size(max = 255) String fullName,
                                      @Size(max = 30) String phone,
                                      @Size(max = 64) String jobTitle) {
}
