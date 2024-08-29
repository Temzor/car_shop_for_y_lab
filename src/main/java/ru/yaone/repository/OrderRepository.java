package ru.yaone.repository;

import ru.yaone.model.Order;

import java.util.List;

public interface OrderRepository {

    void addOrder(Order order);

    List<Order> getAllOrders();

    Order getOrderById(int id);

    void updateOrder(int id, Order updatedOrder);

    boolean deleteOrder(int id);
}