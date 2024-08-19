package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    public ItemDto create(ItemDto itemDto, Long ownerId);

    public ItemDto update(ItemDto itemDto, Long itemId, Long ownerId);

    public ItemDto getItemById(Long itemId);

    public List<ItemDto> getAllItemsByOwner(Long ownerId);

    public List<ItemDto> search(String text);
}
