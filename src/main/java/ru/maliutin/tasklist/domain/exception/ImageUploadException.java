package ru.maliutin.tasklist.domain.exception;

/**
 * Исключение выбрасывается при проблемах загрузки изображения.
 */
public class ImageUploadException extends RuntimeException {
    /**
     * Конструктор исключения.
     * Вызывает родительский конструктор класса RuntimeException.
     * @param message сообщение об ошибке.
     */
    public ImageUploadException(final String message) {
        super(message);
    }

}
