package ru.maliutin.tasklist.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import ru.maliutin.tasklist.config.TestConfig;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.maliutin.tasklist.domain.exception.ResourceNotFoundException;
import ru.maliutin.tasklist.domain.task.Status;
import ru.maliutin.tasklist.domain.task.Task;
import ru.maliutin.tasklist.domain.task.TaskImage;
import ru.maliutin.tasklist.repository.TaskRepository;
import ru.maliutin.tasklist.repository.UserRepository;
import ru.maliutin.tasklist.service.ImageService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@Import(TestConfig.class)
@ExtendWith(MockitoExtension.class)
public class TaskServiceImplTest {

    @MockBean
    private TaskRepository taskRepository;

    @MockBean
    private ImageService imageService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Autowired
    private TaskServiceImpl taskService;

    @Test
    void getById(){
        Long id = 1L;
        Task task = new Task();
        task.setId(id);
        Mockito.when(taskRepository.findById(id))
                .thenReturn(Optional.of(task));
        Task testTask = taskService.getById(id);
        Mockito.verify(taskRepository).findById(id);
        Assertions.assertEquals(task, testTask);
    }

    @Test
    void getByIdWithNotExisting(){
        Long id = 1L;
        Mockito.when(taskRepository.findById(id))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> taskService.getById(id));
        Mockito.verify(taskRepository).findById(id);
    }

    @Test
    void getAllByUserId(){
        Long userId = 1L;
        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            tasks.add(new Task());
        }
        Mockito.when(taskRepository.findAllByUserId(userId))
                .thenReturn(tasks);
        List<Task> testTasks = taskService.getAllByUserId(userId);
        Mockito.verify(taskRepository).findAllByUserId(userId);
        Assertions.assertEquals(tasks, testTasks);
    }

    @Test
    void update(){
        Task task = new Task();
        task.setTitle("test");
        task.setStatus(Status.TODO);
        Mockito.when(taskRepository.save(task)).thenReturn(task);
        Task testTask = taskService.update(task);
        Mockito.verify(taskRepository).save(task);
        Assertions.assertEquals(task, testTask);
    }

    @Test
    void updateWithNullStatus(){
        Task task = new Task();
        task.setTitle("test");
        task.setStatus(Status.TODO);
        Mockito.when(taskRepository.save(task)).thenReturn(task);
        Task testTask = new Task();
        testTask.setTitle("test");
        testTask.setStatus(null);
        testTask = taskService.update(testTask);
        Mockito.verify(taskRepository).save(task);
        Assertions.assertEquals(Status.TODO, testTask.getStatus());
    }

    @Test
    void create(){
        Long userId = 1L;
        Long taskId = 1L;
        Task task = new Task();
        /*
            Поведение объекта репозитория:
            Когда у репозитория будет вызван метод save(task)
            Создастся новый объект taskSaved которому в первый аргумент,
            т.е. в id будет присвоено значение taskId - 1, и будет
            возвращен данный объект (имитация сохр. в БД с присваиванием id)
         */
        Mockito.doAnswer(invocationOnMock -> {
            Task savedTask = invocationOnMock.getArgument(0);
            savedTask.setId(taskId);
            return savedTask;
        }).when(taskRepository).save(task);
        Task testTask = taskService.create(task, userId);
        Mockito.verify(taskRepository).save(task);
        Assertions.assertNotNull(testTask.getId());
        Mockito.verify(taskRepository).assignTask(userId, task.getId());
    }

    @Test
    void createChangeStatus(){
        Long userId = 1L;
        Long taskId = 1L;
        Task task = new Task();
        task.setStatus(Status.IN_PROGRESS);
        /*
            Поведение объекта репозитория:
            Когда у репозитория будет вызван метод save(task)
            Создастся новый объект taskSaved которому в первый аргумент,
            т.е. в id будет присвоено значение taskId - 1, и будет
            возвращен данный объект (имитация сохр. в БД с присваиванием id)
         */
        Mockito.doAnswer(invocationOnMock -> {
            Task savedTask = invocationOnMock.getArgument(0);
            savedTask.setId(taskId);
            savedTask.setStatus(task.getStatus());
            return savedTask;
        }).when(taskRepository).save(task);
        Task testTask = taskService.create(task, userId);
        Mockito.verify(taskRepository).save(task);
        Assertions.assertEquals(Status.TODO, testTask.getStatus());
    }

    @Test
    void delete(){
        Long taskId = 1L;
        taskService.delete(taskId);
        Mockito.verify(taskRepository).deleteById(taskId);
    }

    @Test
    void uploadImage(){
        Long id = 1L;
        String imageName = "imageName";
        TaskImage taskImage = new TaskImage();
        Mockito.when(imageService.upload(taskImage)).thenReturn(imageName);
        taskService.uploadImage(id, taskImage);
        Mockito.verify(taskRepository).addImage(id, imageName);
    }
}
