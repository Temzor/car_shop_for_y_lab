package ru.yaone.repositoryimpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.yaone.aspect.annotation.Loggable;
import ru.yaone.constants.SqlScriptsForClients;
import ru.yaone.manager.DatabaseConnectionManager;
import ru.yaone.model.Client;
import ru.yaone.repository.ClientRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@Loggable("Логирование класса ClientRepositoryImpl")
@RequiredArgsConstructor
public class ClientRepositoryImpl implements ClientRepository {

    private final DatabaseConnectionManager databaseConnectionManager;

    @Loggable("Логирование метода ClientRepositoryImpl.addClient")
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

    @Loggable("Логирование метода ClientRepositoryImpl.getAllClients")
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

    @Loggable("Логирование метода ClientRepositoryImpl.getClientById")
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

    @Loggable("Логирование метода ClientRepositoryImpl.deleteClientById")
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

    @Loggable("Логирование метода ClientRepositoryImpl.updateClient")
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