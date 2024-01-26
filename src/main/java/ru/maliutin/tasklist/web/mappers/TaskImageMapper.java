package ru.maliutin.tasklist.web.mappers;

import org.mapstruct.Mapper;
import ru.maliutin.tasklist.domain.task.TaskImage;
import ru.maliutin.tasklist.web.dto.task.TaskImageDto;

/**
 * Интерфейс реализующий методы преобразования объектов Task.
 */
@Mapper(componentModel = "spring")
public interface TaskImageMapper extends Mappable<TaskImage, TaskImageDto> {

}
