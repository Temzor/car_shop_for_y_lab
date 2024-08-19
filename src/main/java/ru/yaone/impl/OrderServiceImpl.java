package ru.yaone.impl;

import ru.yaone.aspect.annotation.Loggable;
import ru.yaone.constants.SqlScriptsForOrder;
import ru.yaone.dto.OrderDTO;
import ru.yaone.manager.DatabaseConnectionManager;
import ru.yaone.model.Car;
import ru.yaone.model.Client;
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
@Loggable("Логирование класса OrderServiceImpl")
public class OrderServiceImpl implements OrderService {
    @Override
    public void addOrder(OrderDTO orderDTO) {
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(SqlScriptsForOrder.ADD_ORDER)) {
            preparedStatement.setString(1, String.valueOf(orderDTO.getClientId()));
            preparedStatement.setInt(2, orderDTO.getCarId());
            preparedStatement.setDate(3, Date.valueOf(LocalDate.now()));
            preparedStatement.setString(4, orderDTO.getStatus().toString());
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    new OrderDTO(rs.getInt(1), orderDTO.getClientId(), orderDTO.getCarId(), orderDTO.getCreationDate(), orderDTO.getStatus());
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
    public List<OrderDTO> getAllOrders() {
        List<OrderDTO> ordersDTO = new ArrayList<>();
        try (Connection conn = DatabaseConnectionManager.getConnection();
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
                OrderDTO orderDTO = new OrderDTO(
                        rs.getInt("id"),
                        client.id(),
                        car.id(),
                        rs.getTimestamp("creation_date").toInstant(),
                        OrderStatus.valueOf(rs.getString("status"))
                );
                ordersDTO.add(orderDTO);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка SQL: " + e.getMessage());
            throw new RuntimeException("Ошибка при получении заказов", e);
        }
        return ordersDTO;
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
    public OrderDTO getOrderById(int id) {
        try (Connection conn = DatabaseConnectionManager.getConnection();
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
                    return new OrderDTO(
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
    public void updateOrder(int id, OrderDTO updatedOrder) {
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(SqlScriptsForOrder.UPDATE_ORDER)) {
            preparedStatement.setInt(1, updatedOrder.getClientId());
            preparedStatement.setInt(2, updatedOrder.getCarId());
            preparedStatement.setDate(3, Date.valueOf(LocalDate.now()));
            preparedStatement.setString(4, updatedOrder.getStatus().toString());
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
    public boolean deleteOrderById(int id) {
        try (Connection conn = DatabaseConnectionManager.getConnection();
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