package uz.backend.common.dto.dormitory;

import uz.backend.common.dto.AddressDto;

import java.util.List;

/**
 * Общежитие
 *
 * @param id        идентификатор
 * @param name      название
 * @param address   адрес
 * @param entrances подъезды
 * @param version   версия агрегата, нужна при обновлении
 * @author Aleksandr Yagudin
 */
public record DormitoryResponseDto(
        Long id,
        String name,
        AddressDto address,
        List<EntranceResponseDto> entrances,
        Long version
) {
}
