package ru.maliutin.tasklist.web.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.maliutin.tasklist.web.dto.validation.OnCreate;
import ru.maliutin.tasklist.web.dto.validation.OnUpdate;

/**
 * Класс служащий макетом для преобразования
 * объектов модели User в UserDTO и обратно.
 */
// Автоматически создает геттеры,
// сеттеры, конструкторы, hashcode, equals;
@Data
// Аннотация Swagger добавляющая описание схемы в документации
@Schema(description = "User Dto")
public class UserDto {

    /**
     * Id пользователя.
     */
    /*
        Аннотация валидации - проверяющая, что поле не пустое.
        groups - указывает для каких групп применяется аннотация.
        Если создается новый пользователь, соответственно у него еще нет id,
        поэтому проверка проводится только для зарегистрированных пользователей,
        для их определения используется маркерный интерфейс OnUpdate.
     */
    @NotNull(message = "Id не может быть пустым!", groups = OnUpdate.class)
    // Аннотация Swagger добавляющая описание параметра в документации
    @Schema(description = "User id", example = "1")
    private long id;

    /**
     * Имя пользователя.
     */
    @NotNull(message = "Имя не может быть пустым!",
            groups = {OnUpdate.class, OnCreate.class})
    // Проверка длины получаемого строкового значения.
    @Length(max = 255,
            message = "Имя должно быть менее 255 символов!",
            groups = {OnUpdate.class, OnCreate.class})
    // Аннотация Swagger добавляющая описание параметра в документации
    @Schema(description = "User name", example = "John Doe")
    private String name;

    /**
     * Логин пользователя (email).
     */
    @NotNull(message = "Логин не может быть пустым!",
            groups = {OnUpdate.class, OnCreate.class})
    // Проверка длины получаемого строкового значения.
    @Length(max = 255,
            message = "Логин должен быть менее 255 символов!",
            groups = {OnUpdate.class, OnCreate.class})
    // Аннотация Swagger добавляющая описание параметра в документации
    @Schema(description = "User email",
            example = "johndoe@gmail.com")
    private String username;

    /**
     * Пароль пользователя.
     */
    /*
        Аннотация обозначающая, что пароль пользователя
        может быть только принят, но не отправлен при запросе.
        Т.е. при получении объекта данных и конвертации
        его в объект модели, пароль будет получен и загружен в объект модели.
        При преобразовании объекта модели в объект данных,
        пароль не будет заполняться,
        т.к. это не безопасно и не нужно пользователю.
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull(message = "Пароль не может быть пустым!",
            groups = {OnUpdate.class, OnCreate.class})
    // Аннотация Swagger добавляющая описание параметра в документации
    @Schema(description = "User crypted password",
            example =
                "$2a$10$eaL9RAsJeY95hERA/D6iUOMLfDzt7FcIqcf39ytbShEioVYm0KGLq")
    private String password;
    /**
     * Подтверждение пароля пользователя.
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull(message = "Подтверждение пароля не может быть пустым!",
            groups = {OnCreate.class})
    // Аннотация Swagger добавляющая описание параметра в документации
    @Schema(description = "User password confirmation",
            example =
                "$2a$10$eaL9RAsJeY95hERA/D6iUOMLfDzt7FcIqcf39ytbShEioVYm0KGLq")
    private String passwordConfirmation;


}
