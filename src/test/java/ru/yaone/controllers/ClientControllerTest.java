package ru.yaone.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yaone.dto.ClientDTO;
import ru.yaone.mapper.ClientMapper;
import ru.yaone.model.Client;
import ru.yaone.services.ClientService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ClientControllerTest {
    private MockMvc mockMvc;

    @Mock
    private ClientService clientService;

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private ClientController clientController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(clientController).build();
    }

    @Test
    @DisplayName("Тест получения всех клиентов")
    void testGetAllClients() throws Exception {
        Client client = new Client(1, "John Doe", "john@example.com");
        List<Client> clients = List.of(client);
        ClientDTO clientDTO = new ClientDTO(1, "John Doe", "john@example.com");

        when(clientService.getAllClients()).thenReturn(clients);
        when(clientMapper.clientToClientDTO(clients)).thenReturn(List.of(clientDTO));

        mockMvc.perform(get("/api/clients")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].clientName").value("John Doe"));
    }

    @Test
    @DisplayName("Тест получения клиента по ID (найден)")
    void testGetClientByIdFound() throws Exception {
        Client client = new Client(1, "Jane Doe", "jane@example.com");
        ClientDTO clientDTO = new ClientDTO(1, "Jane Doe", "jane@example.com");

        when(clientService.getClientById(1)).thenReturn(client);
        when(clientMapper.clientToClientDTO(client)).thenReturn(clientDTO);

        mockMvc.perform(get("/api/clients/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.clientName").value("Jane Doe"));
    }

    @Test
    @DisplayName("Тест получения клиента по ID (не найден)")
    void testGetClientByIdNotFound() throws Exception {
        when(clientService.getClientById(99)).thenReturn(null);

        mockMvc.perform(get("/api/clients/99")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Тест добавления клиента")
    void testAddClient() throws Exception {
        ClientDTO clientDTO = new ClientDTO(1, "Ford", "ford@example.com");
        Client client = new Client(1, "Ford", "ford@example.com");

        when(clientMapper.clientDTOToClient(any(ClientDTO.class))).thenReturn(client);

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(clientDTO)))
                .andExpect(status().isCreated());

        ArgumentCaptor<ClientDTO> dtoCaptor = ArgumentCaptor.forClass(ClientDTO.class);
        verify(clientMapper).clientDTOToClient(dtoCaptor.capture());
        assert (dtoCaptor.getValue().getContactInfo()).equals(clientDTO.getContactInfo());
    }

    @Test
    @DisplayName("Тест удаления клиента по ID (найден)")
    void testDeleteClientByIdFound() throws Exception {
        when(clientService.deleteClientById(1)).thenReturn(true);

        mockMvc.perform(delete("/api/clients/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Тест удаления клиента по ID (не найден)")
    void testDeleteClientByIdNotFound() throws Exception {
        when(clientService.deleteClientById(99)).thenReturn(false);

        mockMvc.perform(delete("/api/clients/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Тест обновления клиента")
    void testUpdateClient() throws Exception {
        ClientDTO clientDTO = new ClientDTO(1, "Jane Doe", "jane@example.com");
        Client updatedClient = new Client(1, "Jane Doe", "jane@example.com");

        when(clientMapper.clientDTOToClient(any(ClientDTO.class))).thenReturn(updatedClient);
        when(clientService.getClientById(1)).thenReturn(updatedClient);
        mockMvc.perform(put("/api/clients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(clientDTO)))
                .andExpect(status().isOk());

        verify(clientService).updateClient(eq(1), any(Client.class));
    }

    @Test
    @DisplayName("Тест обновления клиента с ошибкой")
    void testUpdateClientError() throws Exception {
        ClientDTO clientDTO = new ClientDTO(1, "Jane Doe", "jane@example.com");

        doThrow(new RuntimeException()).when(clientService).updateClient(eq(1), any(Client.class));

        mockMvc.perform(put("/api/clients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(clientDTO)))
                .andExpect(status().isNotFound());
    }
}