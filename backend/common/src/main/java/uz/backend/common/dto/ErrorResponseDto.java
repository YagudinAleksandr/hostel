package uz.backend.common.dto;

import java.util.List;

/**
 * Ответ с описанием ошибки
 *
 * @author Aleksandr Yagudin
 */
public record ErrorResponseDto(List<String> errorMessages) {
}
