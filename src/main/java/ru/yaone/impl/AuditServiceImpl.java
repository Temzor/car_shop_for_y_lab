package ru.yaone.impl;

import ru.yaone.manager.DatabaseConnectionManager;
import ru.yaone.model.AuditLog;
import ru.yaone.model.User;
import ru.yaone.model.enumeration.UserRole;
import ru.yaone.services.AuditService;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Реализация сервиса аудита для управления логированием действий пользователей.
 *
 * <p>Класс предоставляет методы для записи действий пользователей в
 * базу данных и для извлечения всех логов аудита.</p>
 *
 * <p>Логи сохраняются в таблице <code>audit_logs</code>, которая содержит
 * информацию о пользователе, сообщении лога и времени действия.</p>
 *
 * @see AuditService
 */
public class AuditServiceImpl implements AuditService {

    /**
     * Логирует действие пользователя, добавляя запись в базу данных.
     *
     * <p>Метод принимает пользователя и описание действия, которое будет
     * записано в лог. Логи хранятся в таблице <code>audit_logs</code> с
     * полями <code>user_id</code>, <code>log_message</code> и
     * <code>log_date</code>.</p>
     *
     * @param user   пользователь, совершивший действие
     * @param action описание действия, которое нужно залогировать
     * @throws RuntimeException если произошла ошибка во время выполнения SQL-запроса
     */
    @Override
    public void logAction(User user, String action) {
        String sql = "INSERT INTO car_shop.audit_logs (user_id, log_message, log_date) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, user.id());
            pstmt.setString(2, action);
            pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка SQL: " + e.getMessage());
            throw new RuntimeException("Ошибка при добавлении логов", e);
        }
    }

    /**
     * Получает все логи аудита из базы данных.
     *
     * <p>Метод выполняет SQL-запрос для извлечения всех записей
     * из таблицы <code>audit_logs</code> и возвращает список объёмов
     * {@link AuditLog}, которые содержат информацию о времени,
     * пользователе и действии.</p>
     *
     * @return список всех логов аудита
     * @throws RuntimeException если произошла ошибка во время выполнения SQL-запроса
     */
    @Override
    public List<AuditLog> getAllLogs() {
        List<AuditLog> auditLogs = new ArrayList<>();
        String sql = """
                SELECT a.user_id, a.log_message, a.log_date,
                       c.username, c.password, c.role
                FROM car_shop.audit_logs a
                JOIN car_shop.users c ON a.user_id = c.id
                ORDER BY a.log_date DESC
                """;
        try (Connection conn = DatabaseConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                User user = new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        UserRole.valueOf(rs.getString("role"))
                );
                LocalDateTime timestamp = rs.getTimestamp("log_date").toLocalDateTime();
                String action = rs.getString("log_message");
                auditLogs.add(new AuditLog(timestamp, user, action));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка SQL: " + e.getMessage());
            throw new RuntimeException("Ошибка при получении лога", e);
        }
        return auditLogs;
    }
}