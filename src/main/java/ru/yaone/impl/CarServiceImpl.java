package ru.yaone.impl;

import ru.yaone.manager.DatabaseConnectionManager;
import ru.yaone.model.Car;
import ru.yaone.model.enumeration.CarCondition;
import ru.yaone.services.CarService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Реализация сервиса для управления автомобилями в магазине.
 *
 * <p>Класс предоставляет методы для добавления автомобилей, получения всех автомобилей
 * и поиска автомобиля по его идентификатору. Все операции с базой данных
 * выполняются через JDBC.</p>
 *
 * @see CarService
 */
public class CarServiceImpl implements CarService {

    /**
     * Добавляет новый автомобиль в базу данных.
     *
     * <p>Метод принимает объект Car и записывает его данные
     * в таблицу <code>cars</code> базы данных, включая марку, модель,
     * год выпуска, цену и состояние автомобиля.</p>
     *
     * @param car объект автомобиля, который нужно добавить
     * @throws RuntimeException если произошла ошибка во время выполнения SQL-запроса
     */
    @Override
    public void addCar(Car car) {
        String sql = """
                INSERT INTO car_shop.cars (id, make, model, year, price, condition)
                VALUES (nextval('cars_id_seq'), ?, ?, ?, ?, ?) RETURNING id;
                """;
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, car.make());
            pstmt.setString(2, car.model());
            pstmt.setInt(3, car.year());
            pstmt.setDouble(4, car.price());
            pstmt.setString(5, car.condition().toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    new Car(rs.getInt(1), car.make(), car.model(), car.year(), car.price(), car.condition());
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка SQL: " + e.getMessage());
            throw new RuntimeException("Ошибка при добавлении заказа", e);
        }
    }

    /**
     * Получает список всех автомобилей из базы данных.
     *
     * <p>Метод выполняет SQL-запрос для извлечения всех записей
     * из таблицы <code>cars</code> и возвращает список объектов Car.</p>
     *
     * @return список всех автомобилей
     * @throws RuntimeException если произошла ошибка во время выполнения SQL-запроса
     */
    @Override
    public List<Car> getAllCars() {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT id, make, model, year, price, condition FROM car_shop.cars";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
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

    /**
     * Получает автомобиль по его идентификатору.
     *
     * <p>Метод выполняет SQL-запрос для поиска автомобиля в таблице
     * <code>cars</code> по заданному идентификатору и возвращает
     * соответствующий объект Car.</p>
     *
     * @param id идентификатор автомобиля, который нужно получить
     * @return объект Car с указанным идентификатором или <code>null</code>,
     * если автомобиль не найден
     * @throws RuntimeException если произошла ошибка во время выполнения SQL-запроса
     */
    @Override
    public Car getCarById(int id) {
        String sql = "SELECT id, make, model, year, price, condition FROM car_shop.cars WHERE id = ?";
        try (Connection conn = DatabaseConnectionManager.getConnection();

             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
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
            throw new RuntimeException("Ошибка при получении автомобиля по ID", e);
        }
        return null;
    }

    @Override
/**
 * Обновляет данные автомобиля в базе данных.
 *
 * <p>Метод принимает идентификатор автомобиля и объект Car с обновленными данными.
 * Он выполняет SQL-запрос для обновления записи соответствующего автомобиля
 * в таблице <code>cars</code>.</p>
 *
 * @param id идентификатор автомобиля, который необходимо обновить
 * @param updatedCar объект Car с новыми значениями для обновления
 * @throws RuntimeException если произошла ошибка во время выполнения SQL-запроса
 */
    public void updateCar(int id, Car updatedCar) {
        String sql = """
                UPDATE car_shop.cars SET make = ?, model = ?, year =?, price = ?, condition = ?
                WHERE id = ?;
                """;
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, updatedCar.make());
            pstmt.setString(2, updatedCar.model());
            pstmt.setInt(3, updatedCar.year());
            pstmt.setDouble(4, updatedCar.price());
            pstmt.setString(5, updatedCar.condition().toString());
            pstmt.setInt(6, id);
        } catch (SQLException e) {
            System.err.println("Ошибка SQL: " + e.getMessage());
            throw new RuntimeException("Ошибка при обновлении автомобилей", e);
        }
    }

    @Override
/**
 * Удаляет автомобиль из базы данных по его идентификатору.
 *
 * <p>Метод выполняет SQL-запрос для удаления записи
 * из таблицы <code>cars</code> с указанным идентификатором.</p>
 *
 * @param id идентификатор автомобиля, который необходимо удалить
 * @throws RuntimeException если произошла ошибка во время выполнения SQL-запроса
 */
    public void deleteCarById(int id) {
        String sql = "DELETE FROM cars WHERE id = ?";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении автомобиля", e);
        }
    }

    @Override
/**
 * Ищет автомобили в базе данных по заданным критериям.
 *
 * <p>Метод выполняет SQL-запрос для поиска автомобилей по марке, модели,
 * году выпуска, цене и состоянию. Возвращает список автомобилей,
 * соответствующих критериям поиска.</p>
 *
 * @param make марка автомобилей для поиска
 * @param model модель автомобилей для поиска
 * @param year год выпуска автомобилей для поиска
 * @param price минимальная цена автомобилей для поиска
 * @param condition состояние автомобилей для поиска
 * @return список автомобилей, соответствующих критериям поиска
 * @throws RuntimeException если произошла ошибка во время выполнения SQL-запроса
 */
    public List<Car> searchCars(String make, String model, int year, double price, CarCondition condition) {
        List<Car> cars = new ArrayList<>();
        String sql = """
                        SELECT id, make, model, year, price, condition FROM car_shop.cars
                        WHERE make LIKE ? AND model LIKE ? AND year LIKE ? AND price >= ? AND condition LIKE ?;
                """;
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + make + "%");
            pstmt.setString(2, "%" + model + "%");
            pstmt.setInt(3, year);
            pstmt.setDouble(4, price);
            pstmt.setString(5, condition.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
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