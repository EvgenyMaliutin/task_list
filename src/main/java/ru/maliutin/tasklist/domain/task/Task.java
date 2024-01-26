package ru.maliutin.tasklist.domain.task;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Column;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Класс модели задачи пользователя.
 */
@Entity
@Table(name = "tasks")
// Автоматически создает геттеры,
// сеттеры, конструкторы, hashcode, equals;
@Data
public class Task implements Serializable {
    /**
     * Id задачи.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    /**
     * Заголовок задачи.
     */
    private String title;
    /**
     * Описание задачи.
     */
    private String description;
    /**
     * Статус задачи (TODO, IN_PROGRESS, DONE).
     */
    @Enumerated(value = EnumType.STRING)
    private Status status;
    /**
     * Время до которого должна быть выполнена задача.
     */
    private LocalDateTime expirationDate;

    @Column(name = "image")
    @CollectionTable(name = "tasks_images")
    @ElementCollection
    private List<String> images;

}
