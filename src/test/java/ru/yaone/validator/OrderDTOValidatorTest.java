package ru.yaone.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yaone.dto.OrderDTO;
import ru.yaone.model.enumeration.OrderStatus;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderDTOValidatorTest {

    @Test
    @DisplayName("Тест: валидный объект OrderDTO")
    public void testValidOrderDTO() {
        OrderDTO orderDTO = new OrderDTO(1, 1, 1, Instant.now(), OrderStatus.PENDING);
        List<String> validationResult = OrderDTOValidator.validate(orderDTO);
        assertTrue(validationResult.isEmpty(), "Validation should pass for valid DTO.");
    }

    @Test
    @DisplayName("Тест: отрицательный ID заказа")
    public void testNegativeOrderId() {
        OrderDTO orderDTO = new OrderDTO(-1, 1, 1, Instant.now(), OrderStatus.PENDING);
        List<String> validationResult = OrderDTOValidator.validate(orderDTO);
        assertTrue(validationResult.contains("ID cannot be null and should be positive"), "Validation should fail for negative order ID.");
    }

    @Test
    @DisplayName("Тест: нулевой статус заказа")
    public void testNullOrderStatus() {
        OrderDTO orderDTO = new OrderDTO(1, 1, 1, Instant.now(), null);
        List<String> validationResult = OrderDTOValidator.validate(orderDTO);
        assertTrue(validationResult.contains("Order status cannot be null"), "Validation should fail for null order status.");
    }

    @Test
    @DisplayName("Тест: нулевая дата создания")
    public void testNullCreationDate() {
        OrderDTO orderDTO = new OrderDTO(1, 1, 1, null, OrderStatus.PENDING);
        List<String> validationResult = OrderDTOValidator.validate(orderDTO);
        assertTrue(validationResult.contains("Date cannot be null"), "Validation should fail for null creation date.");
    }
}