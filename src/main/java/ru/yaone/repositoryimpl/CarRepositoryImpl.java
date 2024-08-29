package ru.yaone.repositoryimpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.yaone.aspect.annotation.Loggable;
import ru.yaone.constants.SqlScriptsForCar;
import ru.yaone.manager.DatabaseConnectionManager;
import ru.yaone.model.Car;
import ru.yaone.model.enumeration.CarCondition;
import ru.yaone.repository.CarRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@Loggable("Логирование класса CarRepositoryImpl")
@RequiredArgsConstructor
public class CarRepositoryImpl implements CarRepository {
    private final DatabaseConnectionManager databaseConnectionManager;
    @Loggable("Логирование метода CarRepositoryImpl.addCar")
    @Override
    public void addCar(Car car) {
        try (Connection conn = databaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(SqlScriptsForCar.ADD_CAR)) {
            preparedStatement.setString(1, car.make());
            preparedStatement.setString(2, car.model());
            preparedStatement.setInt(3, car.year());
            preparedStatement.setDouble(4, car.price());
            preparedStatement.setString(5, car.condition().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка SQL: " + e.getMessage());
            throw new RuntimeException("Ошибка при добавлении автомобиля", e);
        }
    }
    @Loggable("Логирование метода CarRepositoryImpl.getAllCars")
    @Override
    public List<Car> getAllCars() {
        List<Car> cars = new ArrayList<>();
        try (Connection conn = databaseConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SqlScriptsForCar.GET_ALL_CARS)) {
            while (rs.next()) {
                Car car = new Car(
                        rs.getInt("id"),
                        rs.getString("make"),
                        rs.getString("model"),
                        rs.getInt("year"),
                        rs.getDouble("price"),
                        CarCondition.valueOf(rs.getString("condition"))
                );
                cars.add(car);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка SQL: " + e.getMessage());
            throw new RuntimeException("Ошибка при получении автомобилей", e);
        }
        return cars;
    }
    @Loggable("Логирование метода CarRepositoryImpl.getCarById")
    @Override
    public Car getCarById(int id) {
        try (Connection conn = databaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(SqlScriptsForCar.GET_CAR_BY_ID)) {
            preparedStatement.setInt(1, id);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return new Car(
                            rs.getInt("id"),
                            rs.getString("make"),
                            rs.getString("model"),
                            rs.getInt("year"),
                            rs.getDouble("price"),
                            CarCondition.valueOf(rs.getString("condition"))
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка SQL: " + e.getMessage());
            throw new RuntimeException("Ошибка при получении автомобиля по ID", e);
        }
        return null;
    }
    @Loggable("Логирование метода CarRepositoryImpl.updateCar")
    @Override
    public void updateCar(int id, Car car) {
        try (Connection conn = databaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(SqlScriptsForCar.UPDATE_CAR)) {
            preparedStatement.setString(1, car.make());
            preparedStatement.setString(2, car.model());
            preparedStatement.setInt(3, car.year());
            preparedStatement.setDouble(4, car.price());
            preparedStatement.setString(5, car.condition().toString());
            preparedStatement.setInt(6, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка SQL: " + e.getMessage());
            throw new RuntimeException("Ошибка при обновлении автомобиля", e);
        }
    }
    @Loggable("Логирование метода CarRepositoryImpl.deleteCarById")
    @Override
    public boolean deleteCarById(int id) {
        try (Connection conn = databaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(SqlScriptsForCar.DELETE_CAR)) {
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка SQL: " + e.getMessage());
            throw new RuntimeException("Ошибка при удалении автомобиля", e);
        }
    }
    @Loggable("Логирование метода CarRepositoryImpl.searchCars")
    @Override
    public List<Car> searchCars(String make, String model, int year, double price, CarCondition condition) {
        List<Car> cars = new ArrayList<>();
        try (Connection conn = databaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(SqlScriptsForCar.SEARCH_CARS)) {
            preparedStatement.setString(1, "%" + make + "%");
            preparedStatement.setString(2, "%" + model + "%");
            preparedStatement.setInt(3, year);
            preparedStatement.setDouble(4, price);
            preparedStatement.setString(5, condition.toString());
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    Car car = new Car(
                            rs.getInt("id"),
                            rs.getString("make"),
                            rs.getString("model"),
                            rs.getInt("year"),
                            rs.getDouble("price"),
                            CarCondition.valueOf(rs.getString("condition"))
                    );
                    cars.add(car);
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка SQL: " + e.getMessage());
            throw new RuntimeException("Ошибка при поиске автомобилей", e);
        }
        return cars;
    }
}