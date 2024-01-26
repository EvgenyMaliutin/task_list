package ru.maliutin.tasklist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.maliutin.tasklist.domain.user.User;

import java.util.Optional;

/**
 * Интерфейс для запросов к БД сущности User.
 */
public interface UserRepository extends JpaRepository<User, Long> {


    /**
     * Поиск пользователя по username (логину).
     *
     * @param username логин пользователя.
     * @return объект Optional,
     * который как может содержать пользователя, так и нет.
     */
    Optional<User> findByUsername(String username);

    /**
     * Проверка является ли пользователь владельцем задачи.
     *
     * @param userId идентификатор пользователя.
     * @param taskId идентификатор задачи.
     * @return true - пользователь является владельцем, иначе - false.
     */
    @Query(value = """
            SELECT exists(SELECT 1
                          FROM users_tasks
                          WHERE user_id = :userId
                            AND task_id = :taskId)
            """, nativeQuery = true)
    boolean isTaskOwner(
            @Param("userId") long userId, @Param("taskId") long taskId);

    @Query(value = """
            SELECT u.id as id,
            u.name as name,
            u.username as username,
            u.password as password
            FROM users_tasks ut
            JOIN users u ON ut.user_id = u.id
            WHERE ut.task_id = :taskId
            """, nativeQuery = true)
    Optional<User> findTaskAuthor(@Param("taskId") Long taskId);
}
