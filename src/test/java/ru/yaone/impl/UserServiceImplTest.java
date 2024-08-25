package ru.yaone.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.yaone.constants.SqlScriptsForUsers;
import ru.yaone.manager.DatabaseConnectionManager;
import ru.yaone.model.User;
import ru.yaone.model.enumeration.UserRole;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    @Mock
    private DatabaseConnectionManager mockConnectionManager;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private Statement mockStatement;

    @Mock
    private ResultSet mockResultSet;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mockConnectionManager.getConnection()).thenReturn(mockConnection);
    }

    @Test
    @DisplayName("Тест для метода addUser когда пользователь уже существует")
    void testAddUserAlreadyExists() throws SQLException {
        User user = new User(1, "existingUser", "password", UserRole.CLIENT);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(1);

        assertThrows(RuntimeException.class, () -> userService.addUser(user));
    }

    @Test
    @DisplayName("Тест для метода addUser, который вызывает isUsernameTaken когда имя пользователя занято")
    void testAddUserThrowsExceptionWhenUsernameTaken() throws SQLException {
        User user = new User(1, "existingUser", "password", UserRole.CLIENT);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(1);
        assertThrows(RuntimeException.class, () -> userService.addUser(user),
                "Пользователь с именем " + user.username() + " уже существует.");
    }

    @Test
    @DisplayName("Тест для метода getAllUsers")
    void testGetAllUsers() throws SQLException {
        List<User> expectedUsers = new ArrayList<>();
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("username")).thenReturn("testUser");
        when(mockResultSet.getString("password")).thenReturn("password");
        when(mockResultSet.getString("role")).thenReturn("CLIENT");

        User user = new User(1, "testUser", "password", UserRole.CLIENT);
        expectedUsers.add(user);

        List<User> users = userService.getAllUsers();

        assertEquals(1, users.get(0).id());
        assertEquals("testUser", users.get(0).username());
        assertEquals("password", users.get(0).password());
    }

    @Test
    @DisplayName("Тест для метода getUserById")
    void testGetUserById() throws SQLException {
        int userId = 1;
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(userId);
        when(mockResultSet.getString("username")).thenReturn("testUser");
        when(mockResultSet.getString("password")).thenReturn("password");
        when(mockResultSet.getString("role")).thenReturn("CLIENT");

        User actualUser = userService.getUserById(userId);
        User expectedUser = new User(userId, "testUser", "password", UserRole.CLIENT);

        assertEquals(expectedUser, actualUser);
    }
}