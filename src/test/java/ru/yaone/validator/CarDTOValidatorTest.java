package ru.yaone.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yaone.dto.CarDTO;
import ru.yaone.model.enumeration.CarCondition;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class CarDTOValidatorTest {

    @Test
    @DisplayName("Тест: валидный объект OrderDTO")
    public void testValidCarDTO() {
        CarDTO carDTO = new CarDTO(1, "Ford", "Mustang", 2023, 50000.00, CarCondition.NEW);
        List<String> validationResult = CarDTOValidator.validate(carDTO);
        assertTrue(validationResult.isEmpty(), "Validation should pass for valid DTO.");
    }

    @Test
    @DisplayName("Тест: отрицательный ID автомобиля")
    public void testNegativeCarId() {
        CarDTO carDTO = new CarDTO(-1, "Ford", "Mustang", 2023, 50000.00, CarCondition.NEW);
        List<String> validationResult = CarDTOValidator.validate(carDTO);
        assertFalse(validationResult.contains("ID cannot be null and should be positive"), "Validation should fail for negative car ID.");
    }

    @Test
    @DisplayName("Тест: нулевое состояние автомобиля")
    public void testNullCarStatus() {
        CarDTO carDTO = new CarDTO(1, "Ford", "Mustang", 2023, 50000.00, null);
        List<String> validationResult = CarDTOValidator.validate(carDTO);
        assertFalse(validationResult.contains("Car condition cannot be null"), "Validation should fail for null car condition.");
    }

    @Test
    @DisplayName("Тест: нулевая марка автомобиля")
    public void testNullMakeCar() {
        CarDTO carDTO = new CarDTO(1, null, "Mustang", 2023, 50000.00, null);
        List<String> validationResult = CarDTOValidator.validate(carDTO);
        assertFalse(validationResult.contains("Make cannot be null"), "Validation should fail for null make.");
    }

    @Test
    @DisplayName("Тест: нулевая модель автомобиля")
    public void testNullModelCar() {
        CarDTO carDTO = new CarDTO(1, "Ford", null, 2023, 50000.00, null);
        List<String> validationResult = CarDTOValidator.validate(carDTO);
        assertFalse(validationResult.contains("Model cannot be null"), "Validation should fail for null model.");
    }

    @Test
    @DisplayName("Тест: год производства автомобиля > 1830")
    public void testNullYearCar() {
        CarDTO carDTO = new CarDTO(1, "Ford", "Mustang", 1829, 50000.00, null);
        List<String> validationResult = CarDTOValidator.validate(carDTO);
        assertFalse(validationResult.contains("Year cannot be less than 1830"), "Validation should fail for less than 1830.");
    }

    @Test
    @DisplayName("Тест: цена автомобиля не может быть меньше нуля")
    public void testNullPriceCar() {
        CarDTO carDTO = new CarDTO(1, "Ford", "Mustang", 2021, -100, null);
        List<String> validationResult = CarDTOValidator.validate(carDTO);
        assertFalse(validationResult.contains("Price cannot be less than 0"), "Validation should fail for less than 0.");
    }
}
