package ru.yaone.model.enumeration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderStatusTest {

    @Test
    @DisplayName("Проверка значений статусов заказов")
    void testOrderStatusValues() {
        OrderStatus[] expectedValues = {OrderStatus.PENDING, OrderStatus.APPROVED, OrderStatus.REJECTED};
        assertThat(OrderStatus.values()).containsExactly(expectedValues);
    }
}