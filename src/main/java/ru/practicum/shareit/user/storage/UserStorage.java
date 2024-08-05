package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    public User create(User user);

    public User update(User newUser);

    public User delete(Long userId);

    public List<User> getAllUsers();

    public Optional<User> getUserById(Long id);

    public Optional<User> getUserByEmail(String email);

    public Long getNextId();
}
