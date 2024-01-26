package ru.maliutin.tasklist.web.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ru.maliutin.tasklist.domain.exception.AccessDeniedException;
import ru.maliutin.tasklist.domain.user.Role;
import ru.maliutin.tasklist.domain.user.User;
import ru.maliutin.tasklist.service.UserService;
import ru.maliutin.tasklist.service.props.JwtProperties;
import ru.maliutin.tasklist.web.dto.aut.JwtResponse;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Сервисный класс обеспечивающий работу с токенами.
 * Их создание, проверку.
 */
// Аннотация Spring - отмечающая класс, как сервисный класс приложения.
@Service
// Аннотация lombok - создающая в классе конструктор для полей.
@RequiredArgsConstructor
public class JwtTokenProvider {
    /**
     * Поле с данными токена полученными из application.yaml.
     */
    private final JwtProperties jwtProperties;
    /**
     * +
     * Поле с сервисом авторизации пользователя.
     */
    private final UserDetailsService userDetailsService;
    /**
     * Поле с сервисом для работы с БД объекта пользователя.
     */
    private final UserService userService;
    /**
     * Поле с секретным ключом токенов.
     * Заполняется в конструкторе из зависимостей в application.yaml
     */
    private Key key;

    /**
     * Заполнение поля ключа. В поле присваивается объект Keys
     * в который вносится секретный ключ из application.yaml
     * Используются библиотеки jjwt.
     */
    // Аннотация указывающая, что метод должен быть
    // вызван после инициализации конструктора.
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }

    /**
     * Метод создания access (короткоживущего) токена.
     * Короткоживущий токен используется
     * для аутентификации и авторизации пользователя.
     *
     * @param userId   идентификатор пользователя.
     * @param username логин пользователя.
     * @param roles    роли пользователя.
     * @return строку в access токеном.
     */
    public String createAccessToken(
            final Long userId,
            final String username,
            final Set<Role> roles) {
        /*
            Claims - объект хранящий информацию о пользователе в токене.
                    Работает как Map и в него передаем id пользователя.
            Jwts - фабричный класс используемый для создания
            экземпляров интерфейсов JWT
         */
        Claims claims = Jwts.claims()
                .subject(username)
                .add("id", userId)
                .add("roles", resolveRoles(roles))
                .build();

        /*
            Время когда токен перестанет быть действительным
            (текущее время + время жизни (маленькое (1))
            из зависимостей application.yaml приведенное к часам).
         */
        Instant validity = Instant.now()
                .plus(jwtProperties.getAccess(), ChronoUnit.HOURS);
        // Собираем и возвращаем токен
        return Jwts.builder()
                // Передаем в токен
                // 1. Информацию о пользователе
                .claims(claims)
                // 3. Время "смерти" токена
                .expiration(Date.from(validity))
                // 4. Секретный ключ токена
                .signWith(key)
                // Собираем токен.
                .compact();
    }

    /**
     * Служебный метод преобразующий
     * множество перечислений ролей в лист строк с именами ролей.
     *
     * @param roles множество ролей.
     * @return список с именами ролей.
     */
    private List<String> resolveRoles(final Set<Role> roles) {
        return roles.stream().map(Enum::name).toList();
    }

    /**
     * Создание refresh (долгоживущего) токена.
     *
     * @param userId   идентификатор пользователя.
     * @param username логин пользователя.
     * @return долгоживущий токен.
     */
    public String createRefreshToken(
            final Long userId,
            final String username) {
        Claims claims = Jwts.claims()
                .subject(username).add("id", userId).build();
        Instant validity = Instant.now()
                .plus(jwtProperties.getAccess(), ChronoUnit.DAYS);
        return Jwts.builder()
                .claims(claims)
                .expiration(Date.from(validity))
                .signWith(key)
                .compact();
    }

    /**
     * Метод получающий refresh токен, производящий его валидацию,
     * если валидация успешна, для пользователя обновляется пара токенов
     * и отправляется обратно.
     *
     * @param refreshToken долгоживущий токен.
     * @return jwt ответ с парой токенов.
     */
    public JwtResponse refreshUserToken(final String refreshToken) {
        // Создаем новый объект Jwt ответа.
        JwtResponse jwtResponse = new JwtResponse();
        // Производим валидацию полученного долгоживущего токена
        if (!validateToken(refreshToken)) {
            // В случае некорректной валидации
            // выбрасываем собственное исключение.
            throw new AccessDeniedException();
        }
        // Иначе получаем Id пользователя
        Long userId = Long.parseLong(getId(refreshToken));
        // Подгружаем пользователя из БД.
        User user = userService.getById(userId);
        // Заполняем поля объекта Jwt ответа
        jwtResponse.setId(userId);
        jwtResponse.setUsername(user.getUsername());
        jwtResponse.setAccessToken(
                createAccessToken(
                        userId,
                        user.getUsername(),
                        user.getRoles()));
        jwtResponse.setRefreshToken(
                createRefreshToken(
                        userId,
                        user.getUsername()));
        // Отправляем Jwt ответ.
        return jwtResponse;
    }

    /**
     * Метод производящий валидацию токенов.
     * (Обрабатывает как access, так и refresh токены)
     *
     * @param token токен в строковом представлении.
     * @return true при успешной валидации, иначе false.
     */
    public boolean validateToken(final String token) {
        // Проводим преобразование полученной строки с токеном в объект токена.
        Jws<Claims> claims = Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseSignedClaims(token);

        /*  Получаем данные из преобразованного токена getPayload(),
            получаем метку времени жизни токена getExpiration(),
            проверяем что она раньше чем текущее время.
            Возвращаем отрицание полученного результата,
            если время жизни истекло вернем false, иначе вернем true.
        */
        return !claims.getPayload().getExpiration().before(new Date());
    }

    /**
     * Метод получения Id пользователя из токена.
     *
     * @param token токен в строковом представлении
     * @return Id пользователя в строковом представлении
     */
    private String getId(final String token) {
        // Проводим преобразование полученной строки с токеном в объект токена.
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseSignedClaims(token)
                // Получаем полезные данные (тело) токена.
                .getPayload()
                // По ключу из тела достаем id.
                .get("id")
                // Полученный id в виде строки возвращаем.
                .toString();
    }

    /**
     * Метод прохождения пользователем аутентификации.
     *
     * @param token токен в строковом представлении.
     * @return объект аутентификации.
     */
    public Authentication getAuthentication(final String token) {
        // Используя служебный метод получаем логин пользователя
        String username = getUsername(token);
        // Используя userDetailsService загружаем пользователя из БД
        // используя метод loadUserByUsername
        UserDetails userDetails =
                userDetailsService.loadUserByUsername(username);
        // Возвращаем Spring Security учетные данные пользователя.
        return new UsernamePasswordAuthenticationToken(
                userDetails, "", userDetails.getAuthorities());
    }

    /**
     * Метод получения логина пользователя из токена.
     *
     * @param token токен в строковом представлении.
     * @return логин пользователя в строковом представлении.
     */
    private String getUsername(final String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
