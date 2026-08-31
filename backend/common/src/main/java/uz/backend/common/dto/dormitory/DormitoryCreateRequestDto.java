package uz.backend.common.dto.dormitory;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import uz.backend.common.dto.AddressDto;

/**
 * Общежитие
 *
 * @param name    название
 * @param address адрес {@link AddressDto}
 * @author Aleksandr Yagudin
 */

public record DormitoryCreateRequestDto(
        @NotBlank(message = "Название обязательно")
        @Size(max = 255, message = "Название не может быть более 255 символов")
        String name,

        @NotNull(message = "Адрес обязателен")
        @Valid
        AddressDto address
) {

}
