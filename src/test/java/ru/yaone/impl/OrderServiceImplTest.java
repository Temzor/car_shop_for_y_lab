package ru.yaone.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yaone.model.Car;
import ru.yaone.model.Client;
import ru.yaone.model.Order;
import ru.yaone.model.User;
import ru.yaone.model.enumeration.CarCondition;
import ru.yaone.model.enumeration.OrderStatus;
import ru.yaone.model.enumeration.UserRole;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

class OrderServiceImplTest {

    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl();
    }

    @Test
    void addOrderShouldAddOrder() {
        Order order = new Order(1, new Client(new User(1, "Pikal", "122121", UserRole.ADMIN), "16-17-44-55"),
                new Car(1, "Amoda", "XO", 1999,
                        400_000.00, CarCondition.USED), LocalDateTime.now(),
                OrderStatus.PENDING);

        orderService.addOrder(order);

        List<Order> orders = orderService.getAllOrders();
        assertThat(orders).containsExactly(order);
    }

    @Test
    void getAllOrdersShouldReturnAllOrders() {
        Order order1 = new Order(1, new Client(new User(1, "Pikal", "122121", UserRole.ADMIN), "16-17-44-55"),
                new Car(1, "Amoda", "XO", 1999,
                        400_000.00, CarCondition.USED), LocalDateTime.now(),
                OrderStatus.PENDING);
        Order order2 = new Order(1, new Client(new User(1, "Pikal", "122121", UserRole.ADMIN), "16-17-44-55"),
                new Car(1, "Amoda", "XM", 2000,
                        4_000_000.00, CarCondition.USED), LocalDateTime.now(),
                OrderStatus.PENDING);

        orderService.addOrder(order1);
        orderService.addOrder(order2);

        List<Order> orders = orderService.getAllOrders();
        assertThat(orders).containsExactlyInAnyOrder(order1, order2);
    }

    @Test
    void getOrderByIdShouldReturnCorrectOrder() {
        Order order = new Order(1, new Client(new User(1, "Pikal", "122121", UserRole.ADMIN), "16-17-44-55"),
                new Car(1, "Amoda", "XO", 1999,
                        400_000.00, CarCondition.USED), LocalDateTime.now(),
                OrderStatus.PENDING);

        orderService.addOrder(order);

        Order retrievedOrder = orderService.getOrderById(1);
        assertThat(retrievedOrder).isEqualTo(order);
    }

    @Test
    void getOrderByIdShouldReturnNullIfOrderNotFound() {
        Order retrievedOrder = orderService.getOrderById(99);
        assertThat(retrievedOrder).isNull();
    }

    @Test
    void updateOrderShouldUpdateExistingOrder() {
        Order order = new Order(1, new Client(new User(1, "Pikal", "122121", UserRole.ADMIN), "16-17-44-55"),
                new Car(1, "Amoda", "XO", 1999,
                        400_000.00, CarCondition.USED), LocalDateTime.now(),
                OrderStatus.PENDING);
        orderService.addOrder(order);

        Order updatedOrder = new Order(1, new Client(new User(1, "Michail", "1414", UserRole.CLIENT), "16-17-44-55"),
                new Car(1, "Amoda", "XO", 1999,
                        400_000.00, CarCondition.USED), LocalDateTime.now(),
                OrderStatus.PENDING);
        orderService.updateOrder(1, updatedOrder);

        Order retrievedOrder = orderService.getOrderById(1);
        assertThat(retrievedOrder).isEqualTo(updatedOrder);
    }

    @Test
    void deleteOrderShouldRemoveOrder() {
        Order order = new Order(1, new Client(new User(1, "Pikal", "122121", UserRole.ADMIN), "16-17-44-55"),
                new Car(1, "Amoda", "XO", 1999,
                        400_000.00, CarCondition.USED), LocalDateTime.now(),
                OrderStatus.PENDING);
        orderService.addOrder(order);

        orderService.deleteOrder(1);

        assertThat(orderService.getAllOrders()).isEmpty();
    }

    @Test
    void deleteOrderShouldDoNothingIfOrderNotFound() {
        assertThatCode(() -> orderService.deleteOrder(99)).doesNotThrowAnyException();
    }
}