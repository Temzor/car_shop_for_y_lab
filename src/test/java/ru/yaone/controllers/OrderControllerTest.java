package ru.yaone.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yaone.dto.OrderDTO;
import ru.yaone.mapper.OrderMapper;
import ru.yaone.model.Order;
import ru.yaone.model.enumeration.OrderStatus;
import ru.yaone.services.OrderService;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderController orderController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
        objectMapper = new ObjectMapper();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("Тест получения всех заказов")
    void testGetAllOrders() throws Exception {
        Order order = new Order(1, 1, 1, Instant.now(), OrderStatus.PENDING);
        OrderDTO orderDTO = new OrderDTO(1, 1, 1, Instant.now(), OrderStatus.PENDING);
        List<Order> orders = List.of(order);

        when(orderService.getAllOrders()).thenReturn(orders);
        when(orderMapper.orderToOrderDTO(orders)).thenReturn(List.of(orderDTO));

        mockMvc.perform(get("/api/orders")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].clientId").value(1))
                .andExpect(jsonPath("$[0].carId").value(1))
                .andExpect(jsonPath("$[0].status").value(OrderStatus.PENDING.toString()));
    }

    @Test
    @DisplayName("Тест получения заказа по ID (найден)")
    void testGetOrderById() throws Exception {
        Order order = new Order(1, 1, 1, Instant.now(), OrderStatus.APPROVED);
        OrderDTO orderDTO = new OrderDTO(1, 1, 1, Instant.now(), OrderStatus.APPROVED);

        when(orderService.getOrderById(1)).thenReturn(order);
        when(orderMapper.orderToOrderDTO(order)).thenReturn(orderDTO);

        mockMvc.perform(get("/api/orders/{id}", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.clientId").value(1))
                .andExpect(jsonPath("$.carId").value(1))
                .andExpect(jsonPath("$.status").value(OrderStatus.APPROVED.toString()));
    }

    @Test
    @DisplayName("Тест получения заказа по ID (не найден)")
    void testGetOrderByIdNotFound() throws Exception {
        when(orderService.getOrderById(2)).thenReturn(null);

        mockMvc.perform(get("/api/orders/{id}", 2)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Тест добавления заказа")
    void testAddOrder() throws Exception {
        Order order = new Order(1, 1, 1, Instant.now(), OrderStatus.REJECTED);
        OrderDTO orderDTO = new OrderDTO(1, 1, 1, Instant.now(), OrderStatus.REJECTED);
        when(orderMapper.orderDTOToOrder(any(OrderDTO.class))).thenReturn(order);
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDTO)))
                .andExpect(status().isCreated());
        verify(orderService, times(1)).addOrder(any(Order.class));
    }


    @Test
    @DisplayName("Тест удаления заказа по id (найден)")
    void testDeleteOrderById() throws Exception {
        when(orderService.deleteOrder(1)).thenReturn(true);

        mockMvc.perform(delete("/api/orders/{id}", 1))
                .andExpect(status().isNoContent());

        verify(orderService, times(1)).deleteOrder(1);
    }

    @Test
    @DisplayName("Тест удаления заказа по id (не найден)")
    void testDeleteOrderByIdNotFound() throws Exception {
        when(orderService.deleteOrder(2)).thenReturn(false);

        mockMvc.perform(delete("/api/orders/{id}", 2))
                .andExpect(status().isNotFound());

        verify(orderService, times(1)).deleteOrder(2);
    }

    @Test
    @DisplayName("Тест обновления заказа")
    void testUpdateOrder() throws Exception {
        OrderDTO orderDTO = new OrderDTO(1, 1, 1, Instant.now(), OrderStatus.REJECTED);
        Order updatedOrder = new Order(1, 1, 2, Instant.now(), OrderStatus.REJECTED);

        when(orderMapper.orderDTOToOrder(any(OrderDTO.class))).thenReturn(updatedOrder);
        when(orderService.getOrderById(1)).thenReturn(updatedOrder);

        mockMvc.perform(put("/api/orders/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDTO)))
                .andExpect(status().isOk());

        verify(orderService).updateOrder(eq(1), any(Order.class));
    }

    @Test
    @DisplayName("Тест обновления заказа с ошибкой")
    void testUpdateOrderInternalServerError() throws Exception {
        OrderDTO updatedOrderDTO = new OrderDTO(1, 1, 1, Instant.now(), OrderStatus.REJECTED);

        doThrow(new RuntimeException()).when(orderService).updateOrder(anyInt(), any(Order.class));

        mockMvc.perform(put("/api/orders/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedOrderDTO)))
                .andExpect(status().is4xxClientError());
    }
}