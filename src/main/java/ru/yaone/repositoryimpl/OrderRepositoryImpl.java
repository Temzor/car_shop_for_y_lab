package ru.yaone.repositoryimpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.yaone.aspect.annotation.Loggable;
import ru.yaone.constants.SqlScriptsForOrder;
import ru.yaone.manager.DatabaseConnectionManager;
import ru.yaone.model.Car;
import ru.yaone.model.Client;
import ru.yaone.model.Order;
import ru.yaone.model.enumeration.CarCondition;
import ru.yaone.model.enumeration.OrderStatus;
import ru.yaone.repository.OrderRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
@Loggable("Логирование класса OrderRepositoryImpl")
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {
    private final DatabaseConnectionManager databaseConnectionManager;

    @Loggable("Логирование метода OrderRepositoryImpl.getAllOrders")
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

    @Loggable("Логирование метода OrderRepositoryImpl.getAllOrders")
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

    @Loggable("Логирование метода OrderRepositoryImpl.getOrderById")
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

    @Loggable("Логирование метода OrderRepositoryImpl.updateOrder")
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

    @Loggable("Логирование метода OrderRepositoryImpl.deleteOrderById")
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