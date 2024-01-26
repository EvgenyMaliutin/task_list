package ru.maliutin.tasklist.service;

import ru.maliutin.tasklist.web.dto.aut.JwtRequest;
import ru.maliutin.tasklist.web.dto.aut.JwtResponse;

/**
 * Интерфейс сервиса аутентификации.
 */
public interface AuthService {
    /**
     * @param loginRequest запрос на аутентификацию.
     * @return ответ токенами.
     */
    JwtResponse login(JwtRequest loginRequest);

    /**
     * @param refreshToken долгоживущий токен
     * @return ответ с парой токенов.
     */
    JwtResponse refresh(String refreshToken);

}
