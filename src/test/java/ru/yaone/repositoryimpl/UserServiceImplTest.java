package ru.yaone.repositoryimpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.yaone.model.User;
import ru.yaone.model.enumeration.UserRole;
import ru.yaone.repository.UserRepository;
import ru.yaone.serviceimpl.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Тесты для класса UserServiceImpl")
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Тест для метода getAllUsers")
    void testGetAllUsers() {
        List<User> expectedUsers = new ArrayList<>();
        User user = new User(1, "testUser", "password", UserRole.CLIENT);
        expectedUsers.add(user);
        when(userRepository.getAllUsers()).thenReturn(expectedUsers);

        List<User> users = userService.getAllUsers();

        assertFalse(users.isEmpty());
        assertEquals(1, users.get(0).id());
        assertEquals("testUser", users.get(0).username());
        assertEquals("password", users.get(0).password());
        assertEquals(UserRole.CLIENT, users.get(0).role());
    }

    @Test
    @DisplayName("Тест для метода getUserById")
    void testGetUserById() {
        int userId = 1;
        User user = new User(userId, "testUser", "password", UserRole.CLIENT);
        when(userRepository.getUserById(userId)).thenReturn(user);

        User resultUser = userService.getUserById(userId);

        assertNotNull(resultUser);
        assertEquals(userId, resultUser.id());
        assertEquals("testUser", resultUser.username());
        assertEquals("password", resultUser.password());
        assertEquals(UserRole.CLIENT, resultUser.role());
    }
}