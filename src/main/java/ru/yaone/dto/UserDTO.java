package ru.yaone.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.yaone.model.enumeration.UserRole;

/**
 * Класс {@code UserDTO} представляет собой Data Transfer Object (DTO) для пользователя.
 *
 * <p>Содержит информацию о пользователе, включая уникальный идентификатор,
 * имя пользователя, пароль и роль пользователя. Этот класс используется для передачи данных
 * между слоями приложения.</p>
 *
 * @author Ваше имя
 * @version 1.0
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    /**
     * Уникальный идентификатор пользователя.
     *
     * <p>Не должен быть {@code null} и должен быть положительным.</p>
     */
    @NotNull(message = "ID cannot be null")
    @Positive(message = "ID should be positive")
    private int id;

    /**
     * Имя пользователя.
     *
     * <p>Обязательное поле, не должно быть пустым или содержать только пробелы.</p>
     */
    @NotBlank(message = "Username is mandatory")
    private String username;

    /**
     * Пароль пользователя.
     *
     * <p>Обязательное поле, не должно быть пустым. Должен быть от 1 до 100 символов.</p>
     */
    @NotBlank(message = "Password is mandatory")
    @Size(min = 1, max = 100, message = "Password must be between 1 and 100 characters")
    private String password;

    /**
     * Роль пользователя.
     *
     * <p>Не должна быть {@code null}.</p>
     */
    @NotNull(message = "User role cannot be null")
    private UserRole role;
}