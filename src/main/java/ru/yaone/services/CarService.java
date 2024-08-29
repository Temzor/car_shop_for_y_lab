package ru.yaone.services;

import ru.yaone.model.Car;
import ru.yaone.model.enumeration.CarCondition;

import java.util.List;

/**
 * Интерфейс для сервиса управления автомобилями.
 * <p>
 * Этот интерфейс предоставляет методы для добавления,
 * получения, обновления и удаления автомобилей, а также
 * для поиска автомобилей по различным критериям.
 * </p>
 */
public interface CarService {

    /**
     * Добавляет новый автомобиль в систему.
     *
     * @param car объект автомобиля, который необходимо добавить
     */
    void addCar(Car car);

    /**
     * Получает список всех автомобилей в системе.
     *
     * @return список всех автомобилей
     */
    List<Car> getAllCars();

    /**
     * Получает автомобиль по его идентификатору.
     *
     * @param id идентификатор автомобиля
     * @return объект автомобиля с указанным идентификатором или null,
     * если автомобиль не найден
     */
    Car getCarById(int id);

    void updateCar(int id, Car updatedCarDTO);

    /**
     * Удаляет автомобиль из системы по его идентификатору.
     *
     * @param id идентификатор автомобиля, который необходимо удалить
     */
    boolean deleteCarById(int id);

    /**
     * Ищет автомобили по заданным критериям.
     *
     * @param make      марка автомобиля
     * @param model     модель автомобиля
     * @param year      год выпуска автомобиля
     * @param price     цена автомобиля
     * @param condition состояние автомобиля
     * @return список автомобилей, удовлетворяющих заданным критериям
     */
    List<Car> searchCars(String make, String model, int year, double price, CarCondition condition);
}