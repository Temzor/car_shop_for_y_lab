package ru.yaone.constans;

/**
 * Класс {@code SqlScriptsForAudit} содержит SQL-скрипты, используемые для ведения аудита.
 *
 * <p>Содержит статические константы, представляющие SQL-запросы для операций с аудиторскими записями.</p>
 *
 * <p>Данный класс служит для обеспечения единообразия и удобства при работе с SQL-запросами в других частях приложения.</p>
 *
 * @author Ваше имя
 * @version 1.0
 */
public class SqlScriptsForAudit {

    /**
     * SQL-запрос для вставки новой записи в таблицу аудита.
     *
     * <p>Запрос вставляет имя метода, аргументы метода, время выполнения,
     * результат и временную метку в таблицу {@code audit_log}.</p>
     */
    public static final String INSERT_EVENTS = """
            INSERT INTO car_shop.audit_log (method_name, method_args, execution_time, result, timestamp)
            VALUES (?, ?, ?, ?, ?)
            """;
}