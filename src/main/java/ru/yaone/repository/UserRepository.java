package ru.yaone.repository;

import ru.yaone.model.User;

import java.util.List;

public interface UserRepository {
    void addUser(User user);

    List<User> getAllUsers();

    User getUserById(int id);

    void updateUser(int id, User updatedUser);
}