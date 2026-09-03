package uz.backend.manager.exception;

/**
 * Запрошенный ресурс не найден
 *
 * @author Aleksandr Yagudin
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
