package ru.maliutin.tasklist.service;


import ru.maliutin.tasklist.domain.task.TaskImage;

public interface ImageService {

    String upload(TaskImage image);

}
