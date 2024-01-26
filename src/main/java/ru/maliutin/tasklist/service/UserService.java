package ru.maliutin.tasklist.service;

import ru.maliutin.tasklist.domain.user.User;

/**
 * Класс содержащий бизнес-логику программы.
 * Осуществляет запросы к репозиторию и взаимодействующий с моделью User.
 */
public interface UserService {
    /**
     * Получение пользователя по его id.
     *
     * @param id идентификатор пользователя.
     * @return объект пользователя или
     * генерирует исключение в случае его отсутствия.
     */
    User getById(long id);

    /**
     * Получение пользователя по username (логину).
     *
     * @param username логин пользователя.
     * @return объект пользователя или
     * генерирует исключение в случае его отсутствия.
     */
    User getByUsername(String username);

    /**
     * Обновление информации о пользователе.
     *
     * @param user объект пользователя.
     * @return обновленный объект пользователя.
     */
    User update(User user);

    /**
     * Создание нового пользователя.
     *
     * @param user объект пользователя.
     * @return созданный объект.
     */
    User create(User user);

    /**
     * Проверка принадлежит ли задача пользователю.
     *
     * @param userId идентификатор пользователя.
     * @param taskId идентификатор задачи.
     * @return true - если задача принадлежит пользователю, иначе - false.
     */
    boolean isTaskOwner(Long userId, long taskId);

    /**
     * Удаление пользователя.
     *
     * @param id идентификатор пользователя.
     */
    void delete(long id);

    User getTaskAuthor(Long taskId);

}
