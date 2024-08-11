package ru.yaone.impl;

import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import ru.yaone.model.AuditLog;
import ru.yaone.model.User;
import ru.yaone.model.enumeration.UserRole;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AuditServiceImplTest {

    private static PostgreSQLContainer<?> postgresContainer;
    private AuditServiceImpl auditService;

    @BeforeAll
    public static void setUp() {
        // Настраиваем и запускаем контейнер PostgreSQL
        postgresContainer = new PostgreSQLContainer<>("postgres:14.13")
                .withDatabaseName("car_shop_test")
                .withUsername("test")
                .withPassword("test");
        postgresContainer.start();
    }

    @BeforeEach
    public void init() {
        auditService = new AuditServiceImpl();
        initializeDatabase(); // Инициализация базы данных перед каждым тестом
    }

    @AfterAll
    public static void tearDown() {
        // Остановка контейнера PostgreSQL
        if (postgresContainer != null) {
            postgresContainer.stop();
        }
    }

    private void initializeDatabase() {
        // Создаем необходимые таблицы и добавляем тестовые данные
        try (Connection conn = DriverManager.getConnection(
                postgresContainer.getJdbcUrl(),
                postgresContainer.getUsername(),
                postgresContainer.getPassword());
             Statement stmt = conn.createStatement()) {

            // Создаем схему, если она не существует
            stmt.execute("CREATE SCHEMA IF NOT EXISTS car_shop;");

            // Создаем таблицы в схеме car_shop
            stmt.execute("CREATE TABLE IF NOT EXISTS car_shop.users (id SERIAL PRIMARY KEY, username VARCHAR(50), password VARCHAR(50), role VARCHAR(50));");
            stmt.execute("CREATE TABLE IF NOT EXISTS car_shop.audit_logs (user_id INT, log_message TEXT, log_date TIMESTAMP);");

            // Добавление тестового пользователя
            stmt.execute("INSERT INTO car_shop.users (username, password, role) VALUES ('testUser', 'testPass', 'USER');");


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testLogAction() {
        User testUser = new User(1, "testUser", "testPass", UserRole.ADMIN);
        String action = "Test action";

        auditService.logAction(testUser, action);

        List<AuditLog> logs = auditService.getAllLogs();
        Assertions.assertFalse(logs.isEmpty());
        Assertions.assertEquals(action, logs.get(0).action());
        Assertions.assertEquals(testUser.id(), logs.get(0).user().id());
    }

    @Test
    public void testDeletedLogAction() {
        User testUser = new User(1, "testUser", "testPass", UserRole.ADMIN);
        String action = "Test action";

        auditService.logAction(testUser, action);

        List<AuditLog> logs = auditService.getAllLogs();
        Assertions.assertFalse(logs.isEmpty());
        Assertions.assertEquals(action, logs.get(0).action());
        Assertions.assertEquals(testUser.id(), logs.get(0).user().id());
    }

    @Test
    public void testGetAllLogs() {
        User testUser = new User(1, "testUser", "testPass", UserRole.MANAGER);
        auditService.logAction(testUser, "First action");
        auditService.logAction(testUser, "Second action");

        List<AuditLog> logs = auditService.getAllLogs();

        assertThat(logs).isNotEmpty().isNotNull();
    }
}