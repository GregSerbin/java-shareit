package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.DataAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public ResponseBookingDto create(RequestBookingDto bookingRequest, Long userId) {
        if (bookingRequest.getStart().isAfter(bookingRequest.getEnd())) {
            throw new ValidationException("Время начала бронирования должно быть раньше времени конца бронирования");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%d не найден", userId)));
        Item item = itemRepository.findById(bookingRequest.getItemId())
                .orElseThrow(() -> new NotFoundException(String.format("Предмет с id=%d не найден", bookingRequest.getItemId())));

        if (!item.getAvailable()) {
            throw new ValidationException(String.format("Предмет с id=%d не доступен для бронирования", bookingRequest.getItemId()));
        }

        Booking booking = bookingMapper.requestBookingDtoToBooking(bookingRequest);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        booking = bookingRepository.save(booking);
        log.info("Создано бронирование {}", booking);
        return bookingMapper.bookingToResponseBookingDto(booking);
    }

    @Transactional
    @Override
    public ResponseBookingDto update(Long bookingId, Long ownerId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Бронирование с id=%d не найдено", bookingId)));

        if (booking.getItem().getOwner().getId().equals(ownerId)) {
            booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        } else {
            throw new DataAccessException("Доступ к информации о бронированиях других пользователей запрещен");
        }

        booking = bookingRepository.save(booking);
        log.info("Статус бронирования с id={} изменен на approved = {}", bookingId, approved);
        return bookingMapper.bookingToResponseBookingDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseBookingDto findById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Бронирование с id=%d не найдено", bookingId)));

        if (!booking.getItem().getOwner().getId().equals(userId) && !booking.getBooker().getId().equals(userId)) {
            throw new DataAccessException("Доступ к информации о бронированиях других пользователей запрещен");
        }

        log.info("Найдено бронирование {}", booking);
        return bookingMapper.bookingToResponseBookingDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ResponseBookingDto> findByBooker(Long bookerId, State state) {

        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findByBookerId(bookerId);
            case PAST -> bookingRepository.findAllBookingByBookerAndPast(bookerId, LocalDateTime.now());
            case CURRENT -> bookingRepository.findAllBookingByBookerAndCurrent(bookerId, LocalDateTime.now());
            case FUTURE -> bookingRepository.findAllBookingByBookerAndFuture(bookerId, LocalDateTime.now());
            case WAITING -> bookingRepository.findByBooker_idAndStatus(bookerId, Status.WAITING.name());
            case REJECTED -> bookingRepository.findByBooker_idAndStatus(bookerId, Status.REJECTED.name());
            case null -> new ArrayList<>();
        };

        log.info("Получен список бронирований пользователя с id={}: {}", bookerId, bookings);
        return bookings.stream()
                .map(bookingMapper::bookingToResponseBookingDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<ResponseBookingDto> findByOwner(Long ownerId, State state) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%d не найден", ownerId)));

        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findAllBookingByOwner(ownerId);
            case PAST -> bookingRepository.findAllBookingByOwnerAndPast(ownerId, LocalDateTime.now());
            case CURRENT -> bookingRepository.findAllBookingByOwnerAndCurrent(ownerId, LocalDateTime.now());
            case FUTURE -> bookingRepository.findAllBookingByOwnerAndFuture(ownerId, LocalDateTime.now());
            case WAITING -> bookingRepository.findAllBookingByOwnerAndStatus(ownerId, Status.WAITING.name());
            case REJECTED -> bookingRepository.findAllBookingByOwnerAndStatus(ownerId, Status.REJECTED.name());
            case null -> new ArrayList<>();
        };

        log.info("Получен список бронирований владельца с id={}: {}", ownerId, bookings);
        return bookings.stream()
                .map(bookingMapper::bookingToResponseBookingDto)
                .toList();
    }

}
