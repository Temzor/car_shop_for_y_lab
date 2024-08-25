package ru.yaone.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yaone.aspect.annotation.Loggable;
import ru.yaone.constants.SqlScriptsForOrder;
import ru.yaone.manager.DatabaseConnectionManager;
import ru.yaone.model.Car;
import ru.yaone.model.Client;
import ru.yaone.model.Order;
import ru.yaone.model.enumeration.CarCondition;
import ru.yaone.model.enumeration.OrderStatus;
import ru.yaone.services.OrderService;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Реализация интерфейса OrderService, предоставляющая методы для работы с заказами в системе.
 */
@Service
@Loggable("Логирование класса OrderServiceImpl")
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final DatabaseConnectionManager databaseConnectionManager;


    @Override
    public void addOrder(Order order) {
        try (Connection conn = databaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(SqlScriptsForOrder.ADD_ORDER, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, order.clientId());
            preparedStatement.setInt(2, order.carId());
            preparedStatement.setDate(3, Date.valueOf(LocalDate.now()));
            preparedStatement.setString(4, order.status().name());
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    new Order(rs.getInt(1), order.clientId(), order.carId(), order.creationDate(), order.status());
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка SQL: " + e.getMessage());
            throw new RuntimeException("Ошибка при добавлении заказа", e);
        }
    }

    /**
     * Получает все заказы из базы данных.
     *
     * <p>Метод выполняет SQL-запрос для получения всех заказов из таблицы <code>orders</code>,
     * включая информацию о клиенте и автомобиле. Возвращает список заказов.</p>
     *
     * @return список объектов Order, представляющих все заказы в системе
     * @throws RuntimeException если произошла ошибка во время выполнения SQL-запроса
     */
    @Loggable("Логирование метода OrderServiceImpl.getAllOrders")
    @Override
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        try (Connection conn = databaseConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SqlScriptsForOrder.GET_ALL_ORDERS)) {
            while (rs.next()) {
                Client client = new Client(
                        rs.getInt("client_id"),
                        rs.getString("client_name"),
                        rs.getString("contact_info")
                );
                Car car = new Car(
                        rs.getInt("car_id"),
                        rs.getString("make"),
                        rs.getString("model"),
                        rs.getInt("year"),
                        rs.getDouble("price"),
                        CarCondition.valueOf(rs.getString("condition"))
                );
                Order order = new Order(
                        rs.getInt("id"),
                        client.id(),
                        car.id(),
                        rs.getTimestamp("creation_date").toInstant(),
                        OrderStatus.valueOf(rs.getString("status"))
                );
                orders.add(order);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка SQL: " + e.getMessage());
            throw new RuntimeException("Ошибка при получении заказов", e);
        }
        return orders;
    }

    /**
     * Получает заказ из базы данных по заданному идентификатору.
     *
     * <p>Метод выполняет SQL-запрос для получения заказа на основе его идентификатора.
     * Если заказ с данным ID найден, возвращает объект Order; если нет, возвращает null.</p>
     *
     * @param id идентификатор заказа, который необходимо получить
     * @return объект Order, представляющий заказ с заданным идентификатором, или null, если заказ не найден
     * @throws RuntimeException если произошла ошибка во время выполнения SQL-запроса
     */
    @Loggable("Логирование метода OrderServiceImpl.getOrderById")
    @Override
    public Order getOrderById(int id) {
        try (Connection conn = databaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(SqlScriptsForOrder.GET_ORDER_BY_ID)) {
            preparedStatement.setInt(1, id);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    Client client = new Client(
                            rs.getInt("client_id"),
                            rs.getString("client_name"),
                            rs.getString("contact_info")
                    );
                    Car car = new Car(
                            rs.getInt("car_id"),
                            rs.getString("make"),
                            rs.getString("model"),
                            rs.getInt("year"),
                            rs.getDouble("price"),
                            CarCondition.valueOf(rs.getString("condition"))
                    );
                    return new Order(
                            rs.getInt("id"),
                            client.id(),
                            car.id(),
                            rs.getTimestamp("creation_date").toInstant(),
                            OrderStatus.valueOf(rs.getString("status"))
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении заказа по ID", e);
        }
        return null;
    }

    /**
     * Обновляет информацию о заказе в базе данных по заданному идентификатору.
     *
     * <p>Метод выполняет SQL-запрос для обновления данных о заказе в таблице <code>orders</code>.
     * Если заказ с заданным ID найден, данные будут обновлены.</p>
     *
     * @param id           идентификатор заказа, который необходимо обновить
     * @param updatedOrder объект Order с обновленными данными
     * @throws RuntimeException если произошла ошибка во время выполнения SQL-запроса
     */
    @Loggable("Логирование метода OrderServiceImpl.updateOrder")
    @Override
    public void updateOrder(int id, Order updatedOrder) {
        try (Connection conn = databaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(SqlScriptsForOrder.UPDATE_ORDER)) {
            preparedStatement.setInt(1, updatedOrder.clientId());
            preparedStatement.setInt(2, updatedOrder.carId());
            preparedStatement.setDate(3, Date.valueOf(LocalDate.now()));
            preparedStatement.setString(4, updatedOrder.status().toString());
            preparedStatement.setInt(5, id);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Заказ успешно обновлен.");
            } else {
                System.out.println("Заказ не найден.");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка SQL: " + e.getMessage());
            throw new RuntimeException("Ошибка при обновлении заказа", e);
        }
    }

    /**
     * Удаляет заказ из базы данных по заданному идентификатору.
     *
     * <p>Метод выполняет SQL-запрос для удаления заказа из таблицы <code>orders</code>.
     * Если заказ с указанным ID найден, он будет удален из базы данных.</p>
     *
     * @param id идентификатор заказа, который необходимо удалить
     * @throws RuntimeException если произошла ошибка во время выполнения SQL-запроса
     */
    @Loggable("Логирование метода OrderServiceImpl.deleteOrderById")
    @Override
    public boolean deleteOrder(int id) {
        try (Connection conn = databaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(SqlScriptsForOrder.DELETE_ORDER)) {
            preparedStatement.setInt(1, id);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Заказ успешно удален.");
                return true;
            } else {
                System.out.println("Заказ не найден.");
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении заказа", e);
        }
    }
}