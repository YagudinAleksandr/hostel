package uz.backend.common.dto.dormitory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Запрос на создание этажа
 *
 * @param name название
 * @author Aleksandr Yagudin
 */
public record FloorCreateRequestDto(
        @NotBlank(message = "Название обязательно")
        @Size(max = 30)
        String name) {
}
