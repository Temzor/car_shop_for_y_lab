package ru.yaone.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.yaone.dto.OrderDTO;
import ru.yaone.impl.OrderServiceImpl;
import ru.yaone.model.enumeration.OrderStatus;
import ru.yaone.services.OrderService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;


public class OrderServletTest {
    private OrderServlet orderServlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private PrintWriter writerMock;
    private OrderService orderServiceMock;

    @BeforeEach
    public void setUp() throws IOException {
        orderServlet = new OrderServlet();
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        writerMock = mock(PrintWriter.class);
        orderServiceMock = mock(OrderServiceImpl.class);
        when(response.getWriter()).thenReturn(writerMock);
        orderServlet.setOrderService(orderServiceMock);
    }

    @Test
    @DisplayName("Тест PUT-запроса с некорректным ID заказа")
    public void testDoPutWithInvalidOrderId() throws IOException {
        when(request.getPathInfo()).thenReturn("/invalidId");
        orderServlet.doPut(request, response);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(writerMock).print("{\"error\":\"Invalid order ID format\"}");
    }

    @Test
    @DisplayName("Тест PUT-запроса без указания ID заказа")
    public void testDoPutWithMissingOrderId() throws IOException {
        when(request.getPathInfo()).thenReturn(null);
        orderServlet.doPut(request, response);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(writerMock).print("{\"error\":\"Order ID must be specified\"}");
    }

    @Test
    @DisplayName("Тест DELETE-запроса с некорректным ID заказа")
    public void testDoDeleteWithInvalidOrderId() throws IOException {
        when(request.getPathInfo()).thenReturn("/invalidId");
        orderServlet.doDelete(request, response);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(writerMock).print("{\"error\":\"Invalid Order ID format\"}");
    }

    @Test
    @DisplayName("Тест DELETE-запроса без указания ID заказа")
    public void testDoDeleteWithMissingOrderId() throws IOException {
        when(request.getPathInfo()).thenReturn(null);
        orderServlet.doDelete(request, response);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(writerMock).print("{\"error\":\"Order ID must be specified\"}");
    }

    @Test
    @DisplayName("Тест GET-запроса для получения всех заказов")
    public void testDoGetAllOrders() throws ServletException, IOException {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setClientId(2);
        orderDTO.setCarId(3);
        when(orderServiceMock.getAllOrders()).thenReturn(Collections.singletonList(orderDTO));
        orderServlet.doGet(request, response);
        verify(response).setContentType("application/json; charset=UTF-8");
        verify(orderServiceMock).getAllOrders();
        String expectedJson = "[{\"id\":0,\"clientId\":2,\"carId\":3,\"creationDate\":null,\"status\":null}]";
        verify(writerMock).print(contains(expectedJson));
    }

    @Test
    @DisplayName("Тест GET-запроса на получение заказа с некорректным ID формата")
    public void testDoGetOrderByIdInvalidIdFormat() throws Exception {
        when(request.getPathInfo()).thenReturn("/invalid");
        orderServlet.doGet(request, response);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(writerMock).print("{\"error\":\"Invalid order ID format\"}");
    }

    @Test
    @DisplayName("Тест GET-запроса на получение заказа по ID")
    public void testDoGetOrderById() throws Exception {
        OrderServiceImpl orderService = mock(OrderServiceImpl.class);
        orderServlet.setOrderService(orderService);
        OrderDTO mockUser = new OrderDTO(1, 2, 3, LocalDateTime.now().toInstant(ZoneOffset.UTC), OrderStatus.PENDING);
        when(orderService.getOrderById(1)).thenReturn(mockUser);
        when(request.getPathInfo()).thenReturn("/1");
        when(response.getWriter()).thenReturn(new PrintWriter(new ByteArrayOutputStream()));
        orderServlet.doGet(request, response);
        verify(response).setContentType("application/json; charset=UTF-8");
        verify(orderService).getOrderById(1);
    }

    @Test
    @DisplayName("Тест POST-запроса с некорректным форматом JSON")
    public void testDoPostInvalidJsonFormat() throws Exception {
        when(request.getInputStream()).thenThrow(new IOException("Invalid JSON"));
        when(response.getWriter()).thenReturn(writerMock);
        assertThatThrownBy(() -> orderServlet.doPost(request, response))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("Invalid JSON");
    }

    @Test
    @DisplayName("Тест PUT-запроса на обновление заказа без указания ID")
    public void testDoPutUserWithoutId() throws IOException {
        when(request.getPathInfo()).thenReturn(null);
        orderServlet.doPut(request, response);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(writerMock).print("{\"error\":\"Order ID must be specified\"}");
    }
}