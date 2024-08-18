package ru.yaone.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yaone.model.enumeration.CarCondition;
import ru.yaone.validator.CarDTOValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CarDTOValidatorTest {

    @Test
    @DisplayName("Тест: валидный объект CarDTO")
    public void testValidCarDTO() {
        CarDTO carDTO = new CarDTO(1, "Toyota", "Camry", 2022, 30000.0, CarCondition.NEW);
        List<String> validationResult = CarDTOValidator.validate(carDTO);
        assertTrue(validationResult.isEmpty(), "Validation should pass for valid DTO.");
    }

    @Test
    @DisplayName("Тест: ID не положительный")
    public void testInvalidId() {
        CarDTO carDTO = new CarDTO(-1, "Toyota", "Camry", 2022, 30000.0, CarCondition.NEW);
        List<String> validationResult = CarDTOValidator.validate(carDTO);
        assertTrue(validationResult.contains("ID should be positive"), "Validation should fail for non-positive ID.");
    }

    @Test
    @DisplayName("Тест: пустое название марки")
    public void testEmptyMake() {
        CarDTO carDTO = new CarDTO(1, "", "Camry", 2022, 30000.0, CarCondition.NEW);
        List<String> validationResult = CarDTOValidator.validate(carDTO);
        assertTrue(validationResult.contains("Make is mandatory"), "Validation should fail for empty make.");
    }

    @Test
    @DisplayName("Тест: пустая модель")
    public void testEmptyModel() {
        CarDTO carDTO = new CarDTO(1, "Toyota", "", 2022, 30000.0, CarCondition.NEW);
        List<String> validationResult = CarDTOValidator.validate(carDTO);
        assertTrue(validationResult.contains("Model is mandatory"), "Validation should fail for empty model.");
    }

    @Test
    @DisplayName("Тест: год выпуска не положительный")
    public void testInvalidYear() {
        CarDTO carDTO = new CarDTO(1, "Toyota", "Camry", -2022, 30000.0, CarCondition.NEW);
        List<String> validationResult = CarDTOValidator.validate(carDTO);
        assertTrue(validationResult.contains("Year should be positive"), "Validation should fail for non-positive year.");
    }

    @Test
    @DisplayName("Тест: цена не положительная")
    public void testInvalidPrice() {
        CarDTO carDTO = new CarDTO(1, "Toyota", "Camry", 2022, -30000.0, CarCondition.NEW);
        List<String> validationResult = CarDTOValidator.validate(carDTO);
        assertTrue(validationResult.contains("Price should be positive"), "Validation should fail for non-positive price.");
    }

    @Test
    @DisplayName("Тест: состояние автомобиля - null")
    public void testNullCondition() {
        CarDTO carDTO = new CarDTO(1, "Toyota", "Camry", 2022, 30000.0, null);
        List<String> validationResult = CarDTOValidator.validate(carDTO);
        assertTrue(validationResult.contains("Car condition cannot be null"), "Validation should fail for null condition.");
    }
}