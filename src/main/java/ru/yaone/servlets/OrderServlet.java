package ru.yaone.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import lombok.Setter;
import ru.yaone.aspect.annotation.Loggable;
import ru.yaone.dto.OrderDTO;
import ru.yaone.impl.OrderServiceImpl;
import ru.yaone.services.OrderService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

/**
 * Сервлет для обработки HTTP запросов связанных с заказами.
 * <p>Обрабатывает запросы на получение списка всех заказов, получение заказа по идентификатору и добавление нового заказа.</p>
 */
@Loggable
@WebServlet(name = "OrderServlet", urlPatterns = "/api/orders/*")
public class OrderServlet extends HttpServlet {

    @Setter
    private OrderService orderService = new OrderServiceImpl();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Конструктор, который инициализирует ObjectMapper и регистрирует модуль JavaTimeModule.
     */
    public OrderServlet() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Обработка HTTP GET запросов.
     * <p>Если идентификатор заказа не указан, возвращает список всех заказов.
     * Если указан идентификатор заказа, возвращает информацию о конкретном заказе.</p>
     *
     * @param request  объект {@link HttpServletRequest}, представляющий HTTP-запрос
     * @param response объект {@link HttpServletResponse}, представляющий HTTP-ответ
     * @throws ServletException если происходит ошибка при обработке сервлета
     * @throws IOException      если происходит ошибка ввода/вывода
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json; charset=UTF-8");
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            List<OrderDTO> ordersDTO = orderService.getAllOrders();
            PrintWriter out = response.getWriter();
            out.print(objectMapper.writeValueAsString(ordersDTO));
            out.flush();
        } else {
            int orderId;
            try {
                orderId = Integer.parseInt(pathInfo.substring(1));
                OrderDTO orderDTO = orderService.getOrderById(orderId);

                if (orderDTO != null) {
                    PrintWriter out = response.getWriter();
                    out.print(objectMapper.writeValueAsString(orderDTO));
                    out.flush();
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    PrintWriter out = response.getWriter();
                    out.print("{\"error\":\"Order not found\"}");
                    out.flush();
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                PrintWriter out = response.getWriter();
                out.print("{\"error\":\"Invalid order ID format\"}");
                out.flush();
            }
        }
    }

    /**
     * Обработка HTTP POST запросов для добавления нового заказа.
     * <p>Получает данные заказа из тела запроса, валидирует их, и если данные корректны, добавляет новый заказ.</p>
     *
     * @param request  объект {@link HttpServletRequest}, представляющий HTTP-запрос
     * @param response объект {@link HttpServletResponse}, представляющий HTTP-ответ
     * @throws ServletException если происходит ошибка при обработке сервлета
     * @throws IOException      если происходит ошибка ввода/вывода
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        OrderDTO orderDTO = objectMapper.readValue(request.getInputStream(), OrderDTO.class);

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<OrderDTO>> violations = validator.validate(orderDTO);

            if (!violations.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                PrintWriter out = response.getWriter();
                StringBuilder errorMessages = new StringBuilder();

                for (ConstraintViolation<OrderDTO> violation : violations) {
                    errorMessages.append(violation.getMessage()).append("\n");
                }
                out.print("{\"errors\": \"" + errorMessages.toString().trim().replace("\n", "\\n") + "\"}");
                out.flush();
            } else {
                orderService.addOrder(orderDTO);
                response.setStatus(HttpServletResponse.SC_CREATED);
                PrintWriter out = response.getWriter();
                out.print("{\"status\":\"Order added successfully\"}");
                out.flush();
            }
        }
    }

    /**
     * Обработка HTTP PUT запросов для обновления информации о заказе.
     * <p>Метод получает идентификатор заказа из URL и данные для обновления из тела запроса.
     * Если идентификатор заказа не указан или имеет некорректный формат,
     * возвращается ошибка с соответствующим сообщением.
     * Если данные для обновления не прошли валидацию,
     * возвращаются сообщения об ошибках.
     * В случае успешного обновления заказа, возвращается статус 200 OK.</p>
     *
     * @param request  объект {@link HttpServletRequest}, представляющий HTTP-запрос
     * @param response объект {@link HttpServletResponse}, представляющий HTTP-ответ
     * @throws IOException если происходит ошибка ввода-вывода
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.length() < 2) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            PrintWriter out = response.getWriter();
            out.print("{\"error\":\"Order ID must be specified\"}");
            out.flush();
            return;
        }

        try {
            int orderId = Integer.parseInt(pathInfo.substring(1));
            OrderDTO orderDTO = objectMapper.readValue(request.getInputStream(), OrderDTO.class);
            orderDTO.setId(orderId);

            try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
                Validator validator = factory.getValidator();
                Set<ConstraintViolation<OrderDTO>> violations = validator.validate(orderDTO);

                if (!violations.isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    PrintWriter out = response.getWriter();
                    StringBuilder errorMessages = new StringBuilder();

                    for (ConstraintViolation<OrderDTO> violation : violations) {
                        errorMessages.append(violation.getMessage()).append("\n");
                    }

                    out.print("{\"errors\": \"" + errorMessages.toString().trim().replace("\n", "\\n") + "\"}");
                    out.flush();
                } else {
                    orderService.updateOrder(orderId, orderDTO);
                    response.setStatus(HttpServletResponse.SC_OK);
                    PrintWriter out = response.getWriter();
                    out.print("{\"status\":\"Order updated successfully\"}");
                    out.flush();
                }
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            PrintWriter out = response.getWriter();
            out.print("{\"error\":\"Invalid order ID format\"}");
            out.flush();
        }
    }

    /**
     * Обработка HTTP DELETE запросов для удаления заказа по идентификатору.
     * <p>Метод получает идентификатор заказа из URL.
     * Если идентификатор заказа не указан или имеет некорректный формат,
     * метод возвращает ошибку с соответствующим сообщением.
     * Если идентификатор корректный, то происходит попытка удалить заказ.
     * В случае успешного удаления возвращается статус 204 No Content,
     * если заказ не найден, возвращается ошибка 404 Not Found.</p>
     *
     * @param request  объект {@link HttpServletRequest}, представляющий HTTP-запрос
     * @param response объект {@link HttpServletResponse}, представляющий HTTP-ответ
     * @throws IOException если происходит ошибка ввода-вывода
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.length() < 2) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            PrintWriter out = response.getWriter();
            out.print("{\"error\":\"Order ID must be specified\"}");
            out.flush();
            return;
        }

        int orderId;
        try {
            orderId = Integer.parseInt(pathInfo.substring(1));
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            PrintWriter out = response.getWriter();
            out.print("{\"error\":\"Invalid Order ID format\"}");
            out.flush();
            return;
        }

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<Integer>> violations = validator.validate(orderId);

            if (!violations.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                PrintWriter out = response.getWriter();
                StringBuilder errorMessages = new StringBuilder();

                for (ConstraintViolation<Integer> violation : violations) {
                    errorMessages.append(violation.getMessage()).append("\n");
                }

                out.print("{\"errors\": \"" + errorMessages.toString().trim().replace("\n", "\\n") + "\"}");
                out.flush();
            } else {
                boolean isDeleted = orderService.deleteOrderById(orderId);
                if (isDeleted) {
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    PrintWriter out = response.getWriter();
                    out.print("{\"error\":\"Order not found\"}");
                    out.flush();
                }
            }
        }
    }
}