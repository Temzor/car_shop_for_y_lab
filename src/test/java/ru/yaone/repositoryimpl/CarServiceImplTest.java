package ru.yaone.repositoryimpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.yaone.model.Car;
import ru.yaone.model.enumeration.CarCondition;
import ru.yaone.repository.CarRepository;
import ru.yaone.serviceimpl.CarServiceImpl;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CarServiceImplTest {
    @Mock
    private CarRepository carRepository;

    private CarServiceImpl carServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        carServiceImpl = new CarServiceImpl(carRepository);
    }

    @Test
    @DisplayName("Тест метода добавления автомобиля")
    void testAddCar() {
        Car car = new Car(1, "Toyota", "Corolla", 2022, 20000.0, CarCondition.NEW);
        doNothing().when(carRepository).addCar(car);

        carServiceImpl.addCar(car);

        verify(carRepository, times(1)).addCar(car);
    }

    @Test
    @DisplayName("Тест метода получения всех автомобилей")
    void testGetAllCars() {
        Car car = new Car(1, "Toyota", "Corolla", 2022, 20000.0, CarCondition.NEW);
        when(carRepository.getAllCars()).thenReturn(List.of(car));

        List<Car> cars = carServiceImpl.getAllCars();

        assertThat(cars).isNotNull().hasSize(1);
        assertThat(cars.get(0).make()).isEqualTo("Toyota");
    }

    @Test
    @DisplayName("Тест метода получения автомобиля по ID")
    void testGetCarById() {
        int carId = 1;
        Car car = new Car(carId, "Toyota", "Corolla", 2022, 20000.0, CarCondition.NEW);
        when(carRepository.getCarById(carId)).thenReturn(car);

        Car resultCar = carServiceImpl.getCarById(carId);

        assertThat(resultCar).isNotNull();
        assertThat(resultCar.make()).isEqualTo("Toyota");
    }

    @Test
    @DisplayName("Тест метода получения автомобиля по ID, если автомобиль не найден")
    void testGetCarByIdNotFound() {
        int carId = 1;
        when(carRepository.getCarById(carId)).thenReturn(null);

        Car resultCar = carServiceImpl.getCarById(carId);

        assertThat(resultCar).isNull();
    }
}