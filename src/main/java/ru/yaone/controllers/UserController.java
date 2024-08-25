package ru.yaone.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
import java.util.List;

@Loggable
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@Api(value = "User Controller", tags = "Операции с пользователями")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Получить всех пользователей", response = UserDTO.class, responseContainer = "List")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserDTO> usersDTO = userMapper.userToUserDTO(users);
        return ResponseEntity.ok(usersDTO);
    }

    @GetMapping("/users/{id}")
    @ApiOperation(value = "Получить пользователя по ID", response = UserDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешно найден пользователь"),
            @ApiResponse(code = 404, message = "Пользователь не найден")
    })
    public ResponseEntity<UserDTO> getUserById(@ApiParam(value = "ID пользователя", required = true) @PathVariable("id") int id) {
        User user = userService.getUserById(id);
        if (user != null) {
            UserDTO userDTO = userMapper.userToUserDTO(user);
            return ResponseEntity.ok(userDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "/users")
    @ApiOperation(value = "Добавить нового пользователя")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Пользователь успешно добавлен"),
            @ApiResponse(code = 400, message = "Неверные данные пользователя")
    })
    public ResponseEntity<Void> addUser(@ApiParam(value = "Данные пользователя", required = true) @RequestBody UserDTO userDTO) {
        User user = userMapper.userDTOToUser(userDTO);
        System.out.println("Добавление пользователя: " + user);
        userService.addUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/users/{id}")
    @ApiOperation(value = "Обновить информацию о пользователе")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Информация о пользователе обновлена"),
            @ApiResponse(code = 400, message = "Неверные данные пользователя"),
            @ApiResponse(code = 404, message = "Пользователь не найден"),
            @ApiResponse(code = 500, message = "Ошибка сервера")
    })
    public ResponseEntity<Void> updateUser(@ApiParam(value = "ID пользователя", required = true) @PathVariable("id") int id,
                                           @ApiParam(value = "Обновленные данные пользователя", required = true) @RequestBody UserDTO updatedUserDTO) {
        if (updatedUserDTO == null) {
            return ResponseEntity.badRequest().build();
        }
        User updatedUser = userMapper.userDTOToUser(updatedUserDTO);
        try {
            if (userService.getUserById(id) == null) {
                return ResponseEntity.notFound().build();
            }
            userService.updateUser(id, updatedUser);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}