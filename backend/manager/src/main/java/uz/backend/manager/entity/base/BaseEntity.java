package uz.backend.manager.entity.base;

/**
 * Базовая сущность
 *
 * @param <T> тип ключа идентификатора
 * @author Aleksandr Yagudin
 */
public interface BaseEntity<T> {
    T getId();

    void setId(T id);
}
