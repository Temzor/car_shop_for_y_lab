package ru.yaone.validator;

import ru.yaone.dto.UserDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс {@code UserDTOValidator} осуществляет валидацию объектов {@code UserDTO}.
 * <p>
 * Этот класс проверяет корректность данных, содержащихся в объекте {@code UserDTO},
 * включая наличие идентификаторов, имени пользователя, пароля и роли пользователя.
 * При наличии ошибок валидации они добавляются в список ошибок, который возвращается
 * как результат работы метода.
 * </p>
 */
public class UserDTOValidator {

    /**
     * Проверяет корректность данных, содержащихся в {@code UserDTO}.
     *
     * @param userDTO объект {@code UserDTO}, данные которого подлежат валидации.
     * @return список строк с описаниями ошибок валидации. Если ошибок нет, возвращается пустой список.
     */
    public static List<String> validate(UserDTO userDTO) {
        List<String> errors = new ArrayList<>();

        if (userDTO.getId() <= 0) {
            errors.add("ID не может быть нулевым и должен быть положительным");
        }

        if (userDTO.getUsername() == null || userDTO.getUsername().trim().isEmpty()) {
            errors.add("Имя пользователя является обязательным");
        }

        if (userDTO.getPassword() == null || userDTO.getPassword().isEmpty()) {
            errors.add("Пароль является обязательным");
        } else if (userDTO.getPassword().length() > 100) {
            errors.add("Пароль должен содержать от 1 до 100 символов");
        }

        if (userDTO.getRole() == null) {
            errors.add("Роль пользователя не может быть нулевой");
        }

        return errors;
    }
}