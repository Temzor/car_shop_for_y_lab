package ru.yaone.services;

import ru.yaone.dto.CarDTO;
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
     * @param carDTO объект автомобиля, который необходимо добавить
     */
    void addCar(CarDTO carDTO);

    /**
     * Получает список всех автомобилей в системе.
     *
     * @return список всех автомобилей
     */
    List<CarDTO> getAllCars();

    /**
     * Получает автомобиль по его идентификатору.
     *
     * @param id идентификатор автомобиля
     * @return объект автомобиля с указанным идентификатором или null,
     * если автомобиль не найден
     */
    CarDTO getCarById(int id);

    void updateCar(int id, CarDTO updatedCarDTO);

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
    List<CarDTO> searchCars(String make, String model, int year, double price, CarCondition condition);
}