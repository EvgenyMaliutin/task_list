package ru.maliutin.tasklist.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.maliutin.tasklist.domain.MailType;
import ru.maliutin.tasklist.domain.task.Task;
import ru.maliutin.tasklist.domain.user.User;
import ru.maliutin.tasklist.service.MailService;
import ru.maliutin.tasklist.service.Reminder;
import ru.maliutin.tasklist.service.TaskService;
import ru.maliutin.tasklist.service.UserService;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

@Service
@RequiredArgsConstructor
public class ReminderImpl implements Reminder {

    private final TaskService taskService;
    private final UserService userService;
    private final MailService mailService;

    private final Duration DURATION = Duration.ofHours(1);

    // Указание методу выполнятся раз час (каждые 00 минут)
//    @Scheduled(cron = "0 0 * * * *")
    @Scheduled(cron = "0 * * * * *")
    @Override
    public void remindForTask() {
        // Получаем задачи до истечения которых остался час.
        List<Task> tasks = taskService.getAllSoonTasks(DURATION);
        tasks.forEach(task -> {
            User user = userService.getTaskAuthor(task.getId());
            Properties properties = new Properties();
            properties.setProperty("task.title", task.getTitle());
            properties.setProperty("task.description", task.getDescription());
            mailService.sendEmail(user, MailType.REMINDER, properties);
        });
    }
}
