package ru.yaone.constants;

/**
 * Класс {@code SqlScriptsForCar} содержит SQL-скрипты, используемые для управления
 * автомобилями в магазине.
 *
 * <p>Содержит статические константы, представляющие SQL-запросы для операций с автомобилями.</p>
 *
 * <p>Запросы позволяют добавлять, извлекать, обновлять и удалять записи о автомобилях
 * из таблицы {@code cars} в базе данных.</p>
 */
public class SqlScriptsForCar {

    /**
     * SQL-запрос для добавления нового автомобиля в таблицу {@code cars}.
     *
     * <p>Запрос вставляет значения для марки, модели, года, цены и состояния автомобиля,
     * устанавливая уникальный идентификатор с помощью последовательности {@code cars_id_seq}.
     * Возвращает сгенерированный идентификатор.</p>
     */
    public static final String ADD_CAR = """
            INSERT INTO car_shop.cars (id, make, model, year, price, condition)
            VALUES (nextval('cars_id_seq'), ?, ?, ?, ?, ?) RETURNING id;
            """;

    /**
     * SQL-запрос для получения всех автомобилей из таблицы {@code cars}.
     *
     * <p>Возвращает список всех автомобилей с их идентификаторами, марками, моделями,
     * годами, ценами и состоянием.</p>
     */
    public static final String GET_ALL_CARS = """
            SELECT id, make, model, year, price, condition FROM car_shop.cars;
            """;

    /**
     * SQL-запрос для получения автомобиля по его идентификатору.
     *
     * <p>Возвращает данные о конкретном автомобиле, если он существует, с помощью его уникального идентификатора.</p>
     */
    public static final String GET_CAR_BY_ID = """
            SELECT id, make, model, year, price, condition FROM car_shop.cars
            WHERE id = ?;
            """;

    /**
     * SQL-запрос для обновления информации об автомобиле.
     *
     * <p>Позволяет обновить марку, модель, год, цену и состояние автомобиля
     * по его уникальному идентификатору.</p>
     */
    public static final String UPDATE_CAR = """
            UPDATE car_shop.cars SET make = ?, model = ?, year = ?, price = ?, condition = ?
            WHERE id = ?;
            """;

    /**
     * SQL-запрос для удаления автомобиля из таблицы {@code cars} по его идентификатору.
     *
     * <p>Удаляет запись о конкретном автомобиле, если он существует.</p>
     */
    public static final String DELETE_CAR = """
            DELETE FROM car_shop.cars WHERE id = ?;
            """;

    /**
     * SQL-запрос для поиска автомобилей по различным критериям.
     *
     * <p>Возвращает список автомобилей, удовлетворяющих критериям поиска по марке,
     * модели, году, цене и состоянию.</p>
     */
    public static final String SEARCH_CARS = """
            SELECT id, make, model, year, price, condition FROM car_shop.cars
            WHERE make LIKE ? AND model LIKE ? AND year LIKE ? AND price >= ? AND condition LIKE ?;
            """;
}