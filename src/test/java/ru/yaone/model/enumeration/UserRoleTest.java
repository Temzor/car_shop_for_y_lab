package ru.yaone.model.enumeration;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

class UserRoleTest {

    @Test
    void testUserRoleValues() {
        UserRole[] expectedValues = {UserRole.ADMIN, UserRole.MANAGER, UserRole.CLIENT};
        assertThat(UserRole.values()).containsExactly(expectedValues);
    }

    @Test
    void testFromValue() {
        assertThat(UserRole.fromValue(1)).isEqualTo(UserRole.ADMIN);
        assertThat(UserRole.fromValue(2)).isEqualTo(UserRole.MANAGER);
        assertThat(UserRole.fromValue(3)).isEqualTo(UserRole.CLIENT);
    }

    @Test
    void testFromValueWithInvalidValue() {
        assertThatThrownBy(() -> UserRole.fromValue(4))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Некорректное значение роли: 4");
    }
}