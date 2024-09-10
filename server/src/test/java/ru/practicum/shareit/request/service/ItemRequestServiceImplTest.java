package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = {"spring.datasource.driver-class-name=org.h2.Driver",
                "spring.datasource.url=jdbc:h2:mem:shareit",
                "spring.datasource.username=dbuser",
                "spring.datasource.password=12345"},
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ItemRequestServiceImplTest {
    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User user;

    private User user2;

    private Item item;

    private ItemRequestDto itemRequest;

    @BeforeEach
    void beforeEach() {
        user = new User();
        user.setName("User Name");
        user.setEmail("user-name@email.ru");

        user2 = new User();
        user2.setName("User Name 2");
        user2.setEmail("user-name-2@email.ru");

        item = new Item();
        item.setOwner(user);
        item.setName("Item");
        item.setAvailable(true);
        item.setDescription("Item Description");

        itemRequest = ItemRequestDto.builder()
                .description("Item Request Description")
                .build();
    }

    @Test
    void create() {
        saveEntity();

        ItemRequestDto itemRequestDto = itemRequestService.create(itemRequest, user.getId());
        assertThat(itemRequestDto.getId(), notNullValue());
        assertThat(itemRequestDto.getDescription(), equalTo(itemRequest.getDescription()));
    }

    @Test
    void createFailUser() {
        saveEntity();

        assertThatThrownBy(() -> itemRequestService.create(itemRequest, 3L));
    }

    @Test
    void findByUserId() {
        saveEntity();

        for (int i = 0; i < 5; i++) {
            itemRequestService.create(itemRequest, user.getId());
        }

        List<ItemRequestDto> itemRequests = itemRequestService.findByUserId(user.getId());
        assertThat(itemRequests.size(), equalTo(5));
    }

    @Test
    void findAll() {
        saveEntity();

        for (int i = 0; i < 5; i++) {
            itemRequestService.create(itemRequest, user2.getId());
        }

        List<ItemRequestDto> itemRequests = itemRequestService.findAll(user.getId());
        assertThat(itemRequests.size(), equalTo(5));
    }

    @Test
    void findByRequestId() {
        saveEntity();
        ItemRequestDto itemRequestDto = itemRequestService.create(itemRequest, user.getId());

        itemRequestDto = itemRequestService.findByRequestId(itemRequestDto.getId());
        assertThat(itemRequestDto.getId(), notNullValue());
        assertThat(itemRequestDto.getDescription(), equalTo(itemRequest.getDescription()));
    }

    private void saveEntity() {
        user = userRepository.save(user);
        user2 = userRepository.save(user2);
        item = itemRepository.save(item);
    }
}