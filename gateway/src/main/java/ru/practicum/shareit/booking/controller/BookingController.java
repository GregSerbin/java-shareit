package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.booking.service.BookingClient;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("bookings")
public class BookingController {
    public static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";
    public static final String PATTERN = "yyyy-MM-dd, HH:mm:ss";

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody RequestBookingDto booking,
                                         @RequestHeader(SHARER_USER_ID_HEADER) Long userId) {
        log.info("Получен запрос на создание предмета {} пользователем с id={}", booking, userId);
        return bookingClient.create(userId, booking);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> update(@PathVariable Long bookingId,
                                         @RequestHeader(SHARER_USER_ID_HEADER) Long ownerId,
                                         @RequestParam boolean approved) {
        log.info("Получен запрос на подтверждение или отклонение запроса с id={} на бронирование от владельца с id={} с статусом подтверждения: {}", bookingId, ownerId, approved);
        return bookingClient.update(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findById(@PathVariable Long bookingId,
                                           @RequestHeader(SHARER_USER_ID_HEADER) Long userId) {
        log.info("Получен запрос на получение бронирования с id={}, от пользователя с id={}", bookingId, userId);
        return bookingClient.findById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findByBooker(@RequestHeader(SHARER_USER_ID_HEADER) Long bookerId,
                                               @RequestParam(defaultValue = "ALL") State state) {
        log.info("Получен запрос на получение всех бронирований текущего пользователя с id={}, с статусом '{}'", bookerId, state);
        return bookingClient.findByBooker(bookerId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findByOwner(@RequestHeader(SHARER_USER_ID_HEADER) Long ownerId,
                                              @RequestParam(defaultValue = "ALL") State state) {
        log.info("Получен запрос на получение списка бронирований для всех вещей текущего пользователя с id={}, с статусом '{}'", ownerId, state);
        return bookingClient.findByOwner(ownerId, state);
    }
}
