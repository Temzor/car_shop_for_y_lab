package ru.yaone.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.yaone.manager.DatabaseConnectionManager;
import ru.yaone.model.Client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Тесты для класса ClientServiceImpl")
public class ClientServiceImplTest {

    @Mock
    private DatabaseConnectionManager databaseConnectionManager;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    @InjectMocks
    private ClientServiceImpl clientServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(databaseConnectionManager.getConnection()).thenReturn(mockConnection);
    }
    @Test
    @DisplayName("Тест для метода addClient")
    void testAddClient() throws SQLException {
        Client client = new Client(1, "Test Client", "test@example.com");

        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(client.id());
        clientServiceImpl.addClient(client);
        verify(mockPreparedStatement, times(1)).setString(1, client.clientName());
        verify(mockPreparedStatement, times(1)).setString(2, client.contactInfo());
        verify(mockPreparedStatement, times(1)).executeQuery();
    }

    @Test
    @DisplayName("Тест для метода getAllClients")
    void testGetAllClients() throws SQLException {
        when(mockConnection.createStatement()).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("client_name")).thenReturn("Test Client");
        when(mockResultSet.getString("contact_info")).thenReturn("test@example.com");

        List<Client> clients = clientServiceImpl.getAllClients();

        assertFalse(clients.isEmpty());
        assertEquals(1, clients.get(0).id());
        assertEquals("Test Client", clients.get(0).clientName());
        assertEquals("test@example.com", clients.get(0).contactInfo());
    }

    @Test
    @DisplayName("Тест для метода getClientById")
    void testGetClientById() throws SQLException {
        int clientId = 1;
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(clientId);
        when(mockResultSet.getString("client_name")).thenReturn("Test Client");
        when(mockResultSet.getString("contact_info")).thenReturn("test@example.com");

        Client client = clientServiceImpl.getClientById(clientId);

        assertNotNull(client);
        assertEquals(clientId, client.id());
        assertEquals("Test Client", client.clientName());
        assertEquals("test@example.com", client.contactInfo());
    }

    @Test
    @DisplayName("Тест для метода deleteClientById")
    void testDeleteClientById() throws SQLException {
        int clientId = 1;
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        boolean result = clientServiceImpl.deleteClientById(clientId);

        assertTrue(result);
        verify(mockPreparedStatement, times(1)).setInt(1, clientId);
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }
}
