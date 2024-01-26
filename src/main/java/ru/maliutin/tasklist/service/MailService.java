package ru.maliutin.tasklist.service;

import ru.maliutin.tasklist.domain.MailType;
import ru.maliutin.tasklist.domain.user.User;

import java.util.Properties;

/**
 * Интерфейс сервиса отправки писем.
 */
public interface MailService {
    /**
     * Отправка письма пользователю.
     * @param user объект пользователя, которому отправляется письмо.
     *             (В объекте содержится email)
     * @param type тип отправляемого письма.
     * @param properties данные, которые понадобятся во время отправки письма.
     */
    void sendEmail(User user, MailType type, Properties properties);

}
