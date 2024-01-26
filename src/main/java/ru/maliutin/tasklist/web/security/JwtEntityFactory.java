package ru.maliutin.tasklist.web.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ru.maliutin.tasklist.domain.user.Role;
import ru.maliutin.tasklist.domain.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс преобразующий объект модели пользователя (User) в объект
 * проверяемый Spring Security на предмет авторизации в приложении.
 * Своего рода маппер для Spring Security.
 */
public class JwtEntityFactory {

    /**
     * Метод преобразующий объект модели в JwtEntity,
     * т.е. тот объект, который будет проверять Spring Security.
     *
     * @param user объект модели.
     * @return объект проверки для Spring Security.
     */
    public static JwtEntity create(final User user) {
        return new JwtEntity(
                user.getId(),  // кладем в объект id
                user.getUsername(),  // кладем в объект логин
                user.getName(),  // кладем в объект имя
                user.getPassword(),  // кладем в объект пароль
                mapToGrantedAuthorities(
                        // вызываем служеб. метод для преобразования ролей
                        new ArrayList<>(user.getRoles()))
        );
    }

    /**
     * Служебный метод, который преобразует роли пользователя
     * из ENUM в GrantedAuthority.
     * GrantedAuthority - используется Spring Security для проверки
     * ролей пользователя и предоставления соответствующего уровня доступа.
     *
     * @param roles роли пользователя.
     * @return коллекция GrantedAuthority пользователя.
     */
    private static List<GrantedAuthority> mapToGrantedAuthorities(
            final List<Role> roles) {
        return roles.stream()
                .map(Enum::name)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
