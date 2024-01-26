package ru.maliutin.tasklist.web.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.maliutin.tasklist.domain.user.User;
import ru.maliutin.tasklist.service.UserService;

/**
 * Сервисный класс осуществляющий работу с UserService,
 * получающий пользователя из БД,
 * и предающий Spring Security преобразованного полученного
 * пользователя для проведения аутентификации.
 */

// Аннотация Spring - отмечающая класс, как сервисный.
@Service
// Аннотация lombok - предоставляющая конструктор
// в соответствии с полями класса.
@RequiredArgsConstructor
public class JwtUserDetailService implements UserDetailsService {
    /**
     * Поле сервиса UserService для работы с БД.
     */
    private final UserService userService;

    /**
     * Реализованный метод интерфейса UserDetailsService,
     * используя UserService осуществляет запрос в БД для поиска
     * пользователя по логину переданному в параметр метода.
     * Используя маппер преобразует полученный объект пользователя в тип
     * необходимый для Spring Security.
     *
     * @param username логин пользователя.
     * @return преобразованный объект.
     * @throws UsernameNotFoundException генерируется
     * в случае отсутствия пользователя в БД.
     */
    @Override
    public UserDetails loadUserByUsername(
            final String username)
            throws UsernameNotFoundException {
        User user = userService.getByUsername(username);
        return JwtEntityFactory.create(user);
    }
}
