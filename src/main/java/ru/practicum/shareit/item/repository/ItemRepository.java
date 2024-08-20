package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwnerId(Long ownerId);

    @Query("select it " +
            "from Item as it " +
            "join it.owner as u " +
            "where it.available = true and (lower(it.name) like ?1 or lower(it.description) like ?1) ")
    List<Item> findBySearch(String text);
}
