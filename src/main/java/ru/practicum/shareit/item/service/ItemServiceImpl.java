package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserStorage userStorage;
    private final ItemStorage itemStorage;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto create(ItemDto itemDto, Long ownerId) {
        userStorage.getUserById(ownerId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%d не найден", ownerId)));

        itemDto.setOwnerId(ownerId);
        itemDto.setId(itemStorage.getNextId());
        Item item = itemStorage.create(itemMapper.mapToItem(itemDto));
        log.info("Предмет создан: {}", item);

        return itemMapper.mapToItemDto(item);
    }

    @Override
    public ItemDto update(ItemDto newItem, Long itemId, Long ownerId) {
        Optional<Item> oldItem = itemStorage.getItemById(itemId);

        if (oldItem.isEmpty()) {
            throw new NotFoundException(String.format("Предмет с id=%d не найден", itemId));
        }

        if (userStorage.getUserById(ownerId).isEmpty()) {
            throw new NotFoundException(String.format("Владелец с id=%d не найден", ownerId));
        }

        newItem.setId(itemId);

        Item updatedItem = itemStorage.update(itemMapper.mapToItem(newItem));
        log.info("Обновлен предмет {}", updatedItem);
        return itemMapper.mapToItemDto(updatedItem);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        Optional<Item> item = itemStorage.getItemById(itemId);
        if (item.isEmpty()) {
            throw new NotFoundException(String.format("Предмет с id=%d не найден", itemId));
        }

        return itemMapper.mapToItemDto(itemStorage.getItemById(itemId).get());
    }

    @Override
    public List<ItemDto> getAllItemsByOwner(Long ownerId) {
        if (userStorage.getUserById(ownerId).isEmpty()) {
            throw new NotFoundException(String.format("Пользователь с id=%d не найден", ownerId));
        }

        List<Item> items = itemStorage.getAllItemsByOwner(ownerId);
        return items.stream()
                .map(itemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            log.info("Запрос на поиск содержит пустую строку");
            return new ArrayList<>();
        }

        List<Item> items = itemStorage.search(text);
        return items.stream()
                .map(itemMapper::mapToItemDto)
                .toList();
    }
}
