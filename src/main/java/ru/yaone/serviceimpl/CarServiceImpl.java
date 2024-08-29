package ru.yaone.serviceimpl;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import ru.yaone.aspect.annotation.Loggable;
import ru.yaone.model.Car;
import ru.yaone.model.enumeration.CarCondition;
import ru.yaone.repository.CarRepository;
import ru.yaone.services.CarService;

import java.util.List;

@Service
@Loggable("Логирование класса CarServiceImpl")
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;

    @Loggable("Логирование метода CarServiceImpl.addCar")
    @Override
    public void addCar(Car car) {
        carRepository.addCar(car);
    }

    @Loggable("Логирование метода CarServiceImpl.getAllCars")
    @Override
    public List<Car> getAllCars() {
        return carRepository.getAllCars();
    }

    @Loggable("Логирование метода CarServiceImpl.getCarById")
    @Override
    public Car getCarById(int id) {
        return carRepository.getCarById(id);
    }

    @Loggable("Логирование метода CarServiceImpl.updateCar")
    @Override
    public void updateCar(int id, Car updatedCarDTO) {
        carRepository.updateCar(id, updatedCarDTO);
    }

    @Loggable("Логирование метода CarServiceImpl.deleteCarById")
    @Override
    public boolean deleteCarById(int id) {
        return carRepository.deleteCarById(id);
    }

    @Loggable("Логирование метода CarServiceImpl.searchCars")
    @Override
    public List<Car> searchCars(String make, String model, int year, double price, CarCondition condition) {
        return carRepository.searchCars(make, model, year, price, condition);
    }
}
