package uz.backend.common.dto.manager;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import uz.backend.common.enums.LicenseType;

import java.time.LocalDate;

/**
 * Создание лицензии
 *
 * @param type      тип лицензии
 * @param number    номер лицензии
 * @param issuedAt  дата назначения
 * @param expiresAt дата истечения
 * @author Aleksandr Yagudin
 */
public record LicenseCreateRequestDto(@NotNull LicenseType type,
                                      @NotBlank @Size(max = 50) String number,
                                      @NotNull LocalDate issuedAt,
                                      LocalDate expiresAt) {
}
