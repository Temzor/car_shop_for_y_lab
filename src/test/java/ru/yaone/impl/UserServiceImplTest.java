package ru.yaone.impl;

import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import ru.yaone.dto.UserDTO;
import ru.yaone.model.enumeration.UserRole;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@DisplayName("Тестирование сервиса пользователей")
public class UserServiceImplTest {

    private static PostgreSQLContainer<?> postgresContainer;
    private UserServiceImpl userService;

    @BeforeAll
    public static void setupContainer() {
        postgresContainer = new PostgreSQLContainer<>("postgres:14.13")
                .withDatabaseName("car_shop")
                .withUsername("test")
                .withPassword("test");
        postgresContainer.start();
    }

    @BeforeEach
    public void setup() {
        userService = new UserServiceImpl();
        initDatabase();
    }

    private void initDatabase() {
        String createSchemaSQL = "CREATE SCHEMA IF NOT EXISTS car_shop;";
        String createTableSQL = """
                CREATE TABLE IF NOT EXISTS car_shop.users (
                id SERIAL PRIMARY KEY,
                username VARCHAR(255) UNIQUE,
                password VARCHAR(255),
                role VARCHAR(255)
                );
                """;
        try (Connection conn = DriverManager.getConnection(
                postgresContainer.getJdbcUrl(),
                postgresContainer.getUsername(),
                postgresContainer.getPassword());
             Statement stmt = conn.createStatement()) {
            stmt.execute(createSchemaSQL);
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при инициализации базы данных", e);
        }
    }

    @AfterAll
    public static void tearDownContainer() {
        postgresContainer.stop();
    }

    @Test
    @DisplayName("Проверка на выброс исключения при добавлении существующего пользователя")
    public void testThrow() {
        UserDTO newUser = new UserDTO(0, "alice", "mypassword", UserRole.CLIENT);
        assertThatThrownBy(() -> userService.addUser(newUser))
                .isInstanceOf(RuntimeException.class);
    }
}