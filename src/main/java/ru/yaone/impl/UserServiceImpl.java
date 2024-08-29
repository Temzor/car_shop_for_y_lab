package ru.yaone.impl;

import ru.yaone.aspect.annotation.Loggable;
import ru.yaone.constants.SqlScriptsForUsers;
import ru.yaone.dto.UserDTO;
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
@Loggable("Логирование класса UserServiceImpl")
public class UserServiceImpl implements UserService {
    /**
     * Добавляет нового пользователя в базу данных.
     *
     * <p>Перед добавлением проверяет, не занято ли имя пользователя. Если имя занято,
     * выбрасывается RuntimeException. Если имя свободно, пользователь добавляется в базу данных.</p>
     *
     * @param userDTO объект пользователя, который необходимо добавить в систему
     * @throws RuntimeException если имя пользователя уже занято или произошла ошибка во время SQL-запроса
     */
    @Loggable("Логирование метода UserServiceImpl.addUser")
    @Override
    public void addUser(UserDTO userDTO) {
        if (isUsernameTaken(userDTO.getUsername())) {
            throw new RuntimeException("Пользователь с именем " + userDTO.getUsername() + " уже существует.");
        }
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(SqlScriptsForUsers.ADD_USER)) {
            preparedStatement.setString(1, userDTO.getUsername());
            preparedStatement.setString(2, userDTO.getPassword());
            preparedStatement.setString(3, userDTO.getRole().toString());
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    new User(rs.getInt(1), userDTO.getUsername(), userDTO.getPassword(), userDTO.getRole());
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
        try (Connection conn = DatabaseConnectionManager.getConnection();
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
    public List<UserDTO> getAllUsers() {
        List<UserDTO> usersDTO = new ArrayList<>();
        try (Connection conn = DatabaseConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SqlScriptsForUsers.GET_ALL_USERS)) {
            while (rs.next()) {
                UserDTO userDTO = new UserDTO(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        UserRole.valueOf(rs.getString("role"))
                );
                usersDTO.add(userDTO);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка SQL: " + e.getMessage());
            throw new RuntimeException("Ошибка при получении пользователей", e);
        }
        return usersDTO;
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
    public UserDTO getUserById(int id) {
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(SqlScriptsForUsers.GET_USER_BY_ID)) {
            preparedStatement.setInt(1, id);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return new UserDTO(
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
     * @param id             уникальный идентификатор пользователя, которого нужно обновить
     * @param updatedUserDTO объект {@link User} с новыми данными
     * @throws RuntimeException если произошла ошибка во время выполнения SQL-запроса
     */
    @Loggable("Логирование метода UserServiceImpl.updateUser")
    @Override
    public void updateUser(int id, UserDTO updatedUserDTO) {
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(SqlScriptsForUsers.UPDATE_USER)) {
            preparedStatement.setString(1, updatedUserDTO.getUsername());
            preparedStatement.setString(2, updatedUserDTO.getPassword());
            preparedStatement.setString(3, updatedUserDTO.getRole().toString());
            preparedStatement.setInt(4, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка SQL: " + e.getMessage());
            throw new RuntimeException("Ошибка при обновлении пользователя", e);
        }
    }
}