package ru.yaone.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yaone.dto.UserDTO;
import ru.yaone.model.enumeration.UserRole;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserDTOValidatorTest {

    @Test
    @DisplayName("Тест: валидный объект UserDTO")
    public void testValidUserDTO() {
        UserDTO userDTO = new UserDTO(1, "username", "password", UserRole.CLIENT);
        List<String> validationResult = UserDTOValidator.validate(userDTO);
        assertTrue(validationResult.isEmpty(), "Validation should pass for valid DTO.");
    }

    @Test
    @DisplayName("Тест: отрицательный ID пользователя")
    public void testNegativeUserId() {
        UserDTO userDTO = new UserDTO(-1, "username", "password", UserRole.CLIENT);
        List<String> validationResult = UserDTOValidator.validate(userDTO);
        assertFalse(validationResult.contains("ID cannot be null and should be positive"), "Validation should fail for negative user ID.");
    }

    @Test
    @DisplayName("Тест: пустое имя пользователя")
    public void testEmptyUsername() {
        UserDTO userDTO = new UserDTO(1, "", "password", UserRole.CLIENT);
        List<String> validationResult = UserDTOValidator.validate(userDTO);
        assertFalse(validationResult.contains("Username is mandatory"), "Validation should fail for empty username.");
    }

    @Test
    @DisplayName("Тест: пустой пароль")
    public void testEmptyPassword() {
        UserDTO userDTO = new UserDTO(1, "username", "", UserRole.CLIENT);
        List<String> validationResult = UserDTOValidator.validate(userDTO);
        assertFalse(validationResult.contains("Password is mandatory"), "Validation should fail for empty password.");
    }

    @Test
    @DisplayName("Тест: пароль слишком длинный")
    public void testPasswordTooLong() {
        String longPassword = "a".repeat(101); // Создаем пароль длиной 101 символ
        UserDTO userDTO = new UserDTO(1, "username", longPassword, UserRole.CLIENT);
        List<String> validationResult = UserDTOValidator.validate(userDTO);
        assertFalse(validationResult.contains("Password must be between 1 and 100 characters"), "Validation should fail for password too long.");
    }

    @Test
    @DisplayName("Тест: нулевая роль пользователя")
    public void testNullUserRole() {
        UserDTO userDTO = new UserDTO(1, "username", "password", null);
        List<String> validationResult = UserDTOValidator.validate(userDTO);
        assertFalse(validationResult.contains("User role cannot be null"), "Validation should fail for null user role.");
    }
}
