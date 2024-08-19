package ru.yaone.services;

import ru.yaone.dto.OrderDTO;

import java.util.List;

/**
 * Интерфейс для сервиса управления заказами.
 * <p>
 * Этот интерфейс предоставляет методы для добавления,
 * получения, обновления и удаления заказов, а также
 * для поиска заказов по различным критериям.
 * </p>
 */
public interface OrderService {

    /**
     * Добавляет новый заказ в систему.
     *
     * @param orderDTO объект заказа, который необходимо добавить
     */
    void addOrder(OrderDTO orderDTO);

    /**
     * Получает список всех заказов в системе.
     *
     * @return список всех заказов
     */
    List<OrderDTO> getAllOrders();

    /**
     * Получает заказ по его идентификатору.
     *
     * @param id идентификатор заказа
     * @return объект заказа с указанным идентификатором или null,
     * если заказ не найден
     */
    OrderDTO getOrderById(int id);

    /**
     * Обновляет информацию о заказе.
     *
     * @param id              идентификатор заказа, который необходимо обновить
     * @param updatedOrderDTO объект заказа с обновлённой информацией
     */
    void updateOrder(int id, OrderDTO updatedOrderDTO);

    /**
     * Удаляет заказ из системы по его идентификатору.
     *
     * @param id идентификатор заказа, который необходимо удалить
     */
    boolean deleteOrderById(int id);
}