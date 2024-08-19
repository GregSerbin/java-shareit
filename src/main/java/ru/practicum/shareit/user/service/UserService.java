package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    public UserDto create(UserDto userDto);

    public UserDto update(UserDto newUser, Long userId);

    public UserDto delete(Long userId);

    public UserDto getUserById(Long userId);

    public List<UserDto> getAllUsers();
}
