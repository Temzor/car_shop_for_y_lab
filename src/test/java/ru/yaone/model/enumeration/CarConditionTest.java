package ru.yaone.model.enumeration;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class CarConditionTest {

    @Test
    void testCarConditionValues() {
        CarCondition[] conditions = CarCondition.values();

        assertThat(conditions).containsExactly(CarCondition.NEW, CarCondition.USED, CarCondition.DAMAGED);
    }

    @Test
    void testValueOf() {
        CarCondition condition = CarCondition.valueOf("NEW");

        assertThat(condition).isEqualTo(CarCondition.NEW);
    }
}