package ru.yaone.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import ru.yaone.model.enumeration.CarCondition;

/**
 * Класс {@code CarDTO} представляет собой Data Transfer Object (DTO) для автомобиля.
 *
 * <p>Содержит информацию о автомобиле, включая его идентификатор, марку, модель,
 * год выпуска, цену и состояние, что позволяет передавать данные между слоями приложения.</p>
 *
 * <p>Класс использует аннотации для валидации полей, чтобы гарантировать, что данные,
 * передаваемые в систему, соответствуют определенным требованиям.</p>
 *
 * @author Ваше имя
 * @version 1.0
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CarDTO {

    /**
     * Уникальный идентификатор автомобиля.
     *
     * <p>Не должен быть {@code null} и должен быть положительным числом.</p>
     */
    @NotNull(message = "ID cannot be null")
    @Positive(message = "ID should be positive")
    private int id;

    /**
     * Марка автомобиля.
     *
     * <p>Не должна быть пустой (не может быть {@code null} или состоять из пробелов).</p>
     */
    @NotBlank(message = "Make is mandatory")
    private String make;

    /**
     * Модель автомобиля.
     *
     * <p>Не должна быть пустой (не может быть {@code null} или состоять из пробелов).</p>
     */
    @NotBlank(message = "Model is mandatory")
    private String model;

    /**
     * Год выпуска автомобиля.
     *
     * <p>Не должен быть {@code null} и должен быть положительным числом.</p>
     */
    @NotNull(message = "Year cannot be null")
    @Positive(message = "Year should be positive")
    private int year;

    /**
     * Цена автомобиля.
     *
     * <p>Не должна быть {@code null} и должна быть положительным числом.</p>
     */
    @NotNull(message = "Price cannot be null")
    @Positive(message = "Price should be positive")
    private double price;

    /**
     * Состояние автомобиля.
     *
     * <p>Не должно быть {@code null}.</p>
     */
    @NotNull(message = "Car condition cannot be null")
    private CarCondition condition;
}