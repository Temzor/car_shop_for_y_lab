package ru.yaone.servlets;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ru.yaone.dto.UserDTO;

import ru.yaone.impl.UserServiceImpl;
import ru.yaone.model.enumeration.UserRole;
import ru.yaone.services.UserService;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;


import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class UserServletTest {
    private UserServlet userServlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private PrintWriter writerMock;
    private UserService userServiceMock;

    @BeforeEach
    public void setUp() throws IOException {
        userServlet = new UserServlet();
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        writerMock = mock(PrintWriter.class);
        userServiceMock = mock(UserServiceImpl.class);
        when(response.getWriter()).thenReturn(writerMock);
        userServlet.setUserService(userServiceMock);
    }

    @Test
    @DisplayName("Тест получения всех пользователей")
    public void testDoGetAllUsers() throws ServletException, IOException {
        UserDTO mockUser = new UserDTO();
        mockUser.setUsername("testuser");
        mockUser.setPassword("password");
        when(userServiceMock.getAllUsers()).thenReturn(Collections.singletonList(mockUser));
        userServlet.doGet(request, response);
        verify(response).setContentType("application/json; charset=UTF-8");
        verify(userServiceMock).getAllUsers();
        verify(writerMock).print(contains("testuser"));
    }

    @Test
    @DisplayName("Тест получения пользователя по некорректному ID")
    public void testDoGetUserByIdInvalidIdFormat() throws Exception {
        when(request.getPathInfo()).thenReturn("/invalid");
        userServlet.doGet(request, response);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(writerMock).print("{\"error\":\"Invalid user ID format\"}");
    }

    @Test
    @DisplayName("Тест получения пользователя по ID")
    public void testDoGetClientById() throws Exception {
        UserServiceImpl userService = mock(UserServiceImpl.class);
        userServlet.setUserService(userService);
        UserDTO mockUser = new UserDTO(1, "User", "+987654321", UserRole.CLIENT);
        when(userService.getUserById(1)).thenReturn(mockUser);
        when(request.getPathInfo()).thenReturn("/1");
        when(response.getWriter()).thenReturn(new PrintWriter(new ByteArrayOutputStream()));
        userServlet.doGet(request, response);
        verify(response).setContentType("application/json; charset=UTF-8");
        verify(userService).getUserById(1);
    }

    @Test
    @DisplayName("Тест POST запроса с некорректным форматом JSON")
    public void testDoPostInvalidJsonFormat() throws Exception {
        when(request.getInputStream()).thenThrow(new IOException("Invalid JSON"));
        when(response.getWriter()).thenReturn(writerMock);
        assertThatThrownBy(() -> userServlet.doPost(request, response))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("Invalid JSON");
    }

    @Test
    @DisplayName("Тест PUT запроса без указания ID пользователя")
    public void testDoPutUserWithoutId() throws IOException {
        when(request.getPathInfo()).thenReturn(null);
        userServlet.doPut(request, response);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(writerMock).print("{\"error\":\"User ID must be specified\"}");
    }
}