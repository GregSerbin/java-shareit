package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingService {
    ResponseBookingDto create(RequestBookingDto booking, Long id);

    ResponseBookingDto update(Long bookingId, Long ownerId, Boolean approved);

    ResponseBookingDto findById(Long bookingId, Long userId);

    List<ResponseBookingDto> findByBooker(Long bookerId, State state);

    List<ResponseBookingDto> findByOwner(Long ownerId, State state);
}
