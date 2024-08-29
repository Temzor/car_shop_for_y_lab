package ru.yaone.repository;

import ru.yaone.model.Car;
import ru.yaone.model.enumeration.CarCondition;

import java.util.List;

public interface CarRepository {
    void addCar(Car car);

    List<Car> getAllCars();

    Car getCarById(int id);

    void updateCar(int id, Car car);

    boolean deleteCarById(int id);

    List<Car> searchCars(String make, String model, int year, double price, CarCondition condition);
}