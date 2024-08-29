package ru.yaone.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yaone.aspect.annotation.Loggable;
import ru.yaone.dto.OrderDTO;
import ru.yaone.mapper.OrderMapper;
import ru.yaone.model.Order;
import ru.yaone.services.OrderService;

import java.util.List;

/**
 * Контроллер для работы с заказами.
 */
@Loggable
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@Tag(name = "Order Controller", description = "Операции с заказами")
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    /**
     * Получить все заказы.
     *
     * @return Список заказов в формате OrderDTO.
     */
    @GetMapping(value = "/orders", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Получить все заказы")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        List<OrderDTO> ordersDTO = orderMapper.orderToOrderDTO(orders);
        return ResponseEntity.ok(ordersDTO);
    }

    /**
     * Получить заказ по ID.
     *
     * @param id Идентификатор заказа.
     * @return Заказ в формате OrderDTO, если найден, иначе 404.
     */
    @GetMapping("/orders/{id}")
    @Operation(description = "Получить заказ по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно найден заказ"),
            @ApiResponse(responseCode = "404", description = "Заказ не найден")
    })
    public ResponseEntity<OrderDTO> getOrderById(
            @Parameter(description = "ID заказа", required = true) @PathVariable("id") int id) {

        Order order = orderService.getOrderById(id);
        return order != null
                ? ResponseEntity.ok(orderMapper.orderToOrderDTO(order))
                : ResponseEntity.notFound().build();
    }

    /**
     * Добавить новый заказ.
     *
     * @param orderDTO Данные заказа для добавления.
     * @return Статус 201, если заказ успешно добавлен, или 400 при неправильных данных.
     */
    @PostMapping(value = "/orders")
    @Operation(description = "Добавить новый заказ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Заказ успешно добавлен"),
            @ApiResponse(responseCode = "400", description = "Неверные данные заказа")
    })
    public ResponseEntity<Void> addOrder(
            @Parameter(description = "Данные заказа", required = true) @RequestBody OrderDTO orderDTO) {

        if (orderDTO == null) {
            return ResponseEntity.badRequest().build();
        }

        Order order = orderMapper.orderDTOToOrder(orderDTO);
        if (order == null) {
            return ResponseEntity.badRequest().build();
        }

        orderService.addOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Удалить заказ по ID.
     *
     * @param id Идентификатор заказа для удаления.
     * @return Статус 204, если заказ успешно удален, или 404, если заказ не найден.
     */
    @DeleteMapping("/orders/{id}")
    @Operation(description = "Удалить заказ по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Заказ успешно удален"),
            @ApiResponse(responseCode = "404", description = "Заказ не найден")
    })
    public ResponseEntity<Void> deleteOrderById(
            @Parameter(description = "ID заказа", required = true) @PathVariable("id") int id) {

        boolean isDeleted = orderService.deleteOrder(id);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    /**
     * Обновить информацию о заказе по ID.
     *
     * @param id       Идентификатор заказа для обновления.
     * @param orderDTO Обновленные данные заказа.
     * @return Статус 200, если информация о заказе успешно обновлена,
     * статус 400, если данные неверны,
     * статус 404, если заказ не найден,
     * статус 500, если произошла ошибка сервера.
     */
    @PutMapping("/orders/{id}")
    @Operation(description = "Обновить информацию о заказе")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация о заказе обновлена"),
            @ApiResponse(responseCode = "400", description = "Неверные данные заказа"),
            @ApiResponse(responseCode = "404", description = "Заказ не найден"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    public ResponseEntity<Void> updateOrder(
            @Parameter(description = "ID заказа", required = true) @PathVariable("id") int id,
            @Parameter(description = "Обновленные данные заказа", required = true) @RequestBody OrderDTO orderDTO) {

        if (orderDTO == null) {
            return ResponseEntity.badRequest().build();
        }

        Order existingOrder = orderService.getOrderById(id);
        if (existingOrder == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            Order updatedOrder = orderMapper.orderDTOToOrder(orderDTO);
            orderService.updateOrder(id, updatedOrder);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}