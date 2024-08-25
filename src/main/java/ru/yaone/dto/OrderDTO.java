package ru.yaone.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import ru.yaone.model.enumeration.OrderStatus;

import java.time.Instant;

/**
 * Класс {@code OrderDTO} представляет собой Data Transfer Object (DTO) для заказа.
 *
 * <p>Содержит информацию о заказе, включая его уникальный идентификатор,
 * идентификатор клиента, идентификатор автомобиля, дату создания заказа и статус заказа.
 * Этот класс используется для передачи данных между слоями приложения.</p>
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderDTO {

    /**
     * Уникальный идентификатор заказа.
     *
     * <p>Не должен быть {@code null} и должен быть положительным.</p>
     */
    @NotNull(message = "ID cannot be null")
    @Positive(message = "ID should be positive")
    private int id;

    /**
     * Уникальный идентификатор клиента, связанного с заказом.
     *
     * <p>Обязательное поле, не должно быть {@code null}.</p>
     */
    @NotNull(message = "Client ID is mandatory")
    private int clientId;

    /**
     * Уникальный идентификатор автомобиля, связанного с заказом.
     *
     * <p>Обязательное поле, не должно быть {@code null}.</p>
     */
    @NotNull(message = "Car ID is mandatory")
    private int carId;

    /**
     * Дата и время создания заказа.
     *
     * <p>Не должно быть {@code null}.</p>
     */
    @NotNull(message = "Date cannot be null")
    private Instant creationDate;

    /**
     * Статус заказа.
     *
     * <p>Не должен быть {@code null}.</p>
     */
    @NotNull(message = "Order status cannot be null")
    private OrderStatus status;
}