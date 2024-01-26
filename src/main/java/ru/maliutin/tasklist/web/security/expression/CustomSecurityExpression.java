package ru.maliutin.tasklist.web.security.expression;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.maliutin.tasklist.domain.user.Role;
import ru.maliutin.tasklist.service.UserService;
import ru.maliutin.tasklist.web.security.JwtEntity;

/**
 * Первый способ. Нужно создать этот класс и повесить аннотации в контреллерах.
 * Класс определяющий возможность пользователя доступа
 * к данных сервиса (доступ к задачам).
 */
@Service("customSecurityExpression")
@RequiredArgsConstructor
public class CustomSecurityExpression {
    // Поле объекта сервиса объектов User
    private final UserService userService;

    public boolean canAccessUser(final Long id) {
        Authentication authentication = SecurityContextHolder
                .getContext().getAuthentication();

        JwtEntity user = (JwtEntity) authentication.getPrincipal();

        Long userId = user.getId();

        return userId.equals(id) || hasAnyRole(authentication, Role.ROLE_ADMIN);
    }

    /**
     * Служебный метод проверки присутствия
     * у объекта аутентификации переданных ролей.
     *
     * @param authentication объект аутентификации.
     * @param roles          коллекция ролей
     * @return true - если какая-либо из ролей коллекции
     * присутствует у объекта аутентификации, иначе false.
     */
    private boolean hasAnyRole(
            final Authentication authentication, final Role... roles) {
        for (Role role : roles) {
            SimpleGrantedAuthority authority =
                    new SimpleGrantedAuthority(role.name());
            if (authentication
                    .getAuthorities()
                    .contains(authority)) {
                return true;
            }
        }
        return false;
    }

    public boolean canAccessTask(final long taskId) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        JwtEntity user = (JwtEntity) authentication.getPrincipal();
        Long userId = user.getId();

        return userService.isTaskOwner(userId, taskId);
    }
}
