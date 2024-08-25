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
import ru.yaone.dto.CarDTO;
import ru.yaone.mapper.CarMapper;
import ru.yaone.model.Car;
import ru.yaone.model.enumeration.CarCondition;
import ru.yaone.services.CarService;
import jakarta.validation.Valid;

import java.util.List;

@Loggable
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@Api(value = "Car Controller", tags = "Операции по управлению автомобилями")
public class CarController {

    private final CarService carService;
    private final CarMapper carMapper;

    @GetMapping(value = "/cars", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Получить всех автомобилей", response = CarDTO.class, responseContainer = "List")
    public ResponseEntity<List<CarDTO>> getAllCars() {
        List<Car> cars = carService.getAllCars();
        List<CarDTO> carsDTO = carMapper.carToCarDTO(cars);
        return ResponseEntity.ok(carsDTO);
    }

    @GetMapping("/cars/{id}")
    @ApiOperation(value = "Получить автомобиль по ID", response = CarDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешно найден автомобиль"),
            @ApiResponse(code = 404, message = "Автомобиль не найден")
    })
    public ResponseEntity<CarDTO> getCarById(
            @ApiParam(value = "ID автомобиля", required = true) @PathVariable("id") int id) {
        Car car = carService.getCarById(id);
        if (car != null) {
            CarDTO carDTO = carMapper.carToCarDTO(car);
            return ResponseEntity.ok(carDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "/cars")
    @ApiOperation(value = "Добавить новый автомобиль")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Автомобиль успешно добавлен"),
            @ApiResponse(code = 400, message = "Неверные данные автомобиля")
    })
    public ResponseEntity<Void> addCar(
            @ApiParam(value = "Данные автомобиля", required = true) @RequestBody @Valid CarDTO carDTO) {
        Car car = carMapper.carDTOToCar(carDTO);
        carService.addCar(car);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/cars/{id}")
    @ApiOperation(value = "Удалить автомобиль по ID")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Автомобиль успешно удален"),
            @ApiResponse(code = 404, message = "Автомобиль не найден")
    })
    public ResponseEntity<Void> deleteCarById(
            @ApiParam(value = "ID автомобиля", required = true) @PathVariable("id") int id) {
        boolean isDeleted = carService.deleteCarById(id);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PutMapping("/cars/{id}")
    @ApiOperation(value = "Обновить информацию об автомобиле")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Информация об автомобиле обновлена"),
            @ApiResponse(code = 400, message = "Неверные данные автомобиля"),
            @ApiResponse(code = 404, message = "Автомобиль не найден"),
            @ApiResponse(code = 500, message = "Ошибка сервера")
    })
    public ResponseEntity<Void> updateCar(
            @ApiParam(value = "ID автомобиля", required = true) @PathVariable("id") int id,
            @ApiParam(value = "Обновленные данные автомобиля", required = true) @RequestBody @Valid CarDTO updatedCarDTO) {
        if (updatedCarDTO == null) {
            return ResponseEntity.badRequest().build();
        }
        Car updatedCar = carMapper.carDTOToCar(updatedCarDTO);
        try {
            if (carService.getCarById(id) == null) {
                return ResponseEntity.notFound().build();
            }
            carService.updateCar(id, updatedCar);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/cars/search")
    @ApiOperation(value = "Поиск автомобилей по параметрам")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешный поиск автомобилей"),
            @ApiResponse(code = 500, message = "Ошибка сервера")
    })
    public ResponseEntity<List<CarDTO>> searchCars(
            @ApiParam(value = "Имя клиента") @RequestParam(required = false) String clientName,
            @ApiParam(value = "Модель автомобиля") @RequestParam(required = false) String model,
            @ApiParam(value = "Год выпуска") @RequestParam(required = false) Integer year,
            @ApiParam(value = "Цена") @RequestParam(required = false) Double price,
            @ApiParam(value = "Состояние автомобиля") @RequestParam(required = false) CarCondition condition) {
        try {
            List<Car> cars = carService.searchCars(clientName, model, year, price, condition);
            List<CarDTO> carsDTO = carMapper.carToCarDTO(cars);
            return ResponseEntity.ok(carsDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}