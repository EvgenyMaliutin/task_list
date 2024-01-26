package ru.maliutin.tasklist.config;

import io.minio.MinioClient;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.maliutin.tasklist.service.props.MinioProperties;
import ru.maliutin.tasklist.web.security.JwtTokenFilter;
import ru.maliutin.tasklist.web.security.JwtTokenProvider;


/**
 * Класс конфигурации Spring Security.
 */

// Аннотация Spring - отмечающая класс, как конфигурационный.
@Configuration

/*
   Аннотация Spring Security - отмечающая класс,
   как конфигурационный для Spring Security.
 */
@EnableWebSecurity
// Аннотация lombok - предоставляющая классу конструкторы.
@RequiredArgsConstructor(onConstructor = @__(@Lazy))
/*
    Благодаря @Lazy, эти аргументы будут созданы
    только при первом обращении к ним.
    Это может быть полезно, если создание аргументов
    потребует затратных ресурсов
    или если вы хотите отложить их инициализацию.
    В данном случае используется для предотвращения циклической зависимости
    между компонентами Spring.
 */
/*
    Аннотация Spring включающая на уровне приложения
    проверку прав доступа в контроллерах.
 */
@EnableMethodSecurity
public class ApplicationConfig {

    /**
     * Поле с объектом класса отвечающим за создание и проверку токенов.
     */
    private final JwtTokenProvider tokenProvider;

    private final MinioProperties minioProperties;

    /**
     * Бин конфигурации minio.
     *
     * @return возвращает клиент Minio.
     */
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioProperties.getUrl())
                .credentials(
                        minioProperties.getAccessKey(),
                        minioProperties.getSecretKey())
                .build();
    }

    /**
     * Бин конфигурации Swagger.
     *
     * @return конфигурационный бин swagger.
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().addSecurityItem(
                        new SecurityRequirement()
                                .addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes(
                                "bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT"))
                )
                .info(new Info()
                        .title("Task list API")
                        .description("Demo Spring Boot application")
                        .version("1.0")
                );
    }

    /**
     * Бин отвечающий за хэширование паролей при прохождении аутентификации.
     *
     * @return бин PasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Бин отвечающий за аутентификацию пользователя.
     *
     * @param configuration конфигурацию аутентификации.
     * @return менеджер аутентификации.
     * @throws Exception любые исключения связан. с аутентиф.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            final AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Бин с настройками фильтров Spring Security.
     *
     * @param httpSecurity настройку Spring Security.
     * @return фильтр безопасности.
     */
    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity httpSecurity)
            throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(except -> except
                        .authenticationEntryPoint(
                                (request, response, authException) -> {
                                    response.setStatus(
                                            HttpStatus.UNAUTHORIZED.value());
                                    response
                                            .getWriter()
                                            .write("Unauthorized.");
                                })
                        .accessDeniedHandler(
                                (request, response, accessDeniedException) -> {
                                    response.setStatus(
                                            HttpStatus.FORBIDDEN.value());
                                    response
                                            .getWriter()
                                            .write("Unauthorized.");
                                }))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/graphiql").permitAll()
                        .anyRequest().authenticated()
                )
                .anonymous(AbstractHttpConfigurer::disable)
                .addFilterBefore(new JwtTokenFilter(tokenProvider),
                        UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

}
