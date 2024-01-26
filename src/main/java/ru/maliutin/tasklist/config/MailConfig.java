package ru.maliutin.tasklist.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import ru.maliutin.tasklist.service.props.MailProperties;

/**
 * Конфигурация почтового сервера.
 */
@Configuration
@RequiredArgsConstructor
public class MailConfig {
    private final MailProperties mailProperties;

    /**
     * Бин с конфигурацией почтового сервиса предопределенного в Java.
     * @return объект JavaMailSender.
     */
    @Bean
    public JavaMailSender mailSender(){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailProperties.getHost());
        mailSender.setPort(mailProperties.getPort());
        mailSender.setUsername(mailProperties.getUsername());
        mailSender.setPassword(mailProperties.getPassword());
        mailSender.setJavaMailProperties(mailProperties.getProperties());
        return mailSender;
    }
}
