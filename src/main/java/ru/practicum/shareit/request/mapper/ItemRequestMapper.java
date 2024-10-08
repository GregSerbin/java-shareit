package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

@Mapper
public interface ItemRequestMapper {
    ItemRequest mapToItemRequest(ItemRequestDto itemRequestDto);

    ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest);
}
