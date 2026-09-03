package uz.backend.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import uz.backend.common.enums.Countries;

/**
 * Адрес
 *
 * @param zip       почтовый индекс
 * @param country   страна {@link Countries}
 * @param region    регион/область
 * @param city      город/населенный пункт
 * @param street    улица
 * @param house     строение/дом
 * @param apartment квартира
 * @author Aleksandr Yagudin
 */
public record AddressDto(
        @Size(max = 20)
        String zip,

        @NotNull(message = "Страна обязательна")
        Countries country,

        @Size(max = 100)
        String region,

        @NotBlank(message = "Город/населенный пункт обязателен")
        @Size(max = 255)
        String city,

        @NotBlank(message = "Улица обязательна")
        @Size(max = 255)
        String street,

        @NotBlank(message = "Дом/строение обязательно")
        @Size(max = 30)
        String house,

        @Size(max = 30)
        String apartment) {
}
