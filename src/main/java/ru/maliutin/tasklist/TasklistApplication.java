package ru.maliutin.tasklist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Класс конфигурации приложения.
 */
/* Аннотация заменяет:
   @ComponentScan;
   @SpringBootConfiguration;
   @Inherited;
   (Указывает класс конфигурации, который объявляет один или несколько Бинов,
   а так же запускает авто-конфигурацию и сканирование приложения
   на наличие компонентов приложения.
   На уровне пакета в котором находится класс,
   а так же пакетов, которые находятся внутри текущего пакета.).
 */
@SpringBootApplication
@EnableTransactionManagement // Аннотация включающая поддержание транзакций
@EnableCaching // Аннотация подключающая кеширование для запросов к БД
@EnableScheduling // Аннотация для работы cron
public class TasklistApplication {
    /**
     * Точка входа в программу.
     *
     * @param args
     */
    public static void main(final String[] args) {
        SpringApplication.run(TasklistApplication.class, args);
    }

}
