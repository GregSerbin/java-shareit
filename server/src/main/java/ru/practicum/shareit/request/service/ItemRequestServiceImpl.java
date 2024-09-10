package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Transactional
    @Override
    public ItemRequestDto create(ItemRequestDto newItemRequest, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%d не найден", userId)));
        ItemRequest itemRequest = itemRequestMapper.itemRequestDtoToItemRequest(newItemRequest);
        itemRequest.setOwner(user);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest = itemRequestRepository.save(itemRequest);
        return itemRequestMapper.itemRequestToItemRequestDto(itemRequest);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDto> findByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователь с id=%d не найден", userId));
        }

        List<ItemRequest> itemRequests = itemRequestRepository.findByOwnerId(userId);
        List<ItemRequestDto> itemRequestsDto = itemRequests.stream()
                .map(itemRequestMapper::itemRequestToItemRequestDto)
                .toList();

        return getListRequestsDtoWithItems(itemRequestsDto);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDto> findAll(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователь с id=%d не найден", userId));
        }

        List<ItemRequest> itemRequests = itemRequestRepository.findByOwnerIdNotEquals(userId);
        List<ItemRequestDto> itemRequestsDto = itemRequests.stream()
                .map(itemRequestMapper::itemRequestToItemRequestDto)
                .toList();

        return getListRequestsDtoWithItems(itemRequestsDto);
    }

    @Transactional(readOnly = true)
    @Override
    public ItemRequestDto findByRequestId(Long requestId) {
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Запрос с id=%d не найден", requestId)));
        ItemRequestDto itemRequestDto = itemRequestMapper.itemRequestToItemRequestDto(itemRequest);
        List<ItemDto> items = itemRepository.findByRequestId(requestId).stream()
                .map(itemMapper::itemToItemDto)
                .toList();
        itemRequestDto.setItems(items);
        return itemRequestDto;
    }

    private List<ItemRequestDto> getListRequestsDtoWithItems(List<ItemRequestDto> itemRequestsDto) {
        for (ItemRequestDto itemRequest : itemRequestsDto) {
            List<ItemDto> itemsDto = itemRepository.findByRequestId(itemRequest.getId()).stream()
                    .map(itemMapper::itemToItemDto)
                    .toList();
            itemRequest.setItems(itemsDto);
        }

        return itemRequestsDto;
    }
}
