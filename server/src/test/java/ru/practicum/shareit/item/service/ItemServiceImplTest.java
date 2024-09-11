package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
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
class ItemServiceImplTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private ItemService itemService;

    @Autowired
    private BookingService bookingService;

    private User user;

    private User ownerItemRequest;

    private ItemDto item;

    private ItemRequest itemRequest;

    @BeforeEach
    void beforeEach() {
        user = new User();
        user.setName("User Name");
        user.setEmail("user-name@email.ru");

        ownerItemRequest = new User();
        ownerItemRequest.setName("Owner Name");
        ownerItemRequest.setEmail("owner@email.ru");

        itemRequest = new ItemRequest();
        itemRequest.setDescription("Item Request Description");
        itemRequest.setOwner(ownerItemRequest);
        itemRequest.setCreated(LocalDateTime.now());

        item = ItemDto.builder()
                .name("Item")
                .description("Item Description")
                .available(true)
                .build();
    }

    @Test
    void createItem() {
        saveEntity();
        item.setRequestId(itemRequest.getId());

        ItemDto itemDto = itemService.createItem(item, user.getId());
        assertThat(itemDto.getId(), notNullValue());
        assertThat(itemDto.getName(), equalTo(item.getName()));
        assertThat(itemDto.getDescription(), equalTo(item.getDescription()));
        assertThat(itemDto.getAvailable(), equalTo(item.getAvailable()));
        assertThat(itemDto.getRequestId(), equalTo(itemRequest.getId()));
    }

    @Test
    void createItemFailUser() {
        saveEntity();

        assertThatThrownBy(() -> itemService.createItem(item, 3L));
    }

    @Test
    void createItemFailItemRequest() {
        saveEntity();
        item.setRequestId(10L);
        assertThatThrownBy(() -> itemService.createItem(item, user.getId()));
    }

    @Test
    void createComment() {
        saveEntity();

        ItemDto itemDto = itemService.createItem(item, user.getId());

        RequestBookingDto booking = RequestBookingDto.builder()
                .start(LocalDateTime.now().minusHours(2))
                .end(LocalDateTime.now().minusHours(2))
                .itemId(itemDto.getId())
                .build();
        bookingService.create(booking, user.getId());

        CommentDto comment = CommentDto.builder()
                .text("Comment Text")
                .build();

        CommentDto commentDto = itemService.createComment(comment, itemDto.getId(), user.getId());
        assertThat(commentDto.getId(), notNullValue());
        assertThat(commentDto.getText(), equalTo(comment.getText()));
        assertThat(commentDto.getAuthorName(), equalTo(user.getName()));
        assertThat(commentDto.getItemId(), equalTo(itemDto.getId()));
    }

    @Test
    void update() {
        saveEntity();
        item.setRequestId(itemRequest.getId());
        ItemDto itemDto = itemService.createItem(item, user.getId());
        item.setName("New Item Name");

        ItemDto updateItemDto = itemService.updateItem(item, itemDto.getId(), user.getId());
        assertThat(updateItemDto.getId(), notNullValue());
        assertThat(updateItemDto.getName(), equalTo(item.getName()));
        assertThat(updateItemDto.getDescription(), equalTo(item.getDescription()));
        assertThat(updateItemDto.getAvailable(), equalTo(item.getAvailable()));
        assertThat(updateItemDto.getRequestId(), equalTo(itemRequest.getId()));
    }

    @Test
    void updateFailAccess() {
        saveEntity();
        item.setRequestId(itemRequest.getId());
        itemService.createItem(item, user.getId());
        item.setName("New Item Name");

        assertThatThrownBy(() -> itemService.updateItem(item, user.getId(), ownerItemRequest.getId()));
    }

    @Test
    void findById() {
        saveEntity();
        item.setRequestId(itemRequest.getId());
        ItemDto itemDto = itemService.createItem(item, user.getId());

        itemDto = itemService.getItemById(itemDto.getId());
        assertThat(itemDto.getId(), notNullValue());
        assertThat(itemDto.getName(), equalTo(item.getName()));
        assertThat(itemDto.getDescription(), equalTo(item.getDescription()));
        assertThat(itemDto.getAvailable(), equalTo(item.getAvailable()));
        assertThat(itemDto.getRequestId(), equalTo(itemRequest.getId()));
    }

    @Test
    void findByIdFailId() {
        saveEntity();
        item.setRequestId(itemRequest.getId());
        itemService.createItem(item, user.getId());

        assertThatThrownBy(() -> itemService.getItemById(5L));
    }

    @Test
    void findByOwnerId() {
        saveEntity();
        item.setRequestId(itemRequest.getId());

        for (int i = 0; i < 5; i++) {
            itemService.createItem(item, user.getId());
        }

        List<ItemDto> items = itemService.getAllItemsByOwner(user.getId());
        assertThat(items.size(), equalTo(5));
    }

    @Test
    void searchByText() {
        saveEntity();
        item.setRequestId(itemRequest.getId());
        itemService.createItem(item, user.getId());

        List<ItemDto> items = itemService.search("Item");
        assertThat(items.size(), equalTo(1));
        assertThat(items.getFirst().getName(), equalTo("Item"));
    }

    private void saveEntity() {
        user = userRepository.save(user);
        ownerItemRequest = userRepository.save(ownerItemRequest);
        itemRequest = itemRequestRepository.save(itemRequest);
    }
}