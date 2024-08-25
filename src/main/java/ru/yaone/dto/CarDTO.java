package ru.yaone.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import ru.yaone.model.enumeration.CarCondition;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CarDTO {
    /**
     * Уникальный идентификатор автомобиля.
     * <p>Не должен быть {@code null} и должен быть положительным числом.</p>
     */
    @NotNull(message = "ID cannot be null")
    @Positive(message = "ID should be positive")
    private Integer id; // Изменено с int на Integer

    /** Марка автомобиля. */
    @NotBlank(message = "Make is mandatory")
    private String make;

    /** Модель автомобиля. */
    @NotBlank(message = "Model is mandatory")
    private String model;

    /** Год выпуска автомобиля. */
    @NotNull(message = "Year cannot be null")
    @Positive(message = "Year should be positive")
    private int year;

    /** Цена автомобиля. */
    @NotNull(message = "Price cannot be null")
    @Positive(message = "Price should be positive")
    private double price;

    /** Состояние автомобиля. */
    @NotNull(message = "Car condition cannot be null")
    private CarCondition condition;
}