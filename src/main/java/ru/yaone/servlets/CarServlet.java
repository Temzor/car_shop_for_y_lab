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
import ru.yaone.dto.CarDTO;
import ru.yaone.impl.CarServiceImpl;
import ru.yaone.services.CarService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

/**
 * Сервлет для управления автомобилями.
 * <p>Этот сервлет обрабатывает HTTP-запросы для получения и добавления информации об автомобилях.</p>
 */
@Loggable("Логирование класса CarServlet")
@Setter
@WebServlet(name = "CarServlet", urlPatterns = "/api/cars/*")
public class CarServlet extends HttpServlet {

    private CarService carService = new CarServiceImpl();
    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Обработка HTTP GET запросов.
     * <p>Принимает запросы на получение информации об автомобилях. Если URL совпадает с "/api/cars/",
     * возвращает список всех автомобилей. Если указано ID автомобиля, возвращает информацию о конкретном автомобиле.</p>
     *
     * @param request  объект {@link HttpServletRequest}, представляющий HTTP-запрос
     * @param response объект {@link HttpServletResponse}, представляющий HTTP-ответ
     * @throws IOException если происходит ошибка ввода/вывода
     */
    @Loggable("Логирование метода CarServlet.doGet")
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=UTF-8");
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            List<CarDTO> carDTOs = carService.getAllCars();
            PrintWriter out = response.getWriter();
            out.print(objectMapper.writeValueAsString(carDTOs));
            out.flush();
        } else {
            int carId;
            try {
                carId = Integer.parseInt(pathInfo.substring(1));
                CarDTO carDTO = carService.getCarById(carId);
                if (carDTO != null) {
                    PrintWriter out = response.getWriter();
                    out.print(objectMapper.writeValueAsString(carDTO));
                    out.flush();
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    PrintWriter out = response.getWriter();
                    out.print("{\"error\":\"Car not found\"}");
                    out.flush();
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                PrintWriter out = response.getWriter();
                out.print("{\"error\":\"Invalid car ID format\"}");
                out.flush();
            }
        }
    }

    /**
     * Обработка HTTP POST запросов.
     * <p>Принимает данные нового автомобиля в формате JSON и добавляет его в систему.
     * В случае ошибок валидации возвращает список ошибок.</p>
     *
     * @param request  объект {@link HttpServletRequest}, представляющий HTTP-запрос
     * @param response объект {@link HttpServletResponse}, представляющий HTTP-ответ
     * @throws IOException если происходит ошибка ввода/вывода
     */
    @Loggable("Логирование метода CarServlet.doPost")
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

        CarDTO carDTO = objectMapper.readValue(request.getInputStream(), CarDTO.class);
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<CarDTO>> violations = validator.validate(carDTO);

            if (!violations.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                PrintWriter out = response.getWriter();
                StringBuilder errorMessages = new StringBuilder();
                for (ConstraintViolation<CarDTO> violation : violations) {
                    errorMessages.append(violation.getMessage()).append("\n");
                }
                out.print("{\"errors\": \"" + errorMessages.toString().trim().replace("\n", "\\n") + "\"}");
                out.flush();
            } else {
                carService.addCar(carDTO);
                response.setStatus(HttpServletResponse.SC_CREATED);
                PrintWriter out = response.getWriter();
                out.print("{\"status\":\"Car added successfully\"}");
                out.flush();
            }
        }
    }

    /**
     * Обработка HTTP DELETE запросов.
     * <p>Этот метод обрабатывает запросы на удаление автомобиля по его ID.
     * Если ID указан неверно или автомобиль не найден, возвращает соответствующий код ошибки.</p>
     *
     * @param request  объект {@link HttpServletRequest}, представляющий HTTP-запрос
     * @param response объект {@link HttpServletResponse}, представляющий HTTP-ответ
     * @throws IOException если происходит ошибка ввода/вывода
     */
    @Loggable("Логирование метода CarServlet.doDelete")
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=UTF-8");
        String pathInfo = request.getPathInfo();

        if (pathInfo != null && !pathInfo.equals("/")) {
            try {
                int carId = Integer.parseInt(pathInfo.substring(1));
                boolean deleted = carService.deleteCarById(carId);
                if (deleted) {
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    PrintWriter out = response.getWriter();
                    out.print("{\"error\":\"Car not found\"}");
                    out.flush();
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                PrintWriter out = response.getWriter();
                out.print("{\"error\":\"Invalid car ID format\"}");
                out.flush();
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            PrintWriter out = response.getWriter();
            out.print("{\"error\":\"Car ID is required\"}");
            out.flush();
        }
    }
}