package uz.backend.common.dto.dormitory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Запрос на обновление этажа
 *
 * @param name название этажа
 * @author Aleksandr Yagudin
 */
public record FloorUpdateRequestDto(
        @NotBlank(message = "Название обязательно")
        @Size(max = 30, message = "Название не может быть более 30 символов")
        String name) {
}
