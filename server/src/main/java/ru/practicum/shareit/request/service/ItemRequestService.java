package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(ItemRequestDto newItemRequest, Long userId);

    List<ItemRequestDto> findByUserId(Long userId);

    List<ItemRequestDto> findAll(Long userId);

    ItemRequestDto findByRequestId(Long requestId);
}

