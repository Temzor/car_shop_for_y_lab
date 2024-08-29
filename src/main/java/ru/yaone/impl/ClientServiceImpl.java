package ru.yaone.impl;

import ru.yaone.manager.DatabaseConnectionManager;
import ru.yaone.model.Client;
import ru.yaone.services.ClientService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Реализация сервиса для управления клиентами.
 *
 * <p>Этот класс обеспечивает операции с клиентами, такие как добавление клиента,
 * получение списка всех клиентов и получение информации о клиенте по идентификатору.</p>
 */
public class ClientServiceImpl implements ClientService {

    @Override
    /**
     * Добавляет нового клиента в базу данных.
     *
     * <p>Метод выполняет SQL-запрос для вставки нового клиента в таблицу
     * <code>clients</code>. Идентификатор клиента автоматически генерируется с помощью
     * последовательности, и возвращается идентификатор добавленного клиента.</p>
     *
     * @param client объект Client, представляющий клиента, которого необходимо добавить
     * @throws RuntimeException если произошла ошибка во время выполнения SQL-запроса
     */
    public void addClient(Client client) {
        String sql = "INSERT INTO car_shop.clients (id, client_name, contact_info) "
                + "VALUES (nextval('clients_id_seq'), ?, ?) RETURNING id";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, client.clientName());
            pstmt.setString(2, client.contactInfo());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    new Client(rs.getInt(1), client.clientName(), client.contactInfo());
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка SQL: " + e.getMessage());
            throw new RuntimeException("Ошибка при добавлении клиента", e);
        }
    }

    @Override
    /**
     * Получает список всех клиентов из базы данных.
     *
     * <p>Метод выполняет SQL-запрос для получения всех клиентов из таблицы
     * <code>clients</code> и возвращает их в виде списка.</p>
     *
     * @return список объектов Client, представляющих всех клиентов в системе
     * @throws RuntimeException если произошла ошибка во время выполнения SQL-запроса
     */
    public List<Client> getAllClients() {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT id, client_name, contact_info FROM car_shop.clients";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Client client = new Client(
                        rs.getInt("id"),
                        rs.getString("client_name"),
                        rs.getString("contact_info")
                );
                clients.add(client);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка SQL: " + e.getMessage());
            throw new RuntimeException("Ошибка при получении клиентов", e);
        }
        return clients;
    }

    @Override
    /**
     * Получает клиента из базы данных по его идентификатору.
     *
     * <p>Метод выполняет SQL-запрос для получения клиента с указанным идентификатором
     * из таблицы <code>clients</code>. Если клиент найден, он возвращается; если
     * нет, возвращается <code>null</code>.</p>
     *
     * @param id идентификатор клиента, которого необходимо получить
     * @return объект Client, представляющий клиента, или <code>null</code>, если клиент не найден
     * @throws RuntimeException если произошла ошибка во время выполнения SQL-запроса
     */
    public Client getClientById(int id) {
        String sql = "SELECT id, client_name, contact_info FROM car_shop.clients WHERE id = ?";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Client(
                            rs.getInt("id"),
                            rs.getString("client_name"),
                            rs.getString("contact_info")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении клиента по ID", e);
        }
        return null;
    }

    @Override
/**
 * Удаляет клиента из базы данных по его идентификатору.
 *
 * <p>Метод выполняет SQL-запрос для удаления клиента с указанным идентификатором
 * из таблицы <code>clients</code>. Если клиент успешно удалён, выводится сообщение
 * об этом; если клиент не найден, выводится сообщение о том, что клиент не найден.</p>
 *
 * @param id идентификатор клиента, которого необходимо удалить
 * @throws RuntimeException если произошла ошибка во время выполнения SQL-запроса
 */
    public void deleteClientById(int id) {
        String sql = "DELETE FROM car_shop.clients WHERE id = ?";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Клиент успешно удален.");
            } else {
                System.out.println("Клиент не найден.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении клиента", e);
        }
    }

    @Override
/**
 * Обновляет информацию о клиенте в базе данных.
 *
 * <p>Метод выполняет SQL-запрос для обновления информации о клиенте с указанным
 * идентификатором в таблице <code>clients</code>. Если клиент найден и информация
 * успешно обновлена, выводится соответствующее сообщение; если клиент не найден,
 * выводится сообщение об этом.</p>
 *
 * @param id идентификатор клиента, информацию о котором необходимо обновить
 * @param updatedClient объект Client, содержащий новые данные для обновления
 * @throws RuntimeException если произошла ошибка во время выполнения SQL-запроса
 */
    public void updateClient(int id, Client updatedClient) {
        String sql = "UPDATE car_shop.clients SET client_name = ?, contact_info = ? WHERE id = ?";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, updatedClient.clientName());
            pstmt.setString(2, updatedClient.contactInfo());
            pstmt.setInt(3, id);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Клиент успешно обновлен.");
            } else {
                System.out.println("Клиент не найден.");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка SQL: " + e.getMessage());
            throw new RuntimeException("Ошибка при обновлении клиента", e);
        }
    }
}