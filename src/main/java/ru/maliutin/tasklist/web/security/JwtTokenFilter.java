package ru.maliutin.tasklist.web.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import ru.maliutin.tasklist.domain.exception.ResourceNotFoundException;

import java.io.IOException;

/**
 * Собственный фильтр проводящий аутентификацию
 * пользователя по передаваемому токену.
 */
// Аннотация lombok - добавляет конструктор со всеми полями
@AllArgsConstructor
public class JwtTokenFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Метод работы фильтра. Проводит аутентификацию пользователя.
     *
     * @param servletRequest запрос пользователя.
     * @param servletResponse ответ пользователю.
     * @param filterChain фильтр безопасности.
     * @throws IOException исключение
     * @throws ServletException исключение
     */
    @Override
    public void doFilter(
            final ServletRequest servletRequest,
            final ServletResponse servletResponse,
            final FilterChain filterChain)
            throws IOException, ServletException {
        /*
            Получаем токен из запроса пользователя.
            servletRequest приводим к типу HttpServletRequest,
            используя метод getHeader() получаем заголовок запроса,
            именуемый как Authorization
         */
        String bearerToken = ((HttpServletRequest) servletRequest)
                .getHeader("Authorization");
        // Проверяем что бы полученный токен был не пустым
        // и начинался с "Bearer " (так маркируются токены).
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            // Обрезаем начало строки с токеном убирая "Bearer "
            bearerToken = bearerToken.substring(7);
        }
        // Проверяем что бы полученный токен не был пустым
        // и передаем его на валидацию
        if (bearerToken != null
                && jwtTokenProvider.validateToken(bearerToken)) {
            try {
                // Получаем объект аутентификации передав
                // в метод getAuthentication полученный токен
                Authentication authentication =
                        jwtTokenProvider.getAuthentication(bearerToken);
                // Если объект аутентификации не пуст
                if (authentication != null) {
                    // Сообщаем Spring что пользователь прошел аутентификацию
                    SecurityContextHolder
                            .getContext().setAuthentication(authentication);
                }
            } catch (ResourceNotFoundException ignored) {
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
