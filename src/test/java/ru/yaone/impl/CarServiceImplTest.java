package ru.yaone.impl;

import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import ru.yaone.model.Car;
import ru.yaone.model.enumeration.CarCondition;
import static org.assertj.core.api.Assertions.assertThat;

import java.sql.*;
import java.util.List;

public class CarServiceImplTest {

    private static PostgreSQLContainer<?> postgresContainer;
    private CarServiceImpl carService;


    @BeforeAll
    public static void setupContainer() {
        // Настройка контейнера PostgreSQL
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
    public static void tearDownContainer() {
        postgresContainer.stop();
    }

    @Test
    public void testAddCar() {
        Car newCar = new Car(0, "Toyota", "Corolla", 2022, 20000, CarCondition.NEW);
        carService.addCar(newCar);
        List<Car> cars = carService.getAllCars();
        assertThat(cars).isNotEmpty();
    }

    @Test
    public void testGetAllCars() {
        carService.addCar(new Car(0, "Toyota", "Corolla", 2022, 20000, CarCondition.NEW));
        carService.addCar(new Car(0, "Honda", "Civic", 2023, 22000, CarCondition.NEW));
        List<Car> cars = carService.getAllCars();
        assertThat(cars).isNotNull()
                .isNotEmpty();
    }

    @Test
    public void testGetCarById() {
        carService.addCar(new Car(0, "Toyota", "Corolla", 2022, 20000, CarCondition.NEW));
        List<Car> cars = carService.getAllCars();
        int carId = cars.get(0).id();
        Car retrievedCar = carService.getCarById(carId);
        Assertions.assertNotNull(retrievedCar);
        Assertions.assertEquals("Toyota", retrievedCar.make());
    }

    @Test
    public void testUpdateCar() {
        carService.addCar(new Car(0, "Toyota", "Corolla", 2022, 21000, CarCondition.NEW));
        List<Car> cars = carService.getAllCars();
        int carId = cars.get(0).id();
        Car updatedCar = new Car(carId, "Toyota", "Corolla", 2022, 20000, CarCondition.NEW);

        carService.updateCar(updatedCar.id(), updatedCar);

        Car retrievedCar = carService.getCarById(carId);
        Assertions.assertEquals(21000, retrievedCar.price());
    }
}