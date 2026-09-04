package uz.backend.common.dto.dormitory;

import jakarta.validation.constraints.NotNull;
import uz.backend.common.enums.ResponsibilityType;

/**
 * Запрос на привязку ответственного
 *
 * @param managerId идентификатор менеджера
 * @param role      роль
 * @author Aleksandr Yagudin
 */
public record ResponsibleCreateRequestDto(@NotNull Long managerId,
                                          @NotNull ResponsibilityType role) {
}
