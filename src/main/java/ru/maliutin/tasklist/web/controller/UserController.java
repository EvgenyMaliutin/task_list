package ru.maliutin.tasklist.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.maliutin.tasklist.domain.task.Task;
import ru.maliutin.tasklist.domain.user.User;
import ru.maliutin.tasklist.service.TaskService;
import ru.maliutin.tasklist.service.UserService;
import ru.maliutin.tasklist.web.dto.task.TaskDto;
import ru.maliutin.tasklist.web.dto.user.UserDto;
import ru.maliutin.tasklist.web.dto.validation.OnCreate;
import ru.maliutin.tasklist.web.dto.validation.OnUpdate;
import ru.maliutin.tasklist.web.mappers.TaskMapper;
import ru.maliutin.tasklist.web.mappers.UserMapper;

import java.util.List;

/**
 * Класс контроллера обрабатывающий запросы к пользователям (User).
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "User Controller", description = "User API")
// Аннотация Swagger добавляющая в документацию название и описание контроллера.

public class UserController {
    /**
     * Поле интерфейса сервиса объектов пользователя (User).
     */
    private final UserService userService;
    /**
     * Поле интерфейса сервиса объектов задач (Task).
     */
    private final TaskService taskService;

    /**
     * Поле маппера объектов пользователя (User).
     */
    private final UserMapper userMapper;
    /**
     * Поле маппера объектов задач (Task).
     */
    private final TaskMapper taskMapper;

    /**
     * Обновление пользователя.
     *
     * @param userDto пользователь для обновления.
     * @return обновленный пользователь.
     */
    @PutMapping
    // Аннотация graphql - TODO добавить описание, так же для @Argument
    @MutationMapping(name = "updateUser")
    // Аннотация Swagger добавляющая описание метода в документацию.
    @Operation(summary = "Update user")
    // Аннотация проверяющая имеет ли аутентифицированный
    // пользователь доступ к методу
    @PreAuthorize("@customSecurityExpression.canAccessUser(#userDto.id)")
    public UserDto update(
            @Validated(OnUpdate.class)
            @RequestBody @Argument final UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        User updatedUser = userService.update(user);
        return userMapper.toDto(updatedUser);
    }

    /**
     * Получение пользователя по id.
     *
     * @param id идентификатор пользователя.
     * @return найденный пользователь.
     */
    @GetMapping("/{id}")
    // Аннотация graphql - TODO добавить описание, так же для @Argument
    @QueryMapping(name = "userById")
    // Аннотация Swagger добавляющая описание метода в документацию.
    @Operation(summary = "Get UserDTO by id")
    // Аннотация проверяющая имеет ли аутентифицированный
    // пользователь доступ к методу
    @PreAuthorize("@customSecurityExpression.canAccessUser(#id)")
    public UserDto getById(@PathVariable @Argument final Long id) {
        User user = userService.getById(id);
        return userMapper.toDto(user);
    }

    /**
     * Удаление пользователя.
     *
     * @param id идентификатор пользователя.
     */
    @DeleteMapping("/{id}")
    // Аннотация graphql - TODO добавить описание, так же для @Argument
    @MutationMapping(name = "deleteUserById")
    // Аннотация Swagger добавляющая описание метода в документацию.
    @Operation(summary = "Delete user by id")
    // Аннотация проверяющая имеет ли аутентифицированный
    // пользователь доступ к методу
    @PreAuthorize("@customSecurityExpression.canAccessUser(#id)")
    public void deleteById(@PathVariable @Argument final Long id) {
        userService.delete(id);
    }

    /**
     * Получение всех задач конкретного пользователя.
     *
     * @param id идентификатор пользователя.
     * @return список задач пользователя.
     */
    @GetMapping("/{id}/tasks")
    // Аннотация graphql - TODO добавить описание, так же для @Argument
    @QueryMapping(name = "tasksByUserId")
    // Аннотация Swagger добавляющая описание метода в документацию.
    @Operation(summary = "Get all user tasks by user id")
    // Аннотация проверяющая имеет ли аутентифицированный
    // пользователь доступ к методу
    @PreAuthorize("@customSecurityExpression.canAccessUser(#id)")
    public List<TaskDto> getTasksByUserId(
            @PathVariable @Argument final Long id) {
        List<Task> tasks = taskService.getAllByUserId(id);
        return taskMapper.toDto(tasks);
    }

    /**
     * Создание новой задачи.
     *
     * @param id      идентификатор пользователя, которому принадлежит задача.
     * @param taskDto новая задача.
     * @return созданную задачу.
     */
    @PostMapping("/{id}/tasks")
    // Аннотация graphql - TODO добавить описание, так же для @Argument
    @MutationMapping(name = "createTask")
    // Аннотация Swagger добавляющая описание метода в документацию.
    @Operation(summary = "Add task to user")
    // Аннотация проверяющая имеет ли аутентифицированный
    // пользователь доступ к методу
    @PreAuthorize("@customSecurityExpression.canAccessUser(#id)")
    public TaskDto createTask(
            @PathVariable @Argument final Long id,
            @Validated(OnCreate.class)
            @RequestBody @Argument(name = "dto") final TaskDto taskDto) {
        System.out.println("В контроллере создания задачи");
        Task newTask = taskMapper.toEntity(taskDto);
        System.out.println("В контроллере перед сохранением задачи: " + newTask.getTitle() + "время: " + newTask.getExpirationDate());
        Task createdTask = taskService.create(newTask, id);
        System.out.println("В контроллере сохранили задачу: " + createdTask.getTitle());
        return taskMapper.toDto(createdTask);
    }
}
