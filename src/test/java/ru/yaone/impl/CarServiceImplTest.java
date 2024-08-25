package ru.yaone.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.yaone.manager.DatabaseConnectionManager;
import ru.yaone.model.Car;
import ru.yaone.model.enumeration.CarCondition;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CarServiceImplTest {

    @Mock
    private DatabaseConnectionManager databaseConnectionManager;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private Statement mockStatement;

    @Mock
    private ResultSet mockResultSet;

    private CarServiceImpl carServiceImpl;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(databaseConnectionManager.getConnection()).thenReturn(mockConnection);
        carServiceImpl = new CarServiceImpl(databaseConnectionManager);
    }

    @Test
    @DisplayName("Тест метода добавления автомобиля")
    void testAddCar() throws Exception {
        Car car = new Car(0, "Toyota", "Corolla", 2022, 20000.0, CarCondition.NEW);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(1);

        carServiceImpl.addCar(car);

        verify(mockPreparedStatement, times(1)).setString(1, car.make());
        verify(mockPreparedStatement, times(1)).setString(2, car.model());
        verify(mockPreparedStatement, times(1)).setInt(3, car.year());
        verify(mockPreparedStatement, times(1)).setDouble(4, car.price());
        verify(mockPreparedStatement, times(1)).setString(5, car.condition().toString());
    }

    @Test
    @DisplayName("Тест метода получения всех автомобилей")
    void testGetAllCars() throws Exception {
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);  // Simulate one result
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("make")).thenReturn("Toyota");
        when(mockResultSet.getString("model")).thenReturn("Corolla");
        when(mockResultSet.getInt("year")).thenReturn(2022);
        when(mockResultSet.getDouble("price")).thenReturn(20000.0);
        when(mockResultSet.getString("condition")).thenReturn("NEW");

        List<Car> cars = carServiceImpl.getAllCars();

        assertThat(cars).isNotNull().hasSize(1);
        assertThat(cars.get(0).make()).isEqualTo("Toyota");
    }

    @Test
    @DisplayName("Тест метода получения автомобиля по ID")
    void testGetCarById() throws Exception {
        int carId = 1;

        when(mockConnection.prepareStatement(anyString(), anyInt())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(carId);
        when(mockResultSet.getString("make")).thenReturn("Toyota");
        when(mockResultSet.getString("model")).thenReturn("Corolla");
        when(mockResultSet.getInt("year")).thenReturn(2022);
        when(mockResultSet.getDouble("price")).thenReturn(20000.0);
        when(mockResultSet.getString("condition")).thenReturn("NEW");

        Car car = carServiceImpl.getCarById(carId);

        assertThat(car).isNotNull();
        assertThat(car.make()).isEqualTo("Toyota");
    }

    @Test
    @DisplayName("Тест метода получения автомобиля по ID если автомобиль не найден")
    void testGetCarById_NotFound() throws Exception {
        int carId = 1;

        when(mockConnection.prepareStatement(anyString(), anyInt())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // Simulate no result

        Car car = carServiceImpl.getCarById(carId);

        assertThat(car).isNull();
    }
}
