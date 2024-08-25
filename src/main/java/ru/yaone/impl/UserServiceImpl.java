package ru.yaone.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yaone.aspect.annotation.Loggable;
import ru.yaone.constants.SqlScriptsForUsers;
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
@Service
@Loggable("Логирование класса UserServiceImpl")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final DatabaseConnectionManager databaseConnectionManager;

    /**
     * Добавляет нового пользователя в базу данных.
     *
     * <p>Перед добавлением проверяет, не занято ли имя пользователя. Если имя занято,
     * выбрасывается RuntimeException. Если имя свободно, пользователь добавляется в базу данных.</p>
     *
     * @param user объект пользователя, который необходимо добавить в систему
     * @throws RuntimeException если имя пользователя уже занято или произошла ошибка во время SQL-запроса
     */
    @Loggable("Логирование метода UserServiceImpl.addUser")
    @Override
    public void addUser(User user) {
        if (isUsernameTaken(user.username())) {
            throw new RuntimeException("Пользователь с именем " + user.username() + " уже существует.");
        }
        try (Connection conn = databaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(SqlScriptsForUsers.ADD_USER, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, user.username());
            preparedStatement.setString(2, user.password());
            preparedStatement.setString(3, user.role().toString());
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    new User(rs.getInt(1), user.username(), user.password(), user.role());
                    System.out.println("Пользователь успешно зарегистрирован.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка SQL: " + e.getMessage());
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
    @Loggable("Логирование метода UserServiceImpl.isUsernameTaken")
    private boolean isUsernameTaken(String username) {
        try (Connection conn = databaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(SqlScriptsForUsers.GET_USER)) {
            preparedStatement.setString(1, username);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка SQL: " + e.getMessage());
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
    @Loggable("Логирование метода UserServiceImpl.getAllUsers")
    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection conn = databaseConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SqlScriptsForUsers.GET_ALL_USERS)) {
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
            System.err.println("Ошибка SQL: " + e.getMessage());
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
    @Loggable("Логирование метода UserServiceImpl.getUserById")
    @Override
    public User getUserById(int id) {
        try (Connection conn = databaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(SqlScriptsForUsers.GET_USER_BY_ID)) {
            preparedStatement.setInt(1, id);
            try (ResultSet rs = preparedStatement.executeQuery()) {
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
            System.err.println("Ошибка SQL: " + e.getMessage());
            throw new RuntimeException("Ошибка при получении пользователя по ID", e);
        }
        return null;
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
    @Loggable("Логирование метода UserServiceImpl.updateUser")
    @Override
    public void updateUser(int id, User updatedUser) {
        try (Connection conn = databaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(SqlScriptsForUsers.UPDATE_USER)) {
            preparedStatement.setString(1, updatedUser.username());
            preparedStatement.setString(2, updatedUser.password());
            preparedStatement.setString(3, updatedUser.role().toString());
            preparedStatement.setInt(4, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка SQL: " + e.getMessage());
            throw new RuntimeException("Ошибка при обновлении пользователя", e);
        }
    }
}