package ru.maliutin.tasklist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.maliutin.tasklist.domain.task.Task;

import java.sql.Timestamp;
import java.util.List;

/**
 * Интерфейс для запросов к БД сущности Task.
 */
public interface TaskRepository extends JpaRepository<Task, Long> {


    /**
     * Получение всех задач для конкретного пользователя.
     *
     * @param userId идентификатор пользователя.
     * @return лист задач.
     */
    @Query(value = """
            SELECT * FROM tasks t
            JOIN users_tasks ut ON ut.task_id = t.id
            WHERE ut.user_id = :userId
            """, nativeQuery = true)
    List<Task> findAllByUserId(@Param("userId") long userId);

    @Modifying
    @Query(value = """
            INSERT INTO users_tasks (user_id, task_id)
            VALUES (:userId, :taskId)
            """, nativeQuery = true)
    void assignTask(@Param("userId") Long userId, @Param("taskId") Long taskId);

    @Modifying
    @Query(value = """
            INSERT INTO tasks_images (task_id, image)
            VALUES (:id, :fileName)
            """, nativeQuery = true)
    void addImage(@Param("id") Long id, @Param("fileName") String fileName);

    @Modifying
    @Query(value = """
            SELECT * FROM tasks t
            where T.expiration_date IS NOT NULL
            AND t.expiration_date BETWEEN :start AND :end
            """, nativeQuery = true)
    List<Task> findAllSoonTasks(@Param("start") Timestamp start,
                                @Param("end") Timestamp end);
}
