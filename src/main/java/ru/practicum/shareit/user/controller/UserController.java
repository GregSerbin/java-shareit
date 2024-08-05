package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public UserDto create(@Valid @RequestBody UserDto user) {
        log.info("Получен запрос на создание пользователя {}", user);
        return userService.create(user);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@RequestBody UserDto newUser, @PathVariable Long userId) {
        log.info("Получен запрос на обновление пользователя с id={}. Новые поля: {}", userId, newUser);
        return userService.update(newUser, userId);
    }

    @DeleteMapping("/{userId}")
    public UserDto delete(@PathVariable Long userId) {
        log.info("Получен запрос на удаление пользователя с id={}", userId);
        return userService.delete(userId);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        log.info("Получен запрос на получение пользователя с id={}", userId);
        return userService.getUserById(userId);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Получен запрос на получение всех пользователей");
        return userService.getAllUsers();
    }
}
