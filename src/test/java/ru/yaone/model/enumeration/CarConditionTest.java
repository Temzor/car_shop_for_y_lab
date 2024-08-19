package ru.yaone.model.enumeration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CarConditionTest {

    @Test
    @DisplayName("Проверка значений состояний автомобиля")
    void testCarConditionValues() {
        CarCondition[] conditions = CarCondition.values();
        assertThat(conditions).containsExactly(CarCondition.NEW, CarCondition.USED, CarCondition.DAMAGED);
    }

    @Test
    @DisplayName("Проверка метода valueOf для состояния NEW")
    void testValueOf() {
        CarCondition condition = CarCondition.valueOf("NEW");
        assertThat(condition).isEqualTo(CarCondition.NEW);
    }
}