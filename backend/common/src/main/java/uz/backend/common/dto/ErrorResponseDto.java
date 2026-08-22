package uz.backend.common.dto;

import java.util.List;

/**
 * Ответ с ошибкой
 *
 * @param errorMessages список ошибок
 * @author Aleksandr Yagudin
 */
public record ErrorResponseDto(List<String> errorMessages) {
}
