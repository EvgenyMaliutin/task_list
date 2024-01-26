package ru.maliutin.tasklist.service.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * Класс хранящий зависимости для minio.
 */
@Component // Аннотация Spring - отмечающая класс, как компонент приложения.
@Data  // Аннотация lombok - добавляющая конструктор, геттеры, сеттеры и т.д.
@ConfigurationProperties(prefix = "spring.mail")
// Аннотация указывающая откуда брать данные для полей класса:
// application.yaml -> minio
// (названия полей совпадают с ключами в файле)
public class MailProperties {
    /**
     * Хост для соединения с почтовым сервером.
     */
    private String host;
    /**
     * Порт для соединения.
     */
    private int port;
    /**
     * Логин для соединения.
     */
    private String username;
    /**
     * Ключ для подключения к почтовому серверу (не пароль от аккаунта).
     */
    private String password;
    /**
     * TODO добавить описание.
     */
    private Properties properties;


}
