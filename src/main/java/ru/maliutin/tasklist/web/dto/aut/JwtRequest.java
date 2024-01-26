package ru.maliutin.tasklist.web.dto.aut;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Класс авторизации пользователя.
 */
@Data
// Аннотация Swagger добавляющая описание схемы в документации
@Schema(description = "Request for login")
public class JwtRequest {
    /**
     * Поле с логином пользователя.
     */
    // Аннотация валидации - проверяющая, что поле не пустое.
    @NotNull(message = "Логин не может быть пусты!")
    @Schema(description = "email", example = "johndoe@gmail.com")
    private String username;

    /**
     * Поле с паролем пользователя.
     */
    @NotNull(message = "Пароль не может быть пустым!")
    @Schema(description = "password", example = "12345")
    private String password;
}
