package ru.yaone.serviceimpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yaone.aspect.annotation.Loggable;
import ru.yaone.model.Order;
import ru.yaone.repository.OrderRepository;
import ru.yaone.services.OrderService;

import java.util.List;


@Service
@Loggable("Логирование класса OrderServiceImpl")
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

    @Loggable("Логирование метода OrderServiceImpl.addOrder")
    @Override
    public void addOrder(Order order) {
        orderRepository.addOrder(order);
    }

    @Loggable("Логирование метода OrderServiceImpl.getAllOrders")
    @Override
    public List<Order> getAllOrders() {
        return orderRepository.getAllOrders();
    }

    @Loggable("Логирование метода OrderServiceImpl.getOrderById")
    @Override
    public Order getOrderById(int id) {
        return orderRepository.getOrderById(id);
    }

    @Loggable("Логирование метода OrderServiceImpl.updateOrder")
    @Override
    public void updateOrder(int id, Order updatedOrder) {
        orderRepository.updateOrder(id, updatedOrder);
    }

    @Loggable("Логирование метода OrderServiceImpl.deleteOrderById")
    @Override
    public boolean deleteOrder(int id) {
        return orderRepository.deleteOrder(id);
    }
}