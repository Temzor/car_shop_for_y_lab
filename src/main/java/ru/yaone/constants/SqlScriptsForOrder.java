package ru.yaone.constants;

/**
 * Класс {@code SqlScriptsForOrder} содержит SQL-скрипты, используемые для управления
 * заказами в магазине автомобилей.
 *
 * <p>Содержит статические константы, представляющие SQL-запросы для операций с заказами.</p>
 *
 * <p>Запросы позволяют добавлять, извлекать, обновлять и удалять записи о заказах
 * из таблицы {@code orders} в базе данных.</p>
 */
public class SqlScriptsForOrder {

    /**
     * SQL-запрос для добавления нового заказа в таблицу {@code orders}.
     *
     * <p>Запрос вставляет значения для идентификатора клиента, идентификатора автомобиля,
     * даты создания и статуса заказа, устанавливая уникальный идентификатор с помощью
     * последовательности {@code orders_id_seq}. Возвращает сгенерированный идентификатор.</p>
     */
    public static final String ADD_ORDER = """
            INSERT INTO car_shop.orders (id, client_id, car_id, creation_date, status)
            VALUES (nextval('orders_id_seq'), ?, ?, ?, ?) RETURNING id;
            """;

    /**
     * SQL-запрос для получения всех заказов из таблицы {@code orders}.
     *
     * <p>Возвращает список всех заказов с информацией о клиентах и автомобилях,
     * включая идентификаторы, имя клиента, контактные данные, идентификатор автомобиля,
     * информацию о машине, дату создания заказа и его статус.</p>
     */
    public static final String GET_ALL_ORDERS = """ 
            SELECT o.id, o.client_id, c.client_name, c.contact_info,
                   o.car_id, car.make, car.model, car.year, car.price, car.condition,
                   o.creation_date, o.status
            FROM car_shop.orders o
            JOIN car_shop.clients c ON o.client_id = c.id
            JOIN car_shop.cars car ON o.car_id = car.id;
            """;

    /**
     * SQL-запрос для получения заказа по его идентификатору.
     *
     * <p>Возвращает данные о конкретном заказе, включая информацию о клиенте и автомобиле,
     * если заказ существует, с помощью его уникального идентификатора.</p>
     */
    public static final String GET_ORDER_BY_ID = """
            SELECT o.id, o.client_id, c.client_name, c.contact_info,
                   o.car_id, car.make, car.model, car.year, car.price, car.condition,
                   o.creation_date, o.status
            FROM car_shop.orders o
            JOIN car_shop.clients c ON o.client_id = c.id
            JOIN car_shop.cars car ON o.car_id = car.id
            WHERE o.id = ?;
            """;

    /**
     * SQL-запрос для обновления информации о заказе.
     *
     * <p>Позволяет обновить идентификатор клиента, идентификатор автомобиля, дату создания и
     * статус заказа по его уникальному идентификатору.</p>
     */
    public static final String UPDATE_ORDER = """
            UPDATE car_shop.orders SET client_id = ?, car_id = ?, creation_date = ?, status = ?
            WHERE id = ?;
            """;

    /**
     * SQL-запрос для удаления заказа из таблицы {@code orders} по его идентификатору.
     *
     * <p>Удаляет запись о конкретном заказе, если он существует.</p>
     */
    public static final String DELETE_ORDER = """
            DELETE FROM car_shop.orders WHERE id = ?;
            """;
}