package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithDateDto;

import java.util.List;

public interface ItemService {
    public ItemDto createItem(ItemDto itemDto, Long ownerId);

    public CommentDto createComment(CommentDto comment, Long itemId, Long userId);

    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long ownerId);

    public ItemDto getItemById(Long itemId);

    public List<ItemWithDateDto> getAllItemsByOwner(Long ownerId);

    public List<ItemDto> search(String text);
}
