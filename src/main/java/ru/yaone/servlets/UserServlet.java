package ru.yaone.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.Setter;
import ru.yaone.aspect.annotation.Loggable;
import ru.yaone.dto.UserDTO;
import ru.yaone.impl.UserServiceImpl;
import ru.yaone.services.UserService;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

/**
 * Сервлет для управления пользователями.
 * <p>
 * Этот сервлет обрабатывает HTTP-запросы для получения данных пользователей и добавления новых пользователей.
 * Основные пути доступа:
 * <ul>
 *     <li>GET /api/users/ - возвращает список всех пользователей;</li>
 *     <li>GET /api/users/{id} - возвращает информацию о пользователе по заданному идентификатору;</li>
 *     <li>POST /api/users/ - добавляет нового пользователя.</li>
 * </ul>
 * </p>
 */
@Loggable("Логирование класса UserService")
@Setter
@WebServlet(name = "UserService", urlPatterns = "/api/users/*")
public class UserServlet extends HttpServlet {

    private UserService userService = new UserServiceImpl();
    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Обработка HTTP GET запросов.
     * <p>
     * Если путь запроса не содержит идентификатора пользователя, возвращает список всех пользователей.
     * Если идентификатор указан, возвращает информацию о пользователе с данным идентификатором.
     * В случае отсутствия пользователя возвращается ошибка 404 Not Found.
     * </p>
     *
     * @param request  объект {@link HttpServletRequest} с информацией о запросе
     * @param response объект {@link HttpServletResponse} для формирования ответа
     * @throws IOException если происходит ошибка ввода-вывода
     */
    @Loggable("Логирование метода UserService.doGet")
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=UTF-8");
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            List<UserDTO> usersDTO = userService.getAllUsers();
            PrintWriter out = response.getWriter();
            out.print(objectMapper.writeValueAsString(usersDTO));
            out.flush();
        } else {
            int clientId;
            try {
                clientId = Integer.parseInt(pathInfo.substring(1));
                UserDTO userDTO = userService.getUserById(clientId);
                if (userDTO != null) {
                    PrintWriter out = response.getWriter();
                    out.print(objectMapper.writeValueAsString(userDTO));
                    out.flush();
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    PrintWriter out = response.getWriter();
                    out.print("{\"error\":\"User not found\"}");
                    out.flush();
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                PrintWriter out = response.getWriter();
                out.print("{\"error\":\"Invalid user ID format\"}");
                out.flush();
            }
        }
    }

    /**
     * Обработка HTTP POST запросов.
     * <p>
     * Принимает данные нового пользователя в формате JSON,
     * выполняет валидацию полей и добавляет пользователя в систему.
     * Если данные невалидные, возвращает ошибки валидации с статусом 400 Bad Request.
     * При успешном добавлении возвращается статус 201 Created.
     * </p>
     *
     * @param request  объект {@link HttpServletRequest} с информацией о запросе
     * @param response объект {@link HttpServletResponse} для формирования ответа
     * @throws IOException если происходит ошибка ввода-вывода
     */
    @Loggable("Логирование метода UserService.doPost")
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        UserDTO userDTO = objectMapper.readValue(request.getInputStream(), UserDTO.class);

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<UserDTO>> violations = validator.validate(userDTO);
            if (!violations.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                PrintWriter out = response.getWriter();
                StringBuilder errorMessages = new StringBuilder();

                for (ConstraintViolation<UserDTO> violation : violations) {
                    errorMessages.append(violation.getMessage()).append("\n");
                }

                out.print("{\"errors\": \"" + errorMessages.toString().trim().replace("\n", "\\n") + "\"}");
                out.flush();
            } else {
                userService.addUser(userDTO);
                response.setStatus(HttpServletResponse.SC_CREATED);
                PrintWriter out = response.getWriter();
                out.print("{\"status\":\"User added successfully\"}");
                out.flush();
            }
        }
    }


    /**
     * Обработка HTTP PUT запросов.
     * <p>
     * Данный метод принимает данные для обновления существующего пользователя по заданному идентификатору.
     * Если идентификатор пользователя не указан или указан неверно, возвращает ошибку 400 Bad Request с соответствующим сообщением.
     * Если данные пользователя прошли валидацию, выполняет обновление информации о пользователе и возвращает статус 200 OK.
     * В случае обнаружения ошибок валидации возвращает ошибки с деталями.
     * </p>
     *
     * @param request  объект {@link HttpServletRequest} с информацией о запросе
     * @param response объект {@link HttpServletResponse} для формирования ответа
     * @throws IOException если происходит ошибка ввода-вывода при обработке запроса или при попытке получить входные данные
     */
    @Loggable("Логирование метода UserService.doPut")
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
            int userId = Integer.parseInt(pathInfo.substring(1));
            UserDTO userDTO = objectMapper.readValue(request.getInputStream(), UserDTO.class);
            userDTO.setId(userId);

            try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
                Validator validator = factory.getValidator();
                Set<ConstraintViolation<UserDTO>> violations = validator.validate(userDTO);

                if (!violations.isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    PrintWriter out = response.getWriter();
                    StringBuilder errorMessages = new StringBuilder();

                    for (ConstraintViolation<UserDTO> violation : violations) {
                        errorMessages.append(violation.getMessage()).append("\n");
                    }

                    out.print("{\"errors\": \"" + errorMessages.toString().trim().replace("\n", "\\n") + "\"}");
                    out.flush();
                } else {
                    userService.updateUser(userId, userDTO);
                    response.setStatus(HttpServletResponse.SC_OK);
                    PrintWriter out = response.getWriter();
                    out.print("{\"status\":\"User updated successfully\"}");
                    out.flush();
                }
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            PrintWriter out = response.getWriter();
            out.print("{\"error\":\"Invalid user ID format\"}");
            out.flush();
        }
    }
}