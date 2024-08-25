package ru.yaone.constants;

/**
 * Класс {@code SqlScriptsForClients} содержит SQL-скрипты, используемые для управления
 * клиентами в магазине автомобилей.
 *
 * <p>Содержит статические константы, представляющие SQL-запросы для операций с клиентами.</p>
 *
 * <p>Запросы позволяют добавлять, извлекать, обновлять и удалять записи о клиентах
 * из таблицы {@code clients} в базе данных.</p>
 */
public class SqlScriptsForClients {

    /**
     * SQL-запрос для добавления нового клиента в таблицу {@code clients}.
     *
     * <p>Запрос вставляет значения для имени клиента и контактной информации,
     * устанавливая уникальный идентификатор с помощью последовательности {@code car_shop.clients_id_seq}.
     * Возвращает сгенерированный идентификатор.</p>
     */
    public static final String ADD_CLIENT = """
            INSERT INTO car_shop.clients (id, client_name, contact_info)
            VALUES (nextval('car_shop.clients_id_seq'), ?, ?) RETURNING id;
            """;

    /**
     * SQL-запрос для получения всех клиентов из таблицы {@code clients}.
     *
     * <p>Возвращает список всех клиентов с их идентификаторами, именами и контактными данными.</p>
     */
    public static final String GET_ALL_CLIENTS = """
            SELECT id, client_name, contact_info FROM car_shop.clients;
            """;

    /**
     * SQL-запрос для получения клиента по его идентификатору.
     *
     * <p>Возвращает данные о конкретном клиенте, если он существует, с помощью его уникального идентификатора.</p>
     */
    public static final String GET_CLIENTS_BY_ID = """
            SELECT id, client_name, contact_info FROM car_shop.clients WHERE id = ?;
            """;

    /**
     * SQL-запрос для удаления клиента из таблицы {@code clients} по его идентификатору.
     *
     * <p>Удаляет запись о конкретном клиенте, если он существует.</p>
     */
    public static final String DELETED_CLIENTS_BY_ID = """
            DELETE FROM car_shop.clients WHERE id = ?;
            """;

    /**
     * SQL-запрос для обновления информации о клиенте.
     *
     * <p>Позволяет обновить имя клиента и контактную информацию по его уникальному идентификатору.</p>
     */
    public static final String UPDATE_CLIENT = """
            UPDATE car_shop.clients SET client_name = ?, contact_info = ? WHERE id = ?;
            """;
}