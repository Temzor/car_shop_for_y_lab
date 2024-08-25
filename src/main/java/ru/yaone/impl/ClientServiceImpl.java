package ru.yaone.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yaone.aspect.annotation.Loggable;
import ru.yaone.constants.SqlScriptsForClients;
import ru.yaone.manager.DatabaseConnectionManager;
import ru.yaone.model.Client;
import ru.yaone.services.ClientService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Реализация сервиса для управления клиентами.
 * <p>Данный класс предоставляет методы для добавления клиентов и получения списка всех клиентов.</p>
 */
@Service
@Loggable("Логирование класса ClientServiceImpl")
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final DatabaseConnectionManager databaseConnectionManager;

    /**
     * Добавляет нового клиента в базу данных.
     * <p>Метод принимает объект {@code Client}, извлекает информацию о клиенте
     * и выполняет SQL-запрос для его добавления в базу данных. Если операция проходит
     * успешно, клиент сохраняется в базе данных, иначе выбрасывается исключение.</p>
     *
     * @param client объект, содержащий информацию о клиенте, который необходимо добавить
     * @throws RuntimeException если произошла ошибка при выполнении SQL-запроса
     */
    @Loggable("Логирование метода ClientServiceImpl.addClient")
    @Override
    public void addClient(Client client) {
        try (Connection conn = databaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(SqlScriptsForClients.ADD_CLIENT, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, client.clientName());
            preparedStatement.setString(2, client.contactInfo());
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    new Client(rs.getInt(1), client.clientName(), client.contactInfo());
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка SQL: " + e.getMessage());
            throw new RuntimeException("Ошибка при добавлении клиента", e);
        }
    }

    /**
     * Получает список всех клиентов из базы данных.
     * <p>Метод выполняет SQL-запрос для получения всех клиентов и возвращает
     * список объектов {@code Client} с информацией о каждом клиенте.</p>
     *
     * @return список {@code Client}, содержащий всех клиентов из базы данных
     * @throws RuntimeException если произошла ошибка при выполнении SQL-запроса
     */
    @Loggable("Логирование метода ClientServiceImpl.getAllClients")
    @Override
    public List<Client> getAllClients() {
        List<Client> clients = new ArrayList<>();
        try (Connection conn = databaseConnectionManager.getConnection();
             Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery(SqlScriptsForClients.GET_ALL_CLIENTS)) {
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


    /**
     * Получает информацию о клиенте по его идентификатору.
     * <p>Метод выполняет SQL-запрос для получения клиента из базы данных на основе его идентификатора.
     * Если клиент с данным ID найден, возвращается объект {@code Client}. В противном случае возвращается {@code null}.</p>
     *
     * @param id идентификатор клиента, информацию о котором необходимо получить
     * @return объект {@code Client} с информацией о клиенте или {@code null}, если клиент не найден
     * @throws RuntimeException если произошла ошибка при выполнении SQL-запроса
     */
    @Loggable("Логирование метода ClientServiceImpl.getClientById")
    @Override
    public Client getClientById(int id) {
        try (Connection conn = databaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(SqlScriptsForClients.GET_CLIENTS_BY_ID, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, id);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return new Client(
                            rs.getInt("id"),
                            rs.getString("client_name"),
                            rs.getString("contact_info")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка SQL: " + e.getMessage());
            throw new RuntimeException("Ошибка при получении клиента по ID", e);
        }
        return null;
    }

    /**
     * Удаляет клиента из базы данных по его идентификатору.
     * <p>Метод выполняет SQL-запрос для удаления клиента. Если клиент с указанным ID найден и удален,
     * возвращает {@code true}. Если клиент не найден, возвращает {@code false}.</p>
     *
     * @param id идентификатор клиента, которого необходимо удалить
     * @return {@code true}, если клиент успешно удален, иначе {@code false}
     * @throws RuntimeException если произошла ошибка при выполнении SQL-запроса
     */
    @Loggable("Логирование метода ClientServiceImpl.deleteClientById")
    @Override
    public boolean deleteClientById(int id) {
        try (Connection conn = databaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(SqlScriptsForClients.DELETED_CLIENTS_BY_ID)) {
            preparedStatement.setInt(1, id);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Клиент успешно удален.");
                return true;
            } else {
                System.out.println("Клиент не найден.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Ошибка SQL: " + e.getMessage());
            throw new RuntimeException("Ошибка при удалении клиента", e);
        }
    }

    /**
     * Обновляет информацию о клиенте.
     * <p>Метод выполняет SQL-запрос для обновления информации о клиенте в базе данных на основе его идентификатора.
     * Если обновление прошло успешно, выводится сообщение об успешном обновлении, в противном случае — о том, что клиент не найден.</p>
     *
     * @param id            идентификатор клиента, которого необходимо обновить
     * @param updatedClient объект {@code Client}, содержащий обновленную информацию о клиенте
     * @throws RuntimeException если произошла ошибка при выполнении SQL-запроса
     */
    @Loggable("Логирование метода ClientServiceImpl.updateClient")
    @Override
    public void updateClient(int id, Client updatedClient) {
        try (Connection conn = databaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(SqlScriptsForClients.UPDATE_CLIENT)) {
            preparedStatement.setString(1, updatedClient.clientName());
            preparedStatement.setString(2, updatedClient.contactInfo());
            preparedStatement.setInt(3, id);
            int affectedRows = preparedStatement.executeUpdate();
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