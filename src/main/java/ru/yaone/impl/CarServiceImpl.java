package ru.yaone.impl;

import ru.yaone.aspect.annotation.Loggable;
import ru.yaone.constants.SqlScriptsForCar;
import ru.yaone.dto.CarDTO;
import ru.yaone.manager.DatabaseConnectionManager;
import ru.yaone.model.enumeration.CarCondition;
import ru.yaone.services.CarService;

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
@Loggable("Логирование класса CarServiceImpl")
public class CarServiceImpl implements CarService {

    /**
     * Добавляет новый автомобиль в базу данных.
     *
     * <p>Метод принимает объект Car и записывает его данные
     * в таблицу <code>cars</code> базы данных, включая марку, модель,
     * год выпуска, цену и состояние автомобиля.</p>
     *
     * @param carDTO объект автомобиля, который нужно добавить
     * @throws RuntimeException если произошла ошибка во время выполнения SQL-запроса
     */
    @Loggable("Логирование метода CarServiceImpl.addCar")
    @Override
    public void addCar(CarDTO carDTO) {
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(SqlScriptsForCar.ADD_CAR)) {
            preparedStatement.setString(1, carDTO.getMake());
            preparedStatement.setString(2, carDTO.getModel());
            preparedStatement.setInt(3, carDTO.getYear());
            preparedStatement.setDouble(4, carDTO.getPrice());
            preparedStatement.setString(5, carDTO.getCondition().toString());
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    new CarDTO(rs.getInt(1),
                            carDTO.getMake(),
                            carDTO.getModel(),
                            carDTO.getYear(),
                            carDTO.getPrice(),
                            carDTO.getCondition());
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
    @Loggable("Логирование метода CarServiceImpl.getAllCars")
    @Override
    public List<CarDTO> getAllCars() {
        List<CarDTO> carsDTO = new ArrayList<>();
        try (Connection conn = DatabaseConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SqlScriptsForCar.GET_ALL_CARS)) {
            while (rs.next()) {
                CarDTO carDTO = new CarDTO(
                        rs.getInt("id"),
                        rs.getString("make"),
                        rs.getString("model"),
                        rs.getInt("year"),
                        rs.getDouble("price"),
                        CarCondition.valueOf(rs.getString("condition"))
                );
                carsDTO.add(carDTO);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка SQL: " + e.getMessage());
            throw new RuntimeException("Ошибка при получении автомобилей", e);
        }
        return carsDTO;
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
    @Loggable("Логирование метода CarServiceImpl.getCarById")
    @Override
    public CarDTO getCarById(int id) {
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(SqlScriptsForCar.GET_CAR_BY_ID)) {
            preparedStatement.setInt(1, id);
            CarDTO rs = getCarDTO(preparedStatement);
            if (rs != null) {
                return rs;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении автомобиля по ID", e);
        }
        return null;
    }

    /**
     * Получает объект CarDTO из результата выполнения SQL-запроса.
     * <p>Метод выполняет запрос через переданный {@code PreparedStatement}
     * и извлекает данные автомобиля из {@code ResultSet}. Если запись найдена,
     * создаётся и возвращается объект CarDTO, иначе возвращается {@code null}.</p>
     *
     * @param preparedStatement подготовленный SQL-запрос для извлечения данных автомобиля
     * @return объект CarDTO с данными автомобиля или {@code null}, если запись не найдена
     * @throws SQLException если произошла ошибка при выполнении SQL-запроса
     */
    @Loggable("Логирование метода CarServiceImpl.getCarDTO")
    private static CarDTO getCarDTO(PreparedStatement preparedStatement) throws SQLException {
        try (ResultSet rs = preparedStatement.executeQuery()) {
            if (rs.next()) {
                return new CarDTO(
                        rs.getInt("id"),
                        rs.getString("make"),
                        rs.getString("model"),
                        rs.getInt("year"),
                        rs.getDouble("price"),
                        CarCondition.valueOf(rs.getString("condition"))
                );
            }
        }
        return null;
    }

    /**
     * Обновляет данные автомобиля в базе данных.
     *
     * <p>Метод принимает идентификатор автомобиля и объект Car с обновленными данными.
     * Он выполняет SQL-запрос для обновления записи соответствующего автомобиля
     * в таблице <code>cars</code>.</p>
     *
     * @param id            идентификатор автомобиля, который необходимо обновить
     * @param updatedCarDTO объект Car с новыми значениями для обновления
     * @throws RuntimeException если произошла ошибка во время выполнения SQL-запроса
     */
    @Loggable("Логирование метода CarServiceImpl.updateCar")
    @Override
    public void updateCar(int id, CarDTO updatedCarDTO) {
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(SqlScriptsForCar.UPDATE_CAR)) {
            preparedStatement.setString(1, updatedCarDTO.getMake());
            preparedStatement.setString(2, updatedCarDTO.getModel());
            preparedStatement.setInt(3, updatedCarDTO.getYear());
            preparedStatement.setDouble(4, updatedCarDTO.getPrice());
            preparedStatement.setString(5, updatedCarDTO.getCondition().toString());
            preparedStatement.setInt(6, id);
        } catch (SQLException e) {
            System.err.println("Ошибка SQL: " + e.getMessage());
            throw new RuntimeException("Ошибка при обновлении автомобилей", e);
        }
    }

    /**
     * Удаляет автомобиль из базы данных по его идентификатору.
     *
     * <p>Метод выполняет SQL-запрос для удаления записи
     * из таблицы <code>cars</code> с указанным идентификатором.</p>
     *
     * @param id идентификатор автомобиля, который необходимо удалить
     * @throws RuntimeException если произошла ошибка во время выполнения SQL-запроса
     */
    @Loggable("Логирование метода CarServiceImpl.deleteCarById")
    @Override
    public boolean deleteCarById(int id) {
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(SqlScriptsForCar.DELETE_CAR)) {
            preparedStatement.setInt(1, id);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Клиент успешно удален.");
                return true;
            } else {
                System.out.println("Клиент не найден.");
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении автомобиля", e);
        }
    }

