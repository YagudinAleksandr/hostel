package uz.backend.common.dto.manager;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Запрос на обновление менеджера
 *
 * @param fullName ФИО менеджера
 * @param phone    телефон
 * @param jobTitle должность
 * @param version  версия
 * @author Aleksandr Yagudin
 */
public record ManagerUpdateRequestDto(@NotBlank @Size(max = 255) String fullName,
                                      @Size(max = 30) String phone,
                                      @Size(max = 64) String jobTitle,
                                      @NotNull Long version) {
}
