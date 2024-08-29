package ru.yaone.repositoryimpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.yaone.model.Client;
import ru.yaone.repository.ClientRepository;
import ru.yaone.serviceimpl.ClientServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Тесты для класса ClientServiceImpl")
public class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientServiceImpl clientServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Тест для метода addClient")
    void testAddClient() {
        Client client = new Client(1, "Test Client", "test@example.com");
        doNothing().when(clientRepository).addClient(client);

        clientServiceImpl.addClient(client);

        verify(clientRepository, times(1)).addClient(client);
    }

    @Test
    @DisplayName("Тест для метода getAllClients")
    void testGetAllClients() {
        Client client = new Client(1, "Test Client", "test@example.com");
        when(clientRepository.getAllClients()).thenReturn(List.of(client));

        List<Client> clients = clientServiceImpl.getAllClients();

        assertFalse(clients.isEmpty());
        assertEquals(1, clients.get(0).id());
        assertEquals("Test Client", clients.get(0).clientName());
        assertEquals("test@example.com", clients.get(0).contactInfo());
    }

    @Test
    @DisplayName("Тест для метода getClientById")
    void testGetClientById() {
        int clientId = 1;
        Client client = new Client(clientId, "Test Client", "test@example.com");
        when(clientRepository.getClientById(clientId)).thenReturn(client);

        Client resultClient = clientServiceImpl.getClientById(clientId);

        assertNotNull(resultClient);
        assertEquals(clientId, resultClient.id());
        assertEquals("Test Client", resultClient.clientName());
        assertEquals("test@example.com", resultClient.contactInfo());
    }

    @Test
    @DisplayName("Тест для метода deleteClientById")
    void testDeleteClientById() {
        int clientId = 1;
        when(clientRepository.deleteClientById(clientId)).thenReturn(true);

        boolean result = clientServiceImpl.deleteClientById(clientId);

        assertTrue(result);
        verify(clientRepository, times(1)).deleteClientById(clientId);
    }
}