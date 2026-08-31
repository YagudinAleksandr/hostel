package uz.backend.common.dto.dormitory;

import java.util.List;

/**
 * Подъезд
 *
 * @param id     идентификатор
 * @param name   название
 * @param floors этажи
 * @author Aleksandr Yagudin
 */
public record EntranceResponseDto(Long id, String name, List<FloorResponseDto> floors) {
}
