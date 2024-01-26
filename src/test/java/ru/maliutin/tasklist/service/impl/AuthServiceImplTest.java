package ru.maliutin.tasklist.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.maliutin.tasklist.config.TestConfig;
import ru.maliutin.tasklist.domain.exception.ResourceNotFoundException;
import ru.maliutin.tasklist.domain.user.Role;
import ru.maliutin.tasklist.domain.user.User;
import ru.maliutin.tasklist.repository.TaskRepository;
import ru.maliutin.tasklist.repository.UserRepository;
import ru.maliutin.tasklist.service.UserService;
import ru.maliutin.tasklist.web.dto.aut.JwtRequest;
import ru.maliutin.tasklist.web.dto.aut.JwtResponse;
import ru.maliutin.tasklist.web.security.JwtTokenProvider;

import java.util.Collections;
import java.util.Set;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@Import(TestConfig.class)
@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private TaskRepository taskRepository;

    @MockBean
    private JwtTokenProvider tokenProvider;

    @Autowired
    private AuthServiceImpl authService;

    @Test
    void login(){
        Long userId = 1L;
        String username = "username";
        String password = "password";
        Set<Role> roles = Collections.emptySet();
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";

        JwtRequest request = new JwtRequest();
        request.setUsername(username);
        request.setPassword(password);
        User user = new User();
        user.setId(userId);
        user.setUsername(username);
        user.setRoles(roles);

        Mockito.when(userService.getByUsername(username)).thenReturn(user);
        Mockito.when(tokenProvider.createAccessToken(userId, username, roles))
                .thenReturn(accessToken);
        Mockito.when(tokenProvider.createRefreshToken(userId, username))
                .thenReturn(refreshToken);
        JwtResponse response = authService.login(request);
        Mockito.verify(authenticationManager)
                .authenticate(
                        new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()
                        )
                );
        Assertions.assertEquals(response.getUsername(), username);
        Assertions.assertEquals(response.getId(), userId);
        Assertions.assertNotNull(response.getAccessToken());
        Assertions.assertNotNull(response.getRefreshToken());
    }

    @Test
    void loginWithIncorrectUserName(){
        String username = "username";
        String password = "password";

        JwtRequest request = new JwtRequest();
        request.setUsername(username);
        request.setPassword(password);

        Mockito.when(userService.getByUsername(username))
                .thenThrow(ResourceNotFoundException.class);

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> authService.login(request));
        Mockito.verifyNoInteractions(tokenProvider);
    }

    @Test
    void loginWithIncorrectPassword(){
        String username = "username";
        String password = "password";

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        JwtRequest request = new JwtRequest();
        request.setUsername(username);
        request.setPassword(password);

        Mockito.when(userService.getByUsername(username))
                .thenReturn(user);
        Mockito.when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username, password
                )
        )).thenThrow(AuthenticationServiceException.class);

        Assertions.assertThrows(AuthenticationException.class,
                () -> authService.login(request));
        Mockito.verifyNoInteractions(tokenProvider);
    }

    @Test
    void refresh(){
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        String newRefreshToken = "newRefreshToken";

        JwtResponse response = new JwtResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(newRefreshToken);

        Mockito.when(tokenProvider.refreshUserToken(refreshToken))
                .thenReturn(response);

        JwtResponse testResponse = authService.refresh(refreshToken);
        Assertions.assertEquals(testResponse, response);
        Mockito.verify(tokenProvider).refreshUserToken(refreshToken);
    }
}
