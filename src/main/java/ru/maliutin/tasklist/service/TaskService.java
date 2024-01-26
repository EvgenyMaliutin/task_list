package ru.maliutin.tasklist.service;

import ru.maliutin.tasklist.domain.task.Task;
import ru.maliutin.tasklist.domain.task.TaskImage;

import java.time.Duration;
import java.util.List;

/**
 * Интерфейс сервиса для работы с репозиторием объектов задач.
 */
public interface TaskService {
    /**
     * Получение задачи по id.
     *
     * @param id идентификатор задачи.
     * @return объект задачи.
     */
    Task getById(long id);

    /**
     * Получение списка всех задач для конкретного пользователя.
     *
     * @param userId идентификатор пользователя.
     * @return лист задач.
     */
    List<Task> getAllByUserId(long userId);

    /**
     * Обновление задачи.
     *
     * @param task объект задачи.
     * @return обновленный объект задачи.
     */
    Task update(Task task);

    /**
     * Создание задачи.
     *
     * @param task   объект задачи.
     * @param userId идентификатор пользователя, которому принадлежит задача.
     * @return Созданный объект задачи.
     */
    Task create(Task task, long userId);

    /**
     * Удаление задачи.
     *
     * @param id идентификатор задачи.
     */
    void delete(long id);

    void uploadImage(Long taskId, TaskImage taskImage);

    List<Task> getAllSoonTasks(Duration duration);
}
