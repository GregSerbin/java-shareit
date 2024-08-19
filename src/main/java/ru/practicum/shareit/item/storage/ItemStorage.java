package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {

    public Item create(Item item);

    Item update(Item newItem);

    public Optional<Item> getItemById(Long id);

    List<Item> getAllItemsByOwner(Long ownerId);

    public List<Item> search(String text);

    public Long getNextId();
}
