package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
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
        properties = {"spring.datasource.driver-class-name=org.h2.Driver",
                "spring.datasource.url=jdbc:h2:mem:shareit",
                "spring.datasource.username=dbuser",
                "spring.datasource.password=12345"},
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
class BookingServiceImplTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingService bookingService;

    private User user;

    private Item item;

    private User ownerUser;

    private RequestBookingDto requestBookingDto;

    @BeforeEach
    void beforeEach() {
        user = new User();
        user.setName("User Name");
        user.setEmail("user-name@email.ru");

        ownerUser = new User();
        ownerUser.setName("Owner Name");
        ownerUser.setEmail("owner@email.ru");

        item = new Item();
        item.setOwner(ownerUser);
        item.setName("Item");
        item.setAvailable(true);
        item.setDescription("Item Description");

        requestBookingDto = RequestBookingDto.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .itemId(1L)
                .build();
    }

    @Test
    void create() {
        saveEntity();
        requestBookingDto.setItemId(item.getId());

        ResponseBookingDto responseBookingDto = bookingService.create(requestBookingDto, user.getId());
        assertThat(responseBookingDto.getId(), notNullValue());
        assertThat(responseBookingDto.getItem().getName(), equalTo(item.getName()));
        assertThat(responseBookingDto.getBooker().getName(), equalTo(user.getName()));
        assertThat((responseBookingDto.getStatus()), equalTo(Status.WAITING));
    }

    @Test
    void createFailUser() {
        saveEntity();

        assertThatThrownBy(() -> bookingService.create(requestBookingDto, 3L));
    }

    @Test
    void createFailItem() {
        saveEntity();

        requestBookingDto.setItemId(5L);
        assertThatThrownBy(() -> bookingService.create(requestBookingDto, 1L));
    }

    @Test
    void update() {
        saveEntity();
        requestBookingDto.setItemId(item.getId());

        ResponseBookingDto responseBookingDto = bookingService.create(requestBookingDto, user.getId());

        responseBookingDto = bookingService.update(responseBookingDto.getId(), ownerUser.getId(), true);
        assertThat(responseBookingDto.getId(), notNullValue());
        assertThat(responseBookingDto.getItem().getName(), equalTo(item.getName()));
        assertThat(responseBookingDto.getBooker().getName(), equalTo(user.getName()));
        assertThat((responseBookingDto.getStatus()), equalTo(Status.APPROVED));
    }

    @Test
    void updateFailBooking() {
        saveEntity();
        requestBookingDto.setItemId(item.getId());
        bookingService.create(requestBookingDto, user.getId());

        assertThatThrownBy(() -> bookingService.update(5L, ownerUser.getId(), true));
    }

    @Test
    void updateFailAccess() {
        saveEntity();
        requestBookingDto.setItemId(item.getId());
        ResponseBookingDto responseBookingDto = bookingService.create(requestBookingDto, user.getId());

        assertThatThrownBy(() -> bookingService.update(responseBookingDto.getId(), 1L, true));
    }

    @Test
    void findById() {
        saveEntity();
        requestBookingDto.setItemId(item.getId());

        ResponseBookingDto responseBookingDto = bookingService.create(requestBookingDto, user.getId());

        responseBookingDto = bookingService.findById(responseBookingDto.getId(), user.getId());
        assertThat(responseBookingDto.getId(), notNullValue());
        assertThat(responseBookingDto.getItem().getName(), equalTo(item.getName()));
        assertThat(responseBookingDto.getBooker().getName(), equalTo(user.getName()));
        assertThat((responseBookingDto.getStatus()), equalTo(Status.WAITING));
    }

    @Test
    void findByBooker() {
        saveEntity();
        requestBookingDto.setItemId(item.getId());

        for (int i = 0; i < 5; i++) {
            bookingService.create(requestBookingDto, user.getId());
        }

        List<ResponseBookingDto> bookings = bookingService.findByBooker(user.getId(), State.ALL);
        assertThat(bookings.size(), equalTo(5));
    }

    @Test
    void findByOwner() {
        saveEntity();
        requestBookingDto.setItemId(item.getId());

        for (int i = 0; i < 5; i++) {
            bookingService.create(requestBookingDto, user.getId());
        }

        List<ResponseBookingDto> bookings = bookingService.findByBooker(user.getId(), State.ALL);
        assertThat(bookings.size(), equalTo(5));
    }

    private void saveEntity() {
        user = userRepository.save(user);
        ownerUser = userRepository.save(ownerUser);
        item = itemRepository.save(item);
    }
}