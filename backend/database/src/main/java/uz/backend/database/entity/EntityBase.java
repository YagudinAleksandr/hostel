package uz.backend.database.entity;

/**
 * Базовая сущность
 *
 * @param <T> тип идентификатора
 * @author Aleksandr Yagudin
 */
public interface EntityBase<T> {
    void setId(T id);

    T getId();
}
