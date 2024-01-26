package ru.maliutin.tasklist.service.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Класс хранящий зависимости для minio.
 */
@Component // Аннотация Spring - отмечающая класс, как компонент приложения.
@Data  // Аннотация lombok - добавляющая конструктор, геттеры, сеттеры и т.д.
@ConfigurationProperties(prefix = "minio")
// Аннотация указывающая откуда брать данные для полей класса:
// application.yaml -> minio
// (названия полей совпадают с ключами в файле)
public class MinioProperties {
    /**
     * Корзина для хранения изображений.
     */
    private String bucket;
    /**
     * Путь до хранилища изображений.
     */
    private String url;
    /**
     * Ключ для подключения к хранилищу (клиента).
     */
    private String accessKey;
    /**
     * Ключ для подключения к хранилищу (админа).
     */
    private String secretKey;


}
