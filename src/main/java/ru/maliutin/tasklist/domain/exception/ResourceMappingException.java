package ru.maliutin.tasklist.domain.exception;

/**
 * Исключение выбрасывается когда в JDBC
 * происходят ошибки при получении объектов из БД.
 */
public class ResourceMappingException extends RuntimeException {
    /**
     * Конструктор класса исключения,
     * использует родительский конструктор RuntimeException.
     *
     * @param message сообщение для пользователя.
     */
    public ResourceMappingException(final String message) {
        super(message);
    }
}
