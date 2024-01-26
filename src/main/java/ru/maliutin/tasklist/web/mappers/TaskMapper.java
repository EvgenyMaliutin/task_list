package ru.maliutin.tasklist.web.mappers;

import org.mapstruct.Mapper;
import ru.maliutin.tasklist.domain.task.Task;
import ru.maliutin.tasklist.web.dto.task.TaskDto;

/**
 * Интерфейс реализующий методы преобразования объектов Task.
 */
@Mapper(componentModel = "spring")
public interface TaskMapper extends Mappable<Task, TaskDto> {

}
