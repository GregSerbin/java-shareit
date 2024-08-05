package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final UserMapper userMapper;

    @Override
    public UserDto create(UserDto userDto) {
        checkUserEmail(userDto);
        userDto.setId(userStorage.getNextId());
        User createdUser = userStorage.create(userMapper.mapToUser(userDto));
        log.info("Создан пользователь {}", createdUser);
        return userMapper.mapToUserDto(createdUser);
    }

    @Override
    public UserDto update(UserDto newUser, Long userId) {
        Optional<User> oldUser = userStorage.getUserById(userId);

        if (oldUser.isEmpty()) {
            throw new NotFoundException(String.format("Пользователь с id=%d не найден", userId));
        }

        checkUserEmail(newUser);
        newUser.setId(userId);

        User updatedUser = userStorage.update(userMapper.mapToUser(newUser));
        log.info("Обновлен пользователь {}", updatedUser);
        return userMapper.mapToUserDto(updatedUser);
    }

    @Override
    public UserDto delete(Long userId) {
        UserDto user = getUserById(userId);
        return userMapper.mapToUserDto(userStorage.delete(user.getId()));
    }

    @Override
    public UserDto getUserById(Long userId) {
        Optional<User> user = userStorage.getUserById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException(String.format("Пользователь с id=%d не найден", userId));
        }

        return userMapper.mapToUserDto(userStorage.getUserById(userId).get());
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userStorage.getAllUsers();

        return users.stream()
                .map(userMapper::mapToUserDto)
                .toList();
    }

    private void checkUserEmail(UserDto userDto) {
        String userEmail = userDto.getEmail();
        if (userEmail != null) {
            Optional<User> existingUser = userStorage.getUserByEmail(userEmail);
            if (existingUser.isPresent()) {
                throw new ConflictDataException(String.format("Пользователь с email=%s уже существует", userEmail));
            }
        }
    }
}
