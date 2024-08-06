package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ItemStorageImpl implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private Long idCounter = 1L;

    @Override
    public Item create(Item item) {
        items.put(item.getId(), item);
        log.info("В памяти сохранен предмет {}", item);
        return item;
    }

    @Override
    public Item update(Item newItem) {
        Item oldItem = getItemById(newItem.getId()).get();
        if (newItem.getName() != null) {
            oldItem.setName(newItem.getName());
        }

        if (newItem.getDescription() != null) {
            oldItem.setDescription(newItem.getDescription());
        }

        if (newItem.getAvailable() != null) {
            oldItem.setAvailable(newItem.getAvailable());
        }

        items.put(oldItem.getId(), oldItem);
        log.info("В памяти обновлен предмет {}", oldItem);

        return oldItem;
    }

    @Override
    public Optional<Item> getItemById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> getAllItemsByOwner(Long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwnerId().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> search(String text) {
        return items.values().stream()
                .filter(item -> item.getDescription().toLowerCase().contains(text.toLowerCase())
                        || item.getName().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getAvailable)
                .toList();
    }

    @Override
    public Long getNextId() {
        return idCounter++;
    }
}
