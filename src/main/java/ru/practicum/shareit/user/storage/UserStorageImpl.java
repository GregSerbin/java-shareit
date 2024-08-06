package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
public class UserStorageImpl implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long idCounter = 1L;

    @Override
    public User create(User user) {
        users.put(user.getId(), user);
        log.info("В памяти сохранен пользователь {}", user);
        return user;
    }

    @Override
    public User update(User newUser) {
        User oldUser = getUserById(newUser.getId()).get();
        if (newUser.getName() != null) {
            oldUser.setName(newUser.getName());
        }

        if (newUser.getEmail() != null) {
            oldUser.setEmail(newUser.getEmail());
        }

        users.put(oldUser.getId(), oldUser);
        log.info("В памяти обновлен пользователь {}", oldUser);

        return oldUser;
    }

    @Override
    public User delete(Long userId) {
        User deletedUser = users.get(userId);
        users.remove(userId);
        log.info("Из памяти удален пользователь {}", deletedUser);
        return deletedUser;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> getAllUsers() {
        return users.values().stream().toList();
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return users.values().stream()
                .filter(e -> e.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    @Override
    public Long getNextId() {
        return idCounter++;
    }


}
