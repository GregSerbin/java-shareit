package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = {
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.datasource.url=jdbc:h2:mem:shareit",
                "spring.datasource.username=dbuser",
                "spring.datasource.password=12345"},
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UserServiceImplTest {
    @Autowired
    private UserService userService;

    private UserDto user;

    @BeforeEach
    void beforeEach() {
        user = UserDto.builder()
                .name("Owner Name")
                .email("owner@email.ru")
                .build();
    }

    @Test
    void create() {
        UserDto userDto = userService.create(user);
        assertThat(userDto.getId(), notNullValue());
        assertThat(userDto.getName(), equalTo(user.getName()));
        assertThat(userDto.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void update() {
        UserDto userDto = userService.create(user);
        user.setEmail("new-email@email.ru");

        userDto = userService.update(user, userDto.getId());
        assertThat(userDto.getId(), notNullValue());
        assertThat(userDto.getName(), equalTo(user.getName()));
        assertThat(userDto.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void updateFailUser() {
        UserDto userDto = userService.create(user);
        user.setEmail("new-email@email.ru");

        assertThatThrownBy(() -> userService.update(user, 3L));
    }

    @Test
    void findById() {
        UserDto userDto = userService.create(user);

        userDto = userService.findById(userDto.getId());
        assertThat(userDto.getId(), notNullValue());
        assertThat(userDto.getName(), equalTo(user.getName()));
        assertThat(userDto.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void findAll() {
        for (int i = 0; i < 6; i++) {
            userService.create(user);
            user.setEmail(i + "owner@email.ru");
        }

        List<UserDto> users = userService.getAllUsers();
        assertThat(users.size(), equalTo(6));
    }

    @Test
    void delete() {
        long id = 0;
        for (int i = 0; i < 6; i++) {
            UserDto us = userService.create(user);
            user.setEmail(i + "owner@email.ru");
            id = us.getId();
        }

        userService.delete(id);
        List<UserDto> users = userService.getAllUsers();
        assertThat(users.size(), equalTo(5));
    }
}