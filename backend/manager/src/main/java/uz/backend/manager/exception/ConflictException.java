package uz.backend.manager.exception;

/**
 * Операция противоречит текущему состоянию ресурса
 *
 * @author Aleksandr Yagudin
 */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
