package ru.yaone.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.Setter;
import ru.yaone.aspect.annotation.Loggable;
import ru.yaone.dto.ClientDTO;
import ru.yaone.impl.ClientServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.yaone.services.ClientService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

/**
 * Сервлет для обработки HTTP запросов, связанных с клиентами.
 * <p>Этот сервлет предоставляет возможности для получения информации о клиентах.</p>
 *
 * @author [Ваше Имя]
 */
@Loggable
@WebServlet(name = "ClientServlet", urlPatterns = "/api/clients/*")
public class ClientServlet extends HttpServlet {

    @Setter
    private ClientService clientService = new ClientServiceImpl();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Конструктор, который регистрирует поддержку форматов времени.
     */
    public ClientServlet() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Обработка HTTP GET запросов для получения информации о клиентах.
     * <p>Если путь запроса не содержит идентификатора клиента, возвращает список всех клиентов.
     * Если путь содержит идентификатор клиента, возвращает информацию о конкретном клиенте.</p>
     *
     * @param request  объект {@link HttpServletRequest}, представляющий HTTP-запрос
     * @param response объект {@link HttpServletResponse}, представляющий HTTP-ответ
     * @throws ServletException если происходит ошибка сервлета
     * @throws IOException      если происходит ошибка ввода/вывода
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json; charset=UTF-8");
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            List<ClientDTO> clientsDTO = clientService.getAllClients();
            PrintWriter out = response.getWriter();
            out.print(objectMapper.writeValueAsString(clientsDTO));
            out.flush();
        } else {
            int clientId;
            try {
                clientId = Integer.parseInt(pathInfo.substring(1));
                ClientDTO clientDTO = clientService.getClientById(clientId);
                if (clientDTO != null) {
                    PrintWriter out = response.getWriter();
                    out.print(objectMapper.writeValueAsString(clientDTO));
                    out.flush();
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    PrintWriter out = response.getWriter();
                    out.print("{\"error\":\"Client not found\"}");
                    out.flush();
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                PrintWriter out = response.getWriter();
                out.print("{\"error\":\"Invalid client ID format\"}");
                out.flush();
            }
        }
    }

    /**
     * Обработка HTTP POST запросов для добавления нового клиента.
     * <p>Метод принимает JSON-данные о клиенте, проверяет их на валидность и
     * добавляет нового клиента, если данные корректны.</p>
     *
     * @param request  объект {@link HttpServletRequest}, представляющий HTTP-запрос
     * @param response объект {@link HttpServletResponse}, представляющий HTTP-ответ
     * @throws ServletException если происходит ошибка сервлета
     * @throws IOException      если происходит ошибка ввода/вывода
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

        ClientDTO clientDTO = objectMapper.readValue(request.getInputStream(), ClientDTO.class);
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<ClientDTO>> violations = validator.validate(clientDTO);

            if (!violations.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                PrintWriter out = response.getWriter();
                StringBuilder errorMessages = new StringBuilder();
                for (ConstraintViolation<ClientDTO> violation : violations) {
                    errorMessages.append(violation.getMessage()).append("\n");
                }
                out.print("{\"errors\": \"" + errorMessages.toString().trim().replace("\n", "\\n") + "\"}");
                out.flush();
            } else {
                clientService.addClient(clientDTO);
                response.setStatus(HttpServletResponse.SC_CREATED);
                PrintWriter out = response.getWriter();
                out.print("{\"status\":\"Client added successfully\"}");
                out.flush();
            }
        }
    }

    /**
     * Обработка HTTP PUT запросов для обновления данных клиента.
     * <p>Метод принимает идентификатор клиента из URL и JSON-данные о клиенте,
     * проверяет их на валидность и обновляет информацию о клиенте, если данные корректны.</p>
     *
     * @param request  объект {@link HttpServletRequest}, представляющий HTTP-запрос
     * @param response объект {@link HttpServletResponse}, представляющий HTTP-ответ
     * @throws IOException если происходит ошибка ввода/вывода
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
            out.print("{\"error\":\"User ID must be specified\"}");
            out.flush();
            return;
        }

        try {
            int orderId = Integer.parseInt(pathInfo.substring(1));
            ClientDTO clientDTO = objectMapper.readValue(request.getInputStream(), ClientDTO.class);
            clientDTO.setId(orderId);

            try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
                Validator validator = factory.getValidator();
                Set<ConstraintViolation<ClientDTO>> violations = validator.validate(clientDTO);

                if (!violations.isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    PrintWriter out = response.getWriter();
                    StringBuilder errorMessages = new StringBuilder();
                    for (ConstraintViolation<ClientDTO> violation : violations) {
                        errorMessages.append(violation.getMessage()).append("\n");
                    }
                    out.print("{\"errors\": \"" + errorMessages.toString().trim().replace("\n", "\\n") + "\"}");
                    out.flush();
                } else {
                    clientService.updateClient(orderId, clientDTO);
                    response.setStatus(HttpServletResponse.SC_OK);
                    PrintWriter out = response.getWriter();
                    out.print("{\"status\":\"Client updated successfully\"}");
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
     * Обработка HTTP DELETE запросов для удаления клиента по указанному идентификатору.
     * <p>Метод извлекает идентификатор клиента из URL, проверяет его на валидность
     * и, если идентификатор корректный, удаляет соответствующего клиента из системы.</p>
     *
     * @param request  объект {@link HttpServletRequest}, представляющий HTTP-запрос
     * @param response объект {@link HttpServletResponse}, представляющий HTTP-ответ
     * @throws IOException если происходит ошибка ввода/вывода
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
            out.print("{\"error\":\"Client ID must be specified\"}");
            out.flush();
            return;
        }

        int clientId;
        try {
            clientId = Integer.parseInt(pathInfo.substring(1));
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            PrintWriter out = response.getWriter();
            out.print("{\"error\":\"Invalid Client ID format\"}");
            out.flush();
            return;
        }

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<Integer>> violations = validator.validate(clientId);

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
                boolean isDeleted = clientService.deleteClientById(clientId);
                if (isDeleted) {
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    PrintWriter out = response.getWriter();
                    out.print("{\"error\":\"Client not found\"}");
                    out.flush();
                }
            }
        }
    }
}