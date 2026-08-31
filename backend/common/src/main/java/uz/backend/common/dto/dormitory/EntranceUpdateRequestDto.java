package uz.backend.common.dto.dormitory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Запрос на обновление подъезда
 *
 * @param name название подъезда
 * @author Aleksandr Yagudin
 */
public record EntranceUpdateRequestDto(
        @NotBlank(message = "Название обязательно")
        @Size(max = 30, message = "Название не может быть более 30 символов")
        String name) {
}
