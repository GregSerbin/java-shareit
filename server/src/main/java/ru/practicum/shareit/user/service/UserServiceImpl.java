package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    @Override
    public UserDto create(UserDto userDto) {
        User createdUser = userRepository.save(userMapper.userDtoToUser(userDto));
        log.info("Создан пользователь {}", createdUser);
        return userMapper.userToUserDto(createdUser);
    }

    @Transactional
    @Override
    public UserDto update(UserDto newUser, Long userId) {
        User oldUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%d не найден", userId)));

        checkUserEmail(newUser);
        if (newUser.getName() != null) {
            oldUser.setName(newUser.getName());
        }
        if (newUser.getEmail() != null) {
            oldUser.setEmail(newUser.getEmail());
        }

        User updatedUser = userRepository.save(oldUser);
        log.info("Обновлен пользователь {}", updatedUser);
        return userMapper.userToUserDto(updatedUser);
    }

    @Transactional
    @Override
    public void delete(Long userId) {
        UserDto user = findById(userId);
        userRepository.deleteById(user.getId());
        log.info("Удален пользователь {}", user);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto findById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%d не найден", userId)));
        log.info("Найден пользователь {}", user);
        return userMapper.userToUserDto(user);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        log.info("Получен список пользователей {}", users);
        return users.stream()
                .map(userMapper::userToUserDto)
                .toList();
    }

    private void checkUserEmail(UserDto userDto) {
        String userEmail = userDto.getEmail();
        if (userEmail != null) {
            Optional<User> existingUser = userRepository.findByEmail(userEmail);
            if (existingUser.isPresent()) {
                throw new ConflictDataException(String.format("Пользователь с email=%s уже существует", userEmail));
            }
        }
    }
}
