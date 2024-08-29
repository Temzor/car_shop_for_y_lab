package ru.yaone.constants;

/**
 * Класс {@code SqlScriptsForUsers} содержит SQL-скрипты, используемые для управления
 * пользователями в системе car-shop.
 *
 * <p>Содержит статические константы, представляющие SQL-запросы для операций с пользователями.</p>
 *
 * <p>Запросы позволяют добавлять, извлекать, обновлять и удалять записи о пользователях
 * из таблицы {@code users} в базе данных.</p>
 *
 * @author Ваше имя
 * @version 1.0
 */
public class SqlScriptsForUsers {

    /**
     * SQL-запрос для добавления нового пользователя в таблицу {@code users}.
     *
     * <p>Запрос вставляет значения для имени пользователя, пароля и роли, устанавливая
     * уникальный идентификатор с помощью последовательности {@code car_shop.users_id_seq}.
     * Возвращает сгенерированный идентификатор.</p>
     */
    public static final String ADD_USER = """
            INSERT INTO car_shop.users (id, username, password, role)
            VALUES (nextval('car_shop.users_id_seq'), ?, ?, ?) RETURNING id;
            """;

    /**
     * SQL-запрос для проверки существования пользователя по имени.
     *
     * <p>Возвращает количество записей с указанным именем пользователя.</p>
     */
    public static final String GET_USER = """
            SELECT COUNT(*) FROM car_shop.users WHERE username = ?;
            """;

    /**
     * SQL-запрос для получения всех пользователей из таблицы {@code users}.
     *
     * <p>Возвращает список всех пользователей с их идентификаторами, именами пользователей,
     * паролями и ролями.</p>
     */
    public static final String GET_ALL_USERS = """
            SELECT id, username, password, role FROM car_shop.users;
            """;

    /**
     * SQL-запрос для получения пользователя по его идентификатору.
     *
     * <p>Возвращает данные о конкретном пользователе, включая имя пользователя, пароль и
     * роль, если пользователь существует, с помощью его уникального идентификатора.</p>
     */
    public static final String GET_USER_BY_ID = """
            SELECT id, username, password, role FROM car_shop.users WHERE id = ?;
            """;

    /**
     * SQL-запрос для обновления информации о пользователе.
     *
     * <p>Позволяет обновить имя пользователя, пароль и роль по его уникальному идентификатору.</p>
     */
    public static final String UPDATE_USER = """
            UPDATE car_shop.users SET username = ?, password = ?, role = ?
            WHERE id = ?;
            """;
}