package ru.yaone.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.yaone.dto.CarDTO;
import ru.yaone.model.Car;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CarMapper {
    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "make", target = "make"),
            @Mapping(source = "model", target = "model"),
            @Mapping(source = "year", target = "year"),
            @Mapping(source = "price", target = "price"),
            @Mapping(source = "condition", target = "condition")
    })
    CarDTO carToCarDTO(Car car);

    List<CarDTO> carToCarDTO(List<Car> cars);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "make", target = "make"),
            @Mapping(source = "model", target = "model"),
            @Mapping(source = "year", target = "year"),
            @Mapping(source = "price", target = "price"),
            @Mapping(source = "condition", target = "condition")
    })
    Car carDTOToCar(CarDTO carDTO);

    List<Car> carDTOToCar(List<CarDTO> carsDTO);
}