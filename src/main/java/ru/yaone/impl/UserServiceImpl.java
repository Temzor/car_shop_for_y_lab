package ru.yaone.impl;

import ru.yaone.model.User;
import ru.yaone.model.enumeration.UserRole;
import ru.yaone.services.UserService;
import ru.yaone.manager.DatabaseConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Реализация сервиса пользователей для управления операциями с пользователями.
 */
public class UserServiceImpl implements UserService {

    /**
     * Добавляет нового пользователя в базу данных.
     *
     * <p>Перед добавлением проверяет, не занято ли имя пользователя. Если имя занято,
     * выбрасывается RuntimeException. Если имя свободно, пользователь добавляется в базу данных.</p>
     *
     * @param user объект пользователя, который необходимо добавить в систему
     * @throws RuntimeException если имя пользователя уже занято или произошла ошибка во время SQL-запроса
     */
    @Override
    public void addUser(User user) {
        if (isUsernameTaken(user.username())) {
            throw new RuntimeException("Пользователь с именем " + user.username() + " уже существует.");
        }
        String sql = "INSERT INTO car_shop.users (id, username, password, role) VALUES (nextval('car_shop.users_id_seq'), ?, ?, ?) RETURNING id";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.username());
            pstmt.setString(2, user.password());
            pstmt.setString(3, user.role().toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    new User(rs.getInt(1), user.username(), user.password(), user.role());
                    System.out.println("Пользователь успешно зарегистрирован.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка SQL: " + e.getMessage() + sql);
            throw new RuntimeException("Ошибка при добавлении пользователя", e);
        }
    }

    /**
     * Проверяет, занято ли указанное имя пользователя.
     *
     * @param username имя пользователя для проверки
     * @return true, если имя пользователя занято; false в противном случае
     * @throws RuntimeException если произошла ошибка во время SQL-запроса
     */
    private boolean isUsernameTaken(String username) {
        String sql = "SELECT COUNT(*) FROM car_shop.users WHERE username = ?";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка SQL: " + e.getMessage() + sql);
            throw new RuntimeException("Ошибка при проверке имени пользователя", e);
        }
        return false;
    }

    /**
     * Получает список всех пользователей из базы данных.
     *
     * @return список пользователей
     * @throws RuntimeException если произошла ошибка во время SQL-запроса
     */
    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, username, password, role FROM car_shop.users";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        UserRole.valueOf(rs.getString("role"))
                );
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка SQL: " + e.getMessage() + sql);
            throw new RuntimeException("Ошибка при получении пользователей", e);
        }
        return users;
    }

    /**
     * Получает пользователя по уникальному идентификатору.
     *
     * <p>Если пользователь с указанным идентификатором существует, метод возвращает объект типа
     * {@link User}, иначе возвращает null.</p>
     *
     * @param id уникальный идентификатор пользователя
     * @return объект {@link User} с данными пользователя или null, если пользователь не найден
     * @throws RuntimeException если произошла ошибка во время выполнения SQL-запроса
     */
    @Override
    public User getUserById(int id) {
        String sql = "SELECT id, username, password, role FROM car_shop.users WHERE id = ?";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            UserRole.valueOf(rs.getString("role"))
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка SQL: " + e.getMessage() + sql);
            throw new RuntimeException("Ошибка при получении пользователя по ID", e);
        }
        return null;
    }

    /**
     * Поиск пользователей по имени и роли.
     *
     * <p>Метод возвращает список пользователей, чье имя соответствует заданному шаблону и у
     * которых указана определенная роль.</p>
     *
     * @param name имя пользователя или часть имени для поиска
     * @param role роль пользователей для фильтрации
     * @return список пользователей {@link List<User>} соответствующих критериям поиска
     * @throws RuntimeException если произошла ошибка во время выполнения SQL-запроса
     */
    @Override
    public List<User> searchUsers(String name, UserRole role) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, username, password, role FROM car_shop.users WHERE username LIKE ? AND role = ?";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + name + "%");
            pstmt.setString(2, role.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            UserRole.valueOf(rs.getString("role"))
                    );
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка SQL: " + e.getMessage() + sql);
            throw new RuntimeException("Ошибка при поиске пользователей", e);
        }
        return users;
    }

    /**
     * Обновляет информацию о пользователе.
     *
     * <p>Метод обновляет имя, пароль и роль пользователя с указанным идентификатором.</p>
     *
     * @param id          уникальный идентификатор пользователя, которого нужно обновить
     * @param updatedUser объект {@link User} с новыми данными
     * @throws RuntimeException если произошла ошибка во время выполнения SQL-запроса
     */
    @Override
    public void updateUser(int id, User updatedUser) {
        String sql = "UPDATE car_shop.users SET username = ?, password = ?, role = ? WHERE id = ?";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, updatedUser.username());
            pstmt.setString(2, updatedUser.password());
            pstmt.setString(3, updatedUser.role().toString());
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка SQL: " + e.getMessage() + sql);
            throw new RuntimeException("Ошибка при обновлении пользователя", e);
        }
    }

    /**
     * Удаляет пользователя по уникальному идентификатору.
     *
     * <p>Если пользователь с указанным идентификатором существует, он будет удален из базы данных.
     * В противном случае будет выведено сообщение о том, что пользователь не найден.</p>
     *
     * @param id уникальный идентификатор пользователя, которого нужно удалить
     * @throws RuntimeException если произошла ошибка во время выполнения SQL-запроса
     */
    @Override
    public void deleteUser(int id) {
        String sql = "DELETE FROM car_shop.users WHERE id = ?";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Пользователь успешно удален.");
            } else {
                System.out.println("Пользователь не найден.");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка SQL: " + e.getMessage() + sql);
            throw new RuntimeException("Ошибка при удалении пользователя", e);
        }
    }
}