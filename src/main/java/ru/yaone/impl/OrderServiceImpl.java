package ru.yaone.impl;

import ru.yaone.manager.DatabaseConnectionManager;
import ru.yaone.model.Car;
import ru.yaone.model.Client;
import ru.yaone.model.Order;
import ru.yaone.model.enumeration.CarCondition;
import ru.yaone.model.enumeration.OrderStatus;
import ru.yaone.services.OrderService;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Реализация интерфейса OrderService, предоставляющая методы для работы с заказами в системе.
 */
public class OrderServiceImpl implements OrderService {

    @Override
    /**
     * Добавляет новый заказ в базу данных.
     *
     * <p>Метод выполняет SQL-запрос для добавления нового заказа в таблицу <code>orders</code>.
     * Он заполняет значения заказа, включая клиента, автомобил и статус, и возвращает идентификатор
     * нового заказа.</p>
     *
     * @param order объект Order, представляющий заказ для добавления
     * @throws RuntimeException если произошла ошибка во время выполнения SQL-запроса
     */
    public void addOrder(Order order) {
        String sql = """
                INSERT INTO car_shop.orders (id, client_id, car_id, creation_date, status)
                VALUES (nextval('orders_id_seq'), ?, ?, ?, ?) RETURNING id;
                """;
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, order.client().toString());
            pstmt.setInt(2, order.car().id());
            pstmt.setDate(3, Date.valueOf(LocalDate.now()));
            pstmt.setString(4, order.status().toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    new Order(rs.getInt(1), order.client(), order.car(), order.creationDate(), order.status());
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка SQL: " + e.getMessage());
            throw new RuntimeException("Ошибка при добавлении заказа", e);
        }
    }

    @Override
    /**
     * Получает все заказы из базы данных.
     *
     * <p>Метод выполняет SQL-запрос для получения всех заказов из таблицы <code>orders</code>,
     * включая информацию о клиенте и автомобиле. Возвращает список заказов.</p>
     *
     * @return список объектов Order, представляющих все заказы в системе
     * @throws RuntimeException если произошла ошибка во время выполнения SQL-запроса
     */
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = """
                SELECT o.id, o.client_id, c.client_name, c.contact_info,
                       o.car_id, car.make, car.model, car.year, car.price, car.condition,
                       o.creation_date, o.status
                FROM car_shop.orders o
                JOIN car_shop.clients c ON o.client_id = c.id
                JOIN car_shop.cars car ON o.car_id = car.id
                """;
        try (Connection conn = DatabaseConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
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
                        client,
                        car,
                        rs.getTimestamp("creation_date").toLocalDateTime(),
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

    @Override
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
    public Order getOrderById(int id) {
        String sql = """
                SELECT o.id, o.client_id, c.client_name, c.contact_info,
                       o.car_id, car.make, car.model, car.year, car.price, car.condition,
                       o.creation_date, o.status
                FROM car_shop.orders o
                JOIN car_shop.clients c ON o.client_id = c.id
                JOIN car_shop.cars car ON o.car_id = car.id
                WHERE o.id = ?
                """;
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
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
                            client,
                            car,
                            rs.getTimestamp("creation_date").toLocalDateTime(),
                            OrderStatus.valueOf(rs.getString("status"))
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении заказа по ID", e);
        }
        return null;
    }

    @Override
/**
 * Обновляет информацию о заказе в базе данных по заданному идентификатору.
 *
 * <p>Метод выполняет SQL-запрос для обновления данных о заказе в таблице <code>orders</code>.
 * Если заказ с заданным ID найден, данные будут обновлены.</p>
 *
 * @param id идентификатор заказа, который необходимо обновить
 * @param updatedOrder объект Order с обновленными данными
 * @throws RuntimeException если произошла ошибка во время выполнения SQL-запроса
 */
    public void updateOrder(int id, Order updatedOrder) {
        String sql = "UPDATE car_shop.orders SET client = ?, car_id = ?, creation_date = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, updatedOrder.car().toString());
            pstmt.setInt(2, updatedOrder.car().id());
            pstmt.setDate(3, Date.valueOf(LocalDate.now()));
            pstmt.setString(4, updatedOrder.status().toString());
            pstmt.setInt(5, id);
            int affectedRows = pstmt.executeUpdate();
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

    @Override
/**
 * Удаляет заказ из базы данных по заданному идентификатору.
 *
 * <p>Метод выполняет SQL-запрос для удаления заказа из таблицы <code>orders</code>.
 * Если заказ с указанным ID найден, он будет удален из базы данных.</p>
 *
 * @param id идентификатор заказа, который необходимо удалить
 * @throws RuntimeException если произошла ошибка во время выполнения SQL-запроса
 */
    public void deleteOrderById(int id) {
        String sql = "DELETE FROM car_shop.orders WHERE id = ?";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Заказ успешно удален.");
            } else {
                System.out.println("Заказ не найден.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении заказа", e);
        }
    }

    @Override
/**
 * Ищет заказы в базе данных по заданным критериям.
 *
 * <p>Метод создает SQL-запрос на основе переданных параметров для поиска заказов.
 * Исключает параметры, которые равны null, и возвращает список заказов, соответствующих
 * указанным критериям.</p>
 *
 * @param from временная метка, указывающая на начало периода поиска (включительно)
 * @param to временная метка, указывающая на конец периода поиска (включительно)
 * @param client объект Client, представляющий клиента, чьи заказы необходимо найти;
 *               если null, поиск по клиенту не производится
 * @param status статус заказа, по которому производится поиск;
 *               если null, поиск по статусу не производится
 * @param car объект Car, представляющий автомобиль, связанный с заказами;
 *            если null, поиск по автомобилю не производится
 * @return список заказов, соответствующих заданным критериям
 * @throws RuntimeException если произошла ошибка во время выполнения SQL-запроса
 */
    public List<Order> searchOrders(LocalDateTime from, LocalDateTime to, Client client, OrderStatus status, Car car) {
        List<Order> orders = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT id, client_id, car_id, creation_date, status FROM orders WHERE 1=1");

        if (client != null) {
            sql.append(" AND client_id = ?");
        }
        if (car != null) {
            sql.append(" AND car_id = ?");
        }
        if (from != null) {
            sql.append(" AND creation_date >= ?");
        }
        if (to != null) {
            sql.append(" AND creation_date <= ?");
        }
        if (status != null) {
            sql.append(" AND status = ?");
        }

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString());
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Client clientSet = new Client(
                        rs.getInt("client_id"),
                        rs.getString("client_name"),
                        rs.getString("contact_info")
                );
                Car carSet = new Car(
                        rs.getInt("car_id"),
                        rs.getString("make"),
                        rs.getString("model"),
                        rs.getInt("year"),
                        rs.getDouble("price"),
                        CarCondition.valueOf(rs.getString("condition"))
                );
                Order order = new Order(
                        rs.getInt("id"),
                        clientSet,
                        carSet,
                        rs.getTimestamp("creation_date").toLocalDateTime(),
                        OrderStatus.valueOf(rs.getString("status"))
                );
                orders.add(order);

            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске заказа", e);
        }
        return orders;
    }
}