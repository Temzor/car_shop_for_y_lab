package ru.yaone.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yaone.aspect.annotation.Loggable;
import ru.yaone.dto.UserDTO;
import ru.yaone.mapper.UserMapper;
import ru.yaone.model.User;
import ru.yaone.services.UserService;
import jakarta.validation.Valid;
import java.util.List;

/**
 * Контроллер для работы с пользователями.
 */
@Loggable
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@Tag(name = "User Controller", description = "Операции с пользователями")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    /**
     * Получить всех пользователей.
     *
     * @return Список пользователей в формате UserDTO.
     */
    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Получить всех пользователей")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserDTO> usersDTO = userMapper.userToUserDTO(users);
        return ResponseEntity.ok(usersDTO);
    }

    /**
     * Получить пользователя по ID.
     *
     * @param id Идентификатор пользователя.
     * @return Пользователь в формате UserDTO, если найден, иначе 404.
     */
    @GetMapping("/users/{id}")
    @Operation(description = "Получить пользователя по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно найден пользователь"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    public ResponseEntity<UserDTO> getUserById(
            @Parameter(description = "ID пользователя", required = true) @PathVariable("id") int id) {

        User user = userService.getUserById(id);
        return user != null ? ResponseEntity.ok(userMapper.userToUserDTO(user))
                : ResponseEntity.notFound().build();
    }

    /**
     * Добавить нового пользователя.
     *
     * @param userDTO Данные пользователя для добавления.
     * @return Статус 201, если пользователь успешно добавлен.
     */
    @PostMapping(value = "/users")
    @Operation(description = "Добавить нового пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь успешно добавлен"),
            @ApiResponse(responseCode = "400", description = "Неверные данные пользователя")
    })
    public ResponseEntity<Void> addUser(
            @Parameter(description = "Данные пользователя", required = true) @Valid @RequestBody UserDTO userDTO) {

        User user = userMapper.userDTOToUser(userDTO);
        userService.addUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Обновить информацию о пользователе.
     *
     * @param id            Идентификатор пользователя для обновления.
     * @param updatedUserDTO Обновленные данные пользователя.
     * @return Статус 200, если информация успешно обновлена,
     *         статус 404, если пользователь не найден,
     *         статус 500 при ошибке сервера.
     */
    @PutMapping("/users/{id}")
    @Operation(description = "Обновить информацию о пользователе")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация о пользователе обновлена"),
            @ApiResponse(responseCode = "400", description = "Неверные данные пользователя"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    public ResponseEntity<Void> updateUser(
            @Parameter(description = "ID пользователя", required = true) @PathVariable("id") int id,
            @Parameter(description = "Обновленные данные пользователя", required = true) @Valid @RequestBody UserDTO updatedUserDTO) {

        if (userService.getUserById(id) == null) {
            return ResponseEntity.notFound().build();
        }

        User updatedUser = userMapper.userDTOToUser(updatedUserDTO);
        try {
            userService.updateUser(id, updatedUser);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
