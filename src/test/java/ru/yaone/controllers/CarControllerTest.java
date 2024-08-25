package ru.yaone.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yaone.dto.CarDTO;
import ru.yaone.mapper.CarMapper;
import ru.yaone.model.Car;
import ru.yaone.model.enumeration.CarCondition;
import ru.yaone.services.CarService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CarControllerTest {
    private MockMvc mockMvc;

    @Mock
    private CarService carService;

    @Mock
    private CarMapper carMapper;

    @InjectMocks
    private CarController carController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(carController).build();
    }

    @Test
    @DisplayName("Тест получения всех автомобилей")
    void testGetAllCars() throws Exception {
        Car car = new Car(1, "Toyota", "Camry", 2020, 30000.00, CarCondition.NEW);
        List<Car> carList = List.of(car);
        CarDTO carDTO = new CarDTO(1, "Toyota", "Camry", 2020, 30000.00, CarCondition.NEW);

        when(carService.getAllCars()).thenReturn(carList);
        when(carMapper.carToCarDTO(carList)).thenReturn(List.of(carDTO)); // Маппим список

        mockMvc.perform(get("/api/cars")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].make").value("Toyota")); // Проверяем значение
    }

    @Test
    @DisplayName("Тест получения автомобиля по ID (найден)")
    void testGetCarByIdFound() throws Exception {
        Car car = new Car(1, "Honda", "Civic", 2022, 25000.00, CarCondition.NEW);
        CarDTO carDTO = new CarDTO(1, "Honda", "Civic", 2022, 25000.00, CarCondition.NEW);

        when(carService.getCarById(1)).thenReturn(car);
        when(carMapper.carToCarDTO(car)).thenReturn(carDTO);

        mockMvc.perform(get("/api/cars/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.make").value("Honda"));
    }

    @Test
    @DisplayName("Тест получения автомобиля по ID (не найден)")
    void testGetCarByIdNotFound() throws Exception {
        when(carService.getCarById(99)).thenReturn(null);

        mockMvc.perform(get("/api/cars/99")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Тест добавления автомобиля")
    void testAddCar() throws Exception {
        CarDTO carDTO = new CarDTO(1, "Ford", "Mustang", 2023, 50000.00, CarCondition.NEW);
        Car car = new Car(1, "Ford", "Mustang", 2023, 50000.00, CarCondition.NEW);

        when(carMapper.carDTOToCar(any(CarDTO.class))).thenReturn(car);

        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(carDTO)))
                .andExpect(status().isCreated());
        ArgumentCaptor<CarDTO> dtoCaptor = ArgumentCaptor.forClass(CarDTO.class);
        verify(carMapper).carDTOToCar(dtoCaptor.capture());

        Assertions.assertEquals(carDTO.getYear(), dtoCaptor.getValue().getYear());
        Assertions.assertEquals(carDTO.getPrice(), dtoCaptor.getValue().getPrice());
        Assertions.assertEquals(carDTO.getCondition(), dtoCaptor.getValue().getCondition());
    }

    @Test
    @DisplayName("Тест удаления автомобиля по id (найден)")
    void testDeleteCarById() throws Exception {
        when(carService.deleteCarById(1)).thenReturn(true);

        mockMvc.perform(delete("/api/cars/1"))
                .andExpect(status().isNoContent());

        verify(carService).deleteCarById(1);
    }

    @Test
    @DisplayName("Тест удаления автомобиля по id (не найден)")
    void testDeleteCarByIdNotFound() throws Exception {
        when(carService.deleteCarById(99)).thenReturn(false);

        mockMvc.perform(delete("/api/cars/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Тест обновления автомобиля")
    void testUpdateCar() throws Exception {
        CarDTO updatedCarDTO = new CarDTO(1, "Ford", "Mustang", 2023, 50000.00, CarCondition.NEW);
        Car updatedCar = new Car(1, "Ford", "Mustang", 2023, 50000.00, CarCondition.NEW);

        when(carMapper.carDTOToCar(any(CarDTO.class))).thenReturn(updatedCar);
        when(carService.getCarById(1)).thenReturn(updatedCar);
        mockMvc.perform(put("/api/cars/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedCarDTO)))
                .andExpect(status().isOk());

        verify(carService, times(1)).updateCar(1, updatedCar);
    }

    @Test
    @DisplayName("Тест обновления автомобиля с ошибкой")
    void testUpdateCarInternalServerError() throws Exception {
        CarDTO updatedCarDTO = new CarDTO(1, "Ford", "Mustang", 2023, 50000.00, CarCondition.NEW);
        doThrow(new RuntimeException("Внутренняя ошибка")).when(carService).updateCar(eq(999), any(Car.class));

        mockMvc.perform(put("/api/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedCarDTO)))
                .andExpect(status().isNotFound());
    }
}