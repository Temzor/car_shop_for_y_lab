package ru.yaone.repositoryimpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.yaone.aspect.annotation.Loggable;
import ru.yaone.constants.SqlScriptsForUsers;
import ru.yaone.model.User;
import ru.yaone.model.enumeration.UserRole;
import ru.yaone.repository.UserRepository;
import ru.yaone.manager.DatabaseConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


@Repository
@Loggable("Логирование класса UserServiceImpl")
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final DatabaseConnectionManager databaseConnectionManager;

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