package uz.backend.common.dto.dormitory;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import uz.backend.common.dto.AddressDto;

/**
 * Запрос на обновление общежития
 *
 * @param name    название
 * @param address адрес {@link AddressDto}
 * @param version версия агрегата, полученная при чтении
 * @author Aleksandr Yagudin
 */
public record DormitoryUpdateRequestDto(
        @NotBlank(message = "Название обязательно")
        @Size(max = 255, message = "Название не может быть более 255 символов")
        String name,

        @NotNull(message = "Адрес обязателен")
        @Valid
        AddressDto address,

        @NotNull(message = "Версия обязательна")
        Long version
) {
}
