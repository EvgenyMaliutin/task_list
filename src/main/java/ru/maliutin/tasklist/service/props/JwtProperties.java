package ru.maliutin.tasklist.service.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Класс хранящий зависимости для токена.
 */
@Component // Аннотация Spring - отмечающая класс, как компонент приложения.
@Data  // Аннотация lombok - добавляющая конструктор, геттеры, сеттеры и т.д.
@ConfigurationProperties(prefix = "security.jwt")
/*
    Аннотация указывающая откуда брать данные для полей класса:
    application.yaml -> security -> jwt
    (названия полей совпадают с ключами в файле)
 */
public class JwtProperties {
    /**
     * Секретный ключ для подписи токена.
     */
    private String secret;
    /**
     * Время жизни access токена (время жизни маленькое).
     */
    private Long access;
    /**
     * Время жизни refresh токена (время жизни большое).
     */
    private Long refresh;
}