    /**
     * Ищет автомобили в базе данных по заданным критериям.
     *
     * <p>Метод выполняет SQL-запрос для поиска автомобилей по марке, модели,
     * году выпуска, цене и состоянию. Возвращает список автомобилей,
     * соответствующих критериям поиска.</p>
     *
     * @param make      марка автомобилей для поиска
     * @param model     модель автомобилей для поиска
     * @param year      год выпуска автомобилей для поиска
     * @param price     минимальная цена автомобилей для поиска
     * @param condition состояние автомобилей для поиска
     * @return список автомобилей, соответствующих критериям поиска
     * @throws RuntimeException если произошла ошибка во время выполнения SQL-запроса
     */
    @Loggable("Логирование метода CarServiceImpl.searchCars")
    @Override
    public List<CarDTO> searchCars(String make, String model, int year, double price, CarCondition condition) {
        List<CarDTO> carDTOs = new ArrayList<>();
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(SqlScriptsForCar.SEARCH_CARS)) {
            preparedStatement.setString(1, "%" + make + "%");
            preparedStatement.setString(2, "%" + model + "%");
            preparedStatement.setInt(3, year);
            preparedStatement.setDouble(4, price);
            preparedStatement.setString(5, condition.toString());
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    CarDTO carDTO = new CarDTO(
                            rs.getInt("id"),
                            rs.getString("make"),
                            rs.getString("model"),
                            rs.getInt("year"),
                            rs.getDouble("price"),
                            CarCondition.valueOf(rs.getString("condition"))
                    );
                    carDTOs.add(carDTO);
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка SQL: " + e.getMessage());

            throw new RuntimeException("Ошибка при поиске автомобилей", e);
        }
        return carDTOs;
    }
}