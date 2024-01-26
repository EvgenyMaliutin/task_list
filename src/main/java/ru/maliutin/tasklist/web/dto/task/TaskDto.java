package ru.maliutin.tasklist.web.dto.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;
import ru.maliutin.tasklist.domain.task.Status;
import ru.maliutin.tasklist.web.dto.validation.OnCreate;
import ru.maliutin.tasklist.web.dto.validation.OnUpdate;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Класс служащий макетом
 * для преобразования объектов модели
 * Task в TaskDTO и обратно.
 */
// Автоматически создает геттеры,
// сеттеры, конструкторы, hashcode, equals;
@Data
public class TaskDto {
    /**
     * Id задачи.
     */
    /*
        Аннотация валидации - проверяющая, что поле не пустое.
        groups - указывает для каких групп применяется аннотация.
        Если создается новая задача, соответственно у нее еще нет id,
        поэтому проверка проводится только для зарегистрированных задач,
        для их определения используется маркерный интерфейс OnUpdate.
     */
    @NotNull(message = "id не может быть пустым!",
            groups = OnUpdate.class)
    // Аннотация валидации - проверяющая, что поле не пустое.
    private long id;
    /**
     * Заголовок задачи.
     */
    @NotNull(message = "Заголовок не может быть пустым!",
            groups = {OnUpdate.class, OnCreate.class})
    // Проверка длины получаемого строкового значения.
    @Length(max = 255,
            message = "Заголовок не может быть длиннее 255 символов!",
            groups = {OnUpdate.class, OnCreate.class})
    private String title;
    /**
     * Описание задачи.
     */
    @Length(max = 255,
            message = "Описание не может быть длиннее 255 символов!",
            groups = {OnUpdate.class, OnCreate.class})
    private String description;
    /**
     * Статус задачи (TODO, IN_PROGRESS, DONE).
     */
    private Status status;
    /**
     * Время до которого должна быть выполнена задача.
     */
    /*
        Аннотация позволяющая задать формат даты и времени
        при преобразовании объектов.
        iso - указывает на определенный стандарт даты и времени.
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    /*
        Аннотация задающая формат даты и времени при преобразовании объектов.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime expirationDate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<String> images;
}
