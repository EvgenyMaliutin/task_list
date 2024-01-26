package ru.maliutin.tasklist.domain.exception;

/**
 * Исключение выбрасывается в случае отсутствия записи в БД.
 * Unchecked исключение.
 */
public class ResourceNotFoundException extends RuntimeException {
    /**
     * Конструктор класса исключения,
     * использует родительский конструктор RuntimeException.
     *
     * @param message сообщение для пользователя.
     */
    public ResourceNotFoundException(final String message) {
        super(message);
    }
}
