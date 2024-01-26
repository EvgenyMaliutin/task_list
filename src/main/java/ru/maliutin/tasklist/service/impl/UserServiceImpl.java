package ru.maliutin.tasklist.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.maliutin.tasklist.domain.MailType;
import ru.maliutin.tasklist.domain.exception.ResourceNotFoundException;
import ru.maliutin.tasklist.domain.user.Role;
import ru.maliutin.tasklist.domain.user.User;
import ru.maliutin.tasklist.repository.UserRepository;
import ru.maliutin.tasklist.service.MailService;
import ru.maliutin.tasklist.service.UserService;

import java.util.Properties;
import java.util.Set;

/**
 * Класс реализующий интерфейс UserService и содержащий бизнес-логику программы.
 * Осуществляет запросы к репозиторию и взаимодействующий с моделью User.
 */
// Аннотация обозначающая класс как объект сервиса для Spring
@Service
/*
    Аннотация lombok - используется для
    автоматической генерации конструктора,
    исходя из аргументов полей класса
 */
@RequiredArgsConstructor
// Аннотация указывающая,
// что в классе производятся транзакции при обращении к БД
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    /**
     * Поле с репозиторием объекта User.
     */
    private final UserRepository userRepository;
    /**
     * Поле с объектом кодирования паролей.
     */
    private final PasswordEncoder passwordEncoder;
    /**
     * Поле с объектом сервиса отправки писем.
     */
    private final MailService mailService;

    /**
     * Получение пользователя по идентификатору.
     *
     * @param id идентификатор пользователя.
     * @return объект пользователя.
     * @throws ResourceNotFoundException пользователь не найден.
     */
    @Override
    // Добавляет данные в кеш
    @Cacheable(value = "UserService::getById", key = "#id")
    public User getById(final long id) throws ResourceNotFoundException {
        return userRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));
    }

    /**
     * Получение пользователя по логину.
     *
     * @param username логин пользователя.
     * @return объект пользователя.
     * @throws ResourceNotFoundException пользователь не найден.
     */
    @Override
    // Добавляет данные в кеш
    @Cacheable(value = "UserService::getByUsername", key = "#username")
    public User getByUsername(
            final String username) throws ResourceNotFoundException {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));
    }

    /**
     * Обновление пользователя.
     *
     * @param user объект пользователя.
     * @return обновленный объект пользователя.
     */
    @Override
    @Transactional
    @Caching(put = {  // Изменяет данные в кеше
            @CachePut(value = "UserService::getById",
                    key = "#user.id"),
            @CachePut(value = "UserService::getByUsername",
                    key = "#user.username")
    })
    public User update(final User user) {
        // Кодируем сырой пароль пользователя при сохранении в БД
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return user;
    }

    /**
     * Создание пользователя.
     *
     * @param user объект пользователя.
     * @return созданный объект пользователя.
     * @throws IllegalArgumentException пользователь с таким логином существует, пароли не совпадают.
     */
    @Override
    @Transactional
    @Caching(cacheable = {  // Изменяет данные в кеше
            @Cacheable(value = "UserService::getById",
                    key = "#user.id"),
            @Cacheable(value = "UserService::getByUsername",
                    key = "#user.username")
    })
    public User create(final User user) {
        // Проверка, что пользователя с таким логином не существует.
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalStateException("User already exists");
        }
        // Проверка, что пароль и подтверждение пароля пользователя совпадают
        if (!user.getPassword().equals(user.getPasswordConfirmation())) {
            throw new IllegalStateException(
                    "Password end password confirmation do not match.");
        }
        // Кодируем сырой пароль пользователя при сохранении в БД
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        //Задаем роль пользователя
        Set<Role> roles = Set.of(Role.ROLE_USER);
        user.setRoles(roles);
        userRepository.save(user);
        mailService.sendEmail(user, MailType.REGISTRATION, new Properties());
        return user;
    }

    /**
     * Проверка, принадлежит ли задача пользователю.
     *
     * @param userId идентификатор пользователя.
     * @param taskId идентификатор задачи.
     * @return true - если принадлежит, иначе false.
     */
    @Override
    @Cacheable(value = "UserService::isTaskOwner",
            key = "#userId + '.' + #taskId") // Помещает данные в кеш
    public boolean isTaskOwner(final Long userId, final long taskId) {
        return userRepository.isTaskOwner(userId, taskId);
    }

    /**
     * Удаление пользователя.
     *
     * @param id идентификатор пользователя.
     */
    @Override
    @CacheEvict(value = "UserService::getById",
            key = "#id") // Удаляет данные из кеша
    public void delete(final long id) {
        userRepository.deleteById(id);
    }

    /**
     * Получение автора по идентификатору задачи.
     *
     * @param taskId идентификатор задачи.
     * @return объект пользователя.
     */
    @Override
    @Cacheable(value = "UserService:getTaskAuthor",
            key = "#taskId")
    public User getTaskAuthor(Long taskId) {
        return userRepository.findTaskAuthor(taskId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));
    }
}
