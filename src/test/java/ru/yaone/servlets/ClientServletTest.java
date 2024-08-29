package ru.yaone.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yaone.dto.ClientDTO;
import ru.yaone.impl.ClientServiceImpl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class ClientServletTest {
    private ClientServlet clientServlet;
    private ClientServiceImpl clientServiceMock;
    private HttpServletRequest requestMock;
    private HttpServletResponse responseMock;
    private PrintWriter writerMock;

    @BeforeEach
    public void setUp() throws IOException {
        clientServiceMock = mock(ClientServiceImpl.class);
        clientServlet = new ClientServlet();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        requestMock = mock(HttpServletRequest.class);
        responseMock = mock(HttpServletResponse.class);
        writerMock = mock(PrintWriter.class);
        when(responseMock.getWriter()).thenReturn(writerMock);
    }

    @Test
    @DisplayName("Тест GET-запроса для получения всех клиентов")
    public void testDoGetAllClients() throws Exception {
        clientServlet.setClientService(clientServiceMock);
        List<ClientDTO> mockClients = Arrays.asList(new ClientDTO(1, "Vasa", "+71234567"),
                new ClientDTO(2, "Misha", "+81234567"));
        when(clientServiceMock.getAllClients()).thenReturn(mockClients);
        when(requestMock.getPathInfo()).thenReturn("/");
        when(responseMock.getWriter()).thenReturn(new PrintWriter(new ByteArrayOutputStream()));
        clientServlet.doGet(requestMock, responseMock);
        verify(responseMock).setContentType("application/json; charset=UTF-8");
        verify(clientServiceMock).getAllClients();
    }

    @Test
    @DisplayName("Тест GET-запроса для получения клиента по ID")
    public void testDoGetClientById() throws Exception {
        ClientServiceImpl clientService = mock(ClientServiceImpl.class);
        clientServlet.setClientService(clientService);
        ClientDTO mockClient = new ClientDTO(1, "Misha", "+81234567");
        when(clientService.getClientById(1)).thenReturn(mockClient);
        when(requestMock.getPathInfo()).thenReturn("/1");
        when(responseMock.getWriter()).thenReturn(new PrintWriter(new ByteArrayOutputStream()));
        clientServlet.doGet(requestMock, responseMock);
        verify(responseMock).setContentType("application/json; charset=UTF-8");
        verify(clientService).getClientById(1);
    }

    @Test
    @DisplayName("Тест GET-запроса на получение клиента с некорректным форматом ID")
    public void testDoGetClientByIdInvalidIdFormat() throws Exception {
        when(requestMock.getPathInfo()).thenReturn("/invalid");
        clientServlet.doGet(requestMock, responseMock);
        verify(responseMock).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(writerMock).print("{\"error\":\"Invalid client ID format\"}");
    }

    @Test
    @DisplayName("Тест POST-запроса с некорректным форматом JSON")
    public void testDoPostInvalidJsonFormat() throws Exception {
        when(requestMock.getInputStream()).thenThrow(new IOException("Invalid JSON"));
        when(responseMock.getWriter()).thenReturn(writerMock);
        assertThatThrownBy(() -> clientServlet.doPost(requestMock, responseMock))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("Invalid JSON");
    }
}