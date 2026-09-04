package uz.backend.common.dto.manager;

/**
 * Краткая информация по менеджеру
 *
 * @param id       идентификатор
 * @param fullName ФИО
 * @param phone    номер телефона
 * @author Aleksandr Yagudin
 */
public record ManagerSummaryDto(Long id, String fullName, String phone) {
}
