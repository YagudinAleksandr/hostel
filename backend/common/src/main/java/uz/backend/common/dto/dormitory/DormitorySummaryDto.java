package uz.backend.common.dto.dormitory;

import uz.backend.common.dto.AddressDto;

/**
 * Краткое представление общежития для списков
 *
 * @param id      идентификатор
 * @param name    название
 * @param address адрес
 * @author Aleksandr Yagudin
 */
public record DormitorySummaryDto(Long id, String name, AddressDto address) {
}
