package ru.yaone.serviceimpl;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yaone.aspect.annotation.Loggable;


import ru.yaone.model.User;
import ru.yaone.repository.UserRepository;
import ru.yaone.services.UserService;

@Service
@Loggable("Логирование класса UserServiceImpl")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Loggable("Логирование метода UserServiceImpl.addUser")
    @Override
    public void addUser(User user) {
        userRepository.addUser(user);
    }

    @Loggable("Логирование метода UserServiceImpl.getAllUsers")
    @Override
    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    @Loggable("Логирование метода UserServiceImpl.getUserById")
    @Override
    public User getUserById(int id) {
        return userRepository.getUserById(id);
    }

    @Loggable("Логирование метода UserServiceImpl.updateUser")
    @Override
    public void updateUser(int id, User updatedUser) {
        userRepository.updateUser(id, updatedUser);
    }
}