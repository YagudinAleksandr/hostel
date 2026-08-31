package uz.backend.common.dto.dormitory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Запрос на создание подъезда
 *
 * @param name название подъезда
 * @author Aleksandr Yagudin
 */
public record EntranceCreateRequestDto(
        @NotBlank(message = "Название обязательно")
        @Size(max = 30)
        String name) {
}
