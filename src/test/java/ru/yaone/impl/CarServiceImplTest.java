package ru.yaone.impl;

import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import ru.yaone.dto.CarDTO;
import ru.yaone.model.enumeration.CarCondition;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("Тестирование сервиса автомобилей")
public class CarServiceImplTest {

    private static PostgreSQLContainer<?> postgresContainer;
    private CarServiceImpl carService;

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
        carService = new CarServiceImpl();
        initDatabase();
    }

    private void initDatabase() {
        String createSchemaSQL = "CREATE SCHEMA IF NOT EXISTS car_shop;";
        String createTableSQL = """
                CREATE TABLE IF NOT EXISTS car_shop.cars (
                id SERIAL PRIMARY KEY,
                make VARCHAR(255),
                model VARCHAR(255),
                year INT,
                price DOUBLE PRECISION,
                condition VARCHAR(255)
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
    public static void tearDown() {
        postgresContainer.stop();
    }

    @Test
    @DisplayName("Добавление нового автомобиля")
    public void testAddCar() {
        CarDTO newCar = new CarDTO(0, "Toyota", "Corolla", 2022, 20000, CarCondition.NEW);
        carService.addCar(newCar);
        List<CarDTO> cars = carService.getAllCars();
        assertThat(cars).isNotEmpty();
    }

    @Test
    @DisplayName("Получение списка всех автомобилей")
    public void testGetAllCars() {
        carService.addCar(new CarDTO(0, "Toyota", "Corolla", 2022, 20000, CarCondition.NEW));
        carService.addCar(new CarDTO(0, "Honda", "Civic", 2023, 22000, CarCondition.NEW));
        List<CarDTO> cars = carService.getAllCars();
        assertThat(cars).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("Получение автомобиля по ID")
    public void testGetCarById() {
        carService.addCar(new CarDTO(0, "Toyota", "Corolla", 2022, 20000, CarCondition.NEW));
        List<CarDTO> cars = carService.getAllCars();
        int carId = cars.get(0).getId();
        CarDTO retrievedCar = carService.getCarById(carId);
        Assertions.assertNotNull(retrievedCar);
        Assertions.assertEquals("Toyota", retrievedCar.getMake());
    }
}