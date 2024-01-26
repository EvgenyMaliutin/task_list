package ru.maliutin.tasklist.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

/**
 * Класс описывающий строение собственного исключения,
 * которое содержит информацию для фронта
 * о сгенерированных исключениях на сервере.
 * (Единое, унифицированное исключение для пользователя)
 */
@Data
@AllArgsConstructor
public class ExceptionBody {
    // Сообщение о сгенерированном исключении
    private String message;
    // Коллекция полей классов и ошибок в этих полях при валидации.
    private Map<String, String> errors;

    public ExceptionBody(final String message) {
        this.message = message;
    }
}
