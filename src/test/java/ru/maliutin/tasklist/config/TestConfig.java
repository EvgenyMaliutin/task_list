package ru.maliutin.tasklist.config;

import freemarker.template.Configuration;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.maliutin.tasklist.repository.TaskRepository;
import ru.maliutin.tasklist.repository.UserRepository;
import ru.maliutin.tasklist.service.*;
import ru.maliutin.tasklist.service.impl.*;
import ru.maliutin.tasklist.service.props.JwtProperties;
import ru.maliutin.tasklist.service.props.MinioProperties;
import ru.maliutin.tasklist.web.security.JwtTokenProvider;
import ru.maliutin.tasklist.web.security.JwtUserDetailService;

/**
 * Конфигурационный класс для тестирования приложения, содержит бины (объекты),
 * которые будут использоваться при тестировании приложения.
 */
@TestConfiguration
@RequiredArgsConstructor
public class TestConfig {

    // Необходимые зависимости для создания бинов
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final AuthenticationManager authenticationManager;

    /**
     * Кодировка пароля пользователя.
     * @return бин кодировщика паролей.
     */
    @Bean
    @Primary
    public PasswordEncoder testPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * Зависимости для создания токенов, в частности секретный ключ,
     * отличающийся от реального ключа используемого в приложении,
     * можно получить путем генерации на
     * (<a href="https://www.base64encode.org/">base64</a>).
     * @return зависимости для создания токенов.
     */
    @Bean
    public JwtProperties jwtProperties(){
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecret("ZGZnZGZnZ2Rkc2RmZHNmZHNmc2RmZHNmc2RmZHNmZHNm");
        return jwtProperties;
    }

    /**
     * Сервис аутентификации пользователя используемый Spring Security.
     * @return переопределенный сервис аутентификации используемый в приложении.
     */
    @Bean
    @Primary
    public UserDetailsService userDetailsService(){
        return new JwtUserDetailService(userService());
    }

    /**
     * Доступ к хранилищу картинок в задачах пользователей.
     * @return мок объект хранилища (реальное не используется).
     */
    @Bean
    public MinioClient minioClient(){
        return Mockito.mock(MinioClient.class);
    }

    /**
     * Зависимости для хранили картинок.
     * @return зависимость хранящая название корзины с картинками.
     */
    @Bean
    public MinioProperties minioProperties(){
        MinioProperties properties = new MinioProperties();
        properties.setBucket("images");
        return properties;
    }

    /**
     * Сервис для работы с картинками в задачах.
     * @return новый объект сервиса, в параметры которому переданы
     * объекты созданные в данном классе.
     */
    @Bean
    @Primary
    public ImageService imageService(){
        return new ImageServiceImpl(minioClient(), minioProperties());
    }

    /**
     * Сервис работы с токенами.
     * @return новый объект сервиса, в параметры которому переданы
     * объекты созданные в данном классе.
     */
    @Bean
    public JwtTokenProvider tokenProvider(){
        return new JwtTokenProvider(jwtProperties(),
                userDetailsService(), userService());
    }

    /**
     * Конфигурация freemarker в тесте не используется,
     * необходима для создания бина сервиса писем.
     * @return мок объект конфигурации.
     */
    @Bean
    public Configuration configuration(){
        return Mockito.mock(Configuration.class);
    }

    /**
     * Мок объект отправщика писем, используется
     * для создания сервиса отправки писем.
     * @return мок объект JavaMailSender.
     */
    @Bean
    public JavaMailSender mailSender(){
        return Mockito.mock(JavaMailSender.class);
    }

    /**
     * Бин объекта сервиса для отправки писем.
     * @return объект отправки писем.
     */
    @Bean
    @Primary
    public MailService mailService(){
        return new MailServiceImpl(configuration(), mailSender());
    }
    /**
     * Сервис работы с пользователями.
     * @return новый объект сервиса, в параметры которому переданы
     * объекты созданные в данном классе.
     */
    @Bean
    @Primary
    public UserService userService(){
        return new UserServiceImpl(userRepository, testPasswordEncoder(),
                mailService());
    }

    /**
     * Сервис работы с задачами пользователей.
     * @return новый объект сервиса, в параметры которому переданы
     * объекты созданные в данном классе.
     */
    @Bean
    @Primary
    public TaskService taskService(){
        return new TaskServiceImpl(taskRepository,
                imageService());
    }

    /**
     * Сервис аутентификации пользователей.
     * @return новый объект сервиса, в параметры которому переданы
     * объекты созданные в данном классе.
     */
    @Bean
    @Primary
    public AuthService authService(){
        return new AuthServiceImpl(authenticationManager,
                userService(),
                tokenProvider());
    }
}
