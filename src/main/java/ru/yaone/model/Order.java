package ru.yaone.model;

import ru.yaone.model.enumeration.OrderStatus;

import java.time.Instant;

/**
 * Запись (record), представляющая заказ.
 * <p>
 * Данный класс хранит информацию о заказе, включая
 * идентификатор заказа, клиента, автомобиль, дату создания
 * заказа и статус заказа. Используется для управления
 * заказами в системе.
 * </p>
 *
 * @param id           уникальный идентификатор заказа
 * @param clientId     id объекта клиента, связанный с заказом
 * @param carId        id объекта автомобиля, связанного с заказом
 * @param creationDate дата и время создания заказа
 * @param status       статус заказа, представленный значением из
 *                     перечисления {@link OrderStatus}
 */
public record Order(int id, int clientId, int carId, Instant creationDate, OrderStatus status) {
}