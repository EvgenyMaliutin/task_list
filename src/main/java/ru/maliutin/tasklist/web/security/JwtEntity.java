package ru.maliutin.tasklist.web.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Класс используемый Spring Security для аутентификации пользователя.
 */
@Data
// Аннотация lombok - предоставляет классу конструктор со всеми полями.
@AllArgsConstructor
public class JwtEntity implements UserDetails {
    /**
     * Идентификатор пользователя.
     */
    private long id;
    /**
     * Логин пользователя.
     */
    private final String username;
    /**
     * Имя пользователя.
     */
    private final String name;
    /**
     * Пароль пользователя.
     */
    private final String password;
    /**
     * Коллекция ролей пользователя.
     */
    private final Collection<? extends GrantedAuthority> authorities;

    /**
     * Возвращает коллекцию прав пользователя.
     *
     * @return коллекция прав пользователя.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * Возвращает пароль пользователя.
     *
     * @return пароль пользователя.
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Возвращает логин пользователя.
     *
     * @return логин пользователя.
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * Проверяет не просрочен ли аккаунт.
     *
     * @return всегда возвращает true, в данном классе проверка опущена.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Проверяет не заблокирован ли аккаунт.
     *
     * @return всегда возвращает true, в данном классе проверка опущена.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Проверяет не просрочен ли пароль.
     *
     * @return всегда возвращает true, в данном классе проверка опущена.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Проверяет, работает ли аккаунт.
     *
     * @return всегда возвращает true, в данном классе проверка опущена.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
