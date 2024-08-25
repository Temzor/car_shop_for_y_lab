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
import ru.yaone.dto.CarDTO;
import ru.yaone.mapper.CarMapper;
import ru.yaone.model.Car;
import ru.yaone.services.CarService;

import jakarta.validation.Valid;

import java.util.List;

/**
 * Контроллер для работы с автомобилями.
 */
@Loggable
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@Tag(name = "Car Controller", description = "Операции по управлению автомобилями")
public class CarController {

    private final CarService carService;
    private final CarMapper carMapper;

    /**
     * Получить всех автомобилей.
     *
     * @return Список автомобилей в формате CarDTO.
     */
    @GetMapping(value = "/cars", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Получить всех автомобилей")
    public ResponseEntity<List<CarDTO>> getAllCars() {
        List<Car> cars = carService.getAllCars();
        List<CarDTO> carsDTO = carMapper.carToCarDTO(cars);
        return ResponseEntity.ok(carsDTO);
    }

    /**
     * Получить автомобиль по ID.
     *
     * @param id Идентификатор автомобиля.
     * @return Автомобиль в формате CarDTO, если найден, иначе 404.
     */
    @GetMapping("/cars/{id}")
    @Operation(summary = "Получить автомобиль по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно найден автомобиль"),
            @ApiResponse(responseCode = "404", description = "Автомобиль не найден")
    })
    public ResponseEntity<CarDTO> getCarById(
            @Parameter(description = "ID автомобиля", required = true) @PathVariable("id") int id) {

        Car car = carService.getCarById(id);
        return car != null
                ? ResponseEntity.ok(carMapper.carToCarDTO(car))
                : ResponseEntity.notFound().build();
    }

    /**
     * Добавить новый автомобиль.
     *
     * @param carDTO Данные автомобиля для добавления.
     * @return Статус 201, если автомобиль успешно добавлен, или 400 при неправильных данных.
     */
    @PostMapping(value = "/cars")
    @Operation(summary = "Добавить новый автомобиль")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Автомобиль успешно добавлен"),
            @ApiResponse(responseCode = "400", description = "Неверные данные автомобиля")
    })
    public ResponseEntity<Void> addCar(
            @Parameter(description = "Данные автомобиля", required = true) @RequestBody @Valid CarDTO carDTO) {

        Car car = carMapper.carDTOToCar(carDTO);
        carService.addCar(car);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Удалить автомобиль по ID.
     *
     * @param id Идентификатор автомобиля для удаления.
     * @return Статус 204, если автомобиль успешно удален, или 404, если автомобиль не найден.
     */
    @DeleteMapping("/cars/{id}")
    @Operation(summary = "Удалить автомобиль по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Автомобиль успешно удален"),
            @ApiResponse(responseCode = "404", description = "Автомобиль не найден")
    })
    public ResponseEntity<Void> deleteCarById(
            @Parameter(description = "ID автомобиля", required = true) @PathVariable("id") int id) {

        boolean isDeleted = carService.deleteCarById(id);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    /**
     * Обновить информацию об автомобиле.
     *
     * @param id            Идентификатор автомобиля для обновления.
     * @param updatedCarDTO Обновленные данные автомобиля.
     * @return Статус 200, если информация об автомобиле успешно обновлена,
     * статус 400, если данные неверны,
     * статус 404, если автомобиль не найден,
     * статус 500, если произошла ошибка сервера.
     */
    @PutMapping("/cars/{id}")
    @Operation(summary = "Обновить информацию об автомобиле")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация об автомобиле обновлена"),
            @ApiResponse(responseCode = "400", description = "Неверные данные автомобиля"),
            @ApiResponse(responseCode = "404", description = "Автомобиль не найден"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    public ResponseEntity<Void> updateCar(
            @Parameter(description = "ID автомобиля", required = true) @PathVariable("id") int id,
            @Parameter(description = "Обновленные данные автомобиля", required = true) @RequestBody @Valid CarDTO updatedCarDTO) {

        Car existingCar = carService.getCarById(id);
        if (existingCar == null) {
            return ResponseEntity.notFound().build();
        }

        Car updatedCar = carMapper.carDTOToCar(updatedCarDTO);
        carService.updateCar(id, updatedCar);
        return ResponseEntity.ok().build();
    }
}