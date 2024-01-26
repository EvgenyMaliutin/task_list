package ru.maliutin.tasklist.domain.exception;

/**
 * Исключение выбрасывается при отсутствии достаточных прав у пользователя.
 * Например, при попытке получить пользователем доступ не к своим задачам.
 */
public class AccessDeniedException extends RuntimeException {
    /**
     * Конструктор исключения.
     * Вызывает родительский конструктор класса RuntimeException.
     */
    public AccessDeniedException() {
        super();
    }

}
