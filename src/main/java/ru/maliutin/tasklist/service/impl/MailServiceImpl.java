package ru.maliutin.tasklist.service.impl;

import freemarker.template.Configuration;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import ru.maliutin.tasklist.domain.MailType;
import ru.maliutin.tasklist.domain.user.User;
import ru.maliutin.tasklist.service.MailService;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    /**
     * Поле конфигурации письма (как будет выглядеть) из библиотеки freemarker.
     */
    private final Configuration configuration;
    /**
     * Интерфейс Spring Framework,
     * предназначенный для отправки электронных писем из приложений.
     */
    private final JavaMailSender mailSender;

    @Override
    public void sendEmail(User user, MailType type, Properties params) {
        switch (type){
            case REGISTRATION -> sendRegistrationEmail(user, params);
            case REMINDER -> sendRemainderEmail(user, params);
            default -> {}
        }
    }

    /**
     * Метод отправки письма при типе РЕГИСТРАЦИЯ.
     * @param user объект пользователя.
     * @param params TODO
     */
    @SneakyThrows
    private void sendRegistrationEmail(User user, Properties params){
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,
                false, "UTF-8");
        // Заголовок письма
        helper.setSubject("Thank you for registration, " + user.getName());
        // Кому отправляем письмо
        helper.setTo(user.getUsername());
        // Наполнение письма информацией
        String emailContent = getRegistrationEmailContent(user, params);
        helper.setText(emailContent, true);
        // Отправляем письмо
        mailSender.send(mimeMessage);
    }

    /**
     * Метод отправки письма при типе НАПОМИНАНИЕ.
     * @param user объект пользователя.
     * @param params TODO
     */
    @SneakyThrows
    private void sendRemainderEmail(User user, Properties params){
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,
                false, "UTF-8");
        // Заголовок письма
        helper.setSubject("You have task to do in 1 hour");
        // Кому отправляем письмо
        helper.setTo(user.getUsername());
        // Наполнение письма информацией
        String emailContent = getReminderEmailContent(user, params);
        helper.setText(emailContent, true);
        // Отправляем письмо
        mailSender.send(mimeMessage);
    }

    /**
     * Метод подготовки теста письма при РЕГИСТРАЦИИ.
     * @param user объект пользователя, которому предназначено письмо.
     * @param param TODO
     * @return содержание письма в строковом представлении.
     */
    @SneakyThrows
    private String getRegistrationEmailContent(User user, Properties param){
        StringWriter writer = new StringWriter();
        // Для передачи данных на форму html используем Map
        Map<String, Object> model = new HashMap<>();
        // файл с содержимым письма
        model.put("name", user.getName());
        configuration.getTemplate("register.ftlh")
                .process(model, writer);
        return writer.getBuffer().toString();
    }

    /**
     * Метод подготовки теста письма при НАПОМИНАНИИ.
     * @param user объект пользователя, которому предназначено письмо.
     * @param param TODO
     * @return содержание письма в строковом представлении.
     */
    @SneakyThrows
    private String getReminderEmailContent(User user, Properties param){
        StringWriter writer = new StringWriter();
        // Для передачи данных на форму html используем Map
        Map<String, Object> model = new HashMap<>();
        // файл с содержимым письма
        model.put("name", user.getName());
        model.put("title", param.getProperty("task.title"));
        model.put("description", param.getProperty("task.description"));
        configuration.getTemplate("reminder.ftlh")
                .process(model, writer);
        return writer.getBuffer().toString();
    }
}
