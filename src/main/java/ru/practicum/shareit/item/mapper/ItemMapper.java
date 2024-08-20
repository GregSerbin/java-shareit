package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithDateDto;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    Item itemDtoToItem(ItemDto itemDto);

    @Mapping(target = "ownerId", source = "owner.id")
    ItemDto itemToItemDto(Item item);

    @Mapping(target = "ownerId", source = "owner.id")
    ItemWithDateDto itemToItemWithDateDto(Item item);
}
