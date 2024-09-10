package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    public static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseBookingDto create(@Valid @RequestBody RequestBookingDto booking,
                                     @RequestHeader(SHARER_USER_ID_HEADER) Long userId) {
        log.info("Получен запрос на создание предмета {} пользователем с id={}", booking, userId);
        return bookingService.create(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseBookingDto update(@PathVariable Long bookingId,
                                     @RequestHeader(SHARER_USER_ID_HEADER) Long ownerId, @RequestParam boolean approved) {
        log.info("Получен запрос на подтверждение или отклонение запроса с id={} на бронирование от владельца с id={} с статусом подтверждения: {}", bookingId, ownerId, approved);
        return bookingService.update(bookingId, ownerId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseBookingDto findBookingById(@PathVariable Long bookingId,
                                              @RequestHeader(SHARER_USER_ID_HEADER) Long userId) {
        log.info("Получен запрос на получение бронирования с id={}, от пользователя с id={}", bookingId, userId);
        return bookingService.findById(bookingId, userId);
    }

    @GetMapping
    public List<ResponseBookingDto> findBookingsByBooker(@RequestHeader(SHARER_USER_ID_HEADER) Long bookerId,
                                                         @RequestParam(defaultValue = "ALL") State state) {
        log.info("Получен запрос на получение всех бронирований текущего пользователя с id={}, с статусом '{}'", bookerId, state);
        return bookingService.findByBooker(bookerId, state);
    }

    @GetMapping("/owner")
    public List<ResponseBookingDto> findBookingsByOwner(@RequestHeader(SHARER_USER_ID_HEADER) Long ownerId,
                                                        @RequestParam(defaultValue = "ALL") State state) {
        log.info("Получен запрос на получение списка бронирований для всех вещей текущего пользователя с id={}, с статусом '{}'", ownerId, state);
        return bookingService.findByOwner(ownerId, state);
    }
}
