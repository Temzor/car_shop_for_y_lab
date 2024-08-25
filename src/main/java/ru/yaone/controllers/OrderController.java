package ru.yaone.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

@Loggable
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@Api(value = "Order Controller", tags = "Операции с заказами")
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @GetMapping(value = "/orders", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Получить все заказы", response = OrderDTO.class, responseContainer = "List")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        List<OrderDTO> ordersDTO = orderMapper.orderToOrderDTO(orders);
        return ResponseEntity.ok(ordersDTO);
    }

    @GetMapping("/orders/{id}")
    @ApiOperation(value = "Получить заказ по ID", response = OrderDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешно найден заказ"),
            @ApiResponse(code = 404, message = "Заказ не найден")
    })
    public ResponseEntity<OrderDTO> getOrderById(@ApiParam(value = "ID заказа", required = true) @PathVariable("id") int id) {
        Order order = orderService.getOrderById(id);
        if (order != null) {
            OrderDTO orderDTO = orderMapper.orderToOrderDTO(order);
            return ResponseEntity.ok(orderDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "/orders")
    @ApiOperation(value = "Добавить новый заказ")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Заказ успешно добавлен"),
            @ApiResponse(code = 400, message = "Неверные данные заказа")
    })
    public ResponseEntity<Void> addOrder(@ApiParam(value = "Данные заказа", required = true) @RequestBody OrderDTO orderDTO) {
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

    @DeleteMapping("/orders/{id}")
    @ApiOperation(value = "Удалить заказ по ID")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Заказ успешно удален"),
            @ApiResponse(code = 404, message = "Заказ не найден")
    })
    public ResponseEntity<Void> deleteOrderById(@ApiParam(value = "ID заказа", required = true) @PathVariable("id") int id) {
        boolean isDeleted = orderService.deleteOrder(id);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PutMapping("/orders/{id}")
    @ApiOperation(value = "Обновить информацию о заказе")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Информация о заказе обновлена"),
            @ApiResponse(code = 400, message = "Неверные данные заказа"),
            @ApiResponse(code = 404, message = "Заказ не найден"),
            @ApiResponse(code = 500, message = "Ошибка сервера")
    })
    public ResponseEntity<Void> updateOrder(@ApiParam(value = "ID заказа", required = true) @PathVariable("id") int id,
                                            @ApiParam(value = "Обновленные данные заказа", required = true) @RequestBody OrderDTO updatedOrderDTO) {
        if (updatedOrderDTO == null) {
            return ResponseEntity.badRequest().build();
        }
        Order updatedOrder = orderMapper.orderDTOToOrder(updatedOrderDTO);
        try {
            if (orderService.getOrderById(id) == null) {
                return ResponseEntity.notFound().build();
            }
            orderService.updateOrder(id, updatedOrder);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}