package ru.yaone.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yaone.dto.UserDTO;
import ru.yaone.mapper.UserMapper;
import ru.yaone.model.User;
import ru.yaone.model.enumeration.UserRole;
import ru.yaone.services.UserService;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    @DisplayName("Тест получения всех пользователей")
    void testGetAllUsers() throws Exception {
        User user = new User(1, "John Doe", "123", UserRole.CLIENT);
        UserDTO userDTO = new UserDTO(1, "John Doe", "123", UserRole.CLIENT);
        List<User> users = Collections.singletonList(user);

        when(userService.getAllUsers()).thenReturn(users);
        when(userMapper.userToUserDTO(users)).thenReturn(List.of(userDTO));

        mockMvc.perform(get("/api/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].username").value("John Doe"));

        verify(userService, times(1)).getAllUsers();
        verify(userMapper, times(1)).userToUserDTO(users);
    }

    @Test
    @DisplayName("Тест получения пользователей по ID (найден)")
    void testGetUserByIdUserExists() throws Exception {
        User user = new User(1, "John Doe", "123", UserRole.CLIENT);
        UserDTO userDTO = new UserDTO(1, "John Doe", "123", UserRole.CLIENT);
        when(userService.getUserById(1)).thenReturn(user);
        when(userMapper.userToUserDTO(user)).thenReturn(userDTO);

        mockMvc.perform(get("/api/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("John Doe"));

        verify(userService, times(1)).getUserById(1);
        verify(userMapper, times(1)).userToUserDTO(user);
    }

    @Test
    @DisplayName("Тест получения пользователя по ID (не найден)")
    void testGetUserByIdUserNotFound() throws Exception {
        when(userService.getUserById(anyInt())).thenReturn(null);

        mockMvc.perform(get("/api/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserById(1);
    }

    @Test
    @DisplayName("Тест добавления пользователя")
    void testAddUser() throws Exception {
        UserDTO userDTO = new UserDTO(1, "John Doe", "123", UserRole.CLIENT);
        User newUser = new User(1, "John Doe", "123", UserRole.CLIENT);

        when(userMapper.userDTOToUser(any(UserDTO.class))).thenReturn(newUser);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userDTO)))
                .andExpect(status().isCreated());

        verify(userService).addUser(eq(newUser));
    }

    @Test
    @DisplayName("Тест обновления пользователя")
    void testUpdateUser() throws Exception {
        UserDTO updatedUserDTO = new UserDTO(1, "Jane Doe", "4321", UserRole.CLIENT);
        User updatedUser = new User(1, "Jane Doe", "4321", UserRole.CLIENT);

        when(userMapper.userDTOToUser(any(UserDTO.class))).thenReturn(updatedUser);
        when(userService.getUserById(1)).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedUserDTO)))
                .andExpect(status().isOk());

        verify(userService, times(1)).updateUser(1, updatedUser);
    }

    @Test
    @DisplayName("Тест обновления пользователя с ошибкой")
    void testUpdateUserInternalServerError() throws Exception {
        UserDTO userDTO = new UserDTO(1, "Jane Doe", "43211", UserRole.CLIENT);
        doThrow(new RuntimeException("Внутренняя ошибка")).when(userService).updateUser(eq(999), any(User.class));

        mockMvc.perform(put("/api/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userDTO)))
                .andExpect(status().isNotFound());
    }
}