package ru.maliutin.tasklist.web.controller;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.maliutin.tasklist.domain.exception.AccessDeniedException;
import ru.maliutin.tasklist.domain.exception.ExceptionBody;
import ru.maliutin.tasklist.domain.exception.ResourceMappingException;
import ru.maliutin.tasklist.domain.exception.ResourceNotFoundException;
import ru.maliutin.tasklist.domain.exception.ImageUploadException;


import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс перехватывающий исключения и возвращающий их на фронт в удобном виде.
 */

@RestControllerAdvice
public class ControllerAdvice {

    /**
     * Исключение при ненахождении данных в БД.
     *
     * @param e исключение ResourceNotFoundException
     * @return объект ExceptionBody
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionBody handleResourceNotFound(
            final ResourceNotFoundException e) {
        return new ExceptionBody(e.getMessage());
    }

    /**
     * Исключение при преобразовании объектов.
     *
     * @param e исключение ResourceNotFoundException
     * @return объект ExceptionBody
     */
    @ExceptionHandler(ResourceMappingException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionBody handleResourceMapping(
            final ResourceNotFoundException e) {
        return new ExceptionBody(e.getMessage());
    }

    /**
     * Исключение при несовпадении паролей
     * или повторной регистрации пользователя.
     *
     * @param e объект исключение IllegalStateException
     * @return объект ExceptionBody
     */
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody handleIllegalState(
            final IllegalStateException e) {
        return new ExceptionBody(e.getMessage());
    }

    /**
     * Исключение при ошибках аутентификации,
     * неверном токене.
     *
     * @return объект ExceptionBody
     */
    @ExceptionHandler({AccessDeniedException.class,
            org.springframework.security.access.AccessDeniedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ExceptionBody handleAccessDenied() {
        return new ExceptionBody("Access denied.");
    }

    /**
     * Исключение генерируемое при валидации данных.
     *
     * @param e исключение MethodArgumentNotValidException.
     * @return объект ExceptionBody.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody handleMethodArgumentNotValid(
            final MethodArgumentNotValidException e) {
        ExceptionBody exceptionBody = new ExceptionBody("Validation failed");
        List<FieldError> errors = e.getBindingResult().getFieldErrors();
        exceptionBody.setErrors(errors.stream()
                .collect(Collectors
                        .toMap(FieldError::getField,
                                FieldError::getDefaultMessage)));
        return exceptionBody;
    }

    /**
     * Исключение генерируемое при валидации данных.
     *
     * @param e исключение ConstraintViolationException
     * @return объект ExceptionBody
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody handleConstraintViolation(
            final ConstraintViolationException e) {
        ExceptionBody exceptionBody = new ExceptionBody("Validation failed");
        exceptionBody.setErrors(e.getConstraintViolations()
                .stream().collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        violation -> violation.getMessage()
                )));
        return exceptionBody;
    }

    /**
     * Обработка исключения при некорректной аутентификации пользователя.
     *
     * @param e исключение AuthenticationException.
     * @return объект ExceptionBody.
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody handlerBadCredentials(
            final AuthenticationException e) {
        e.printStackTrace();
        return new ExceptionBody("Authentication failed");
    }

    /**
     * Обработка исключения при ошибке загрузки изображения.
     *
     * @param e исключение ImageUploadException.
     * @return объект ExceptionBody.
     */
    @ExceptionHandler(ImageUploadException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody handleImageUpload(
            final ImageUploadException e) {
        return new ExceptionBody(e.getMessage());
    }

    /**
     * Метод перехватывающий все оставшиеся
     * не обработанные в данном классе исключения.
     *
     * @param e общий класс исключений
     * @return объект обобщенного исключения.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionBody handleException(final Exception e) {
        e.printStackTrace();
        return new ExceptionBody("Internal error.");
    }
}
