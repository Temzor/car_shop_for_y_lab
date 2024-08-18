package ru.yaone.servlets;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.yaone.dto.CarDTO;
import ru.yaone.impl.CarServiceImpl;
import ru.yaone.model.enumeration.CarCondition;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;


public class CarServletTest {
    private CarServlet carServlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private PrintWriter writerMock;

    @BeforeEach
    public void setUp() throws IOException {
        carServlet = new CarServlet();
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        writerMock = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writerMock);
    }

    @Test
    @DisplayName("Тест GET-запроса для получения всех автомобилей")
    public void testDoGetAllCars() throws ServletException, IOException {
        CarServiceImpl carServiceMock = mock(CarServiceImpl.class);
        carServlet.setCarService(carServiceMock);
        List<CarDTO> mockCars = Arrays.asList(new CarDTO(1, "Toyota", "Corolla", 1999, 200.00, CarCondition.NEW),
                new CarDTO(2, "Lada", "Granta", 2000, 100.00, CarCondition.USED));
        when(carServiceMock.getAllCars()).thenReturn(mockCars);
        when(request.getPathInfo()).thenReturn("/");
        when(response.getWriter()).thenReturn(new PrintWriter(new ByteArrayOutputStream()));
        carServlet.doGet(request, response);
        verify(response).setContentType("application/json; charset=UTF-8");
        verify(carServiceMock).getAllCars();
    }

    @Test
    @DisplayName("Тест GET-запроса для получения автомобиля по ID")
    public void testDoGetCarById() throws ServletException, IOException {
        CarServiceImpl carServiceMock = mock(CarServiceImpl.class);
        carServlet.setCarService(carServiceMock);
        CarDTO mockCar = new CarDTO(1, "Toyota", "Corolla", 1999, 200.00, CarCondition.NEW);
        when(carServiceMock.getCarById(1)).thenReturn(mockCar);
        when(request.getPathInfo()).thenReturn("/1");
        when(response.getWriter()).thenReturn(new PrintWriter(new ByteArrayOutputStream()));
        carServlet.doGet(request, response);
        verify(response).setContentType("application/json; charset=UTF-8");
        verify(carServiceMock).getCarById(1);
    }

    @Test
    @DisplayName("Тест POST-запроса с некорректным форматом JSON")
    public void testDoPostInvalidJsonFormat() throws Exception {
        when(request.getInputStream()).thenThrow(new IOException("Invalid JSON"));
        when(response.getWriter()).thenReturn(writerMock);
        assertThatThrownBy(() -> carServlet.doPost(request, response))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("Invalid JSON");
    }
}