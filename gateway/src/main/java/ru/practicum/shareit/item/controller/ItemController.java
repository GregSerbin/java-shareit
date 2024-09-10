package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemClient;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("items")
public class ItemController {
    private final ItemClient itemClient;
    public static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createItem(@Valid @RequestBody ItemDto item, @RequestHeader(SHARER_USER_ID_HEADER) Long ownerId) {
        log.info("Получен запрос на создание предмета {} пользователем с id={}", item, ownerId);
        return itemClient.createItem(ownerId, item);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Valid @RequestBody CommentDto comment,
                                                @PathVariable Long itemId,
                                                @RequestHeader(SHARER_USER_ID_HEADER) Long userId) {
        log.info("Получен запрос на создание комментария {} на предмет с id={} от пользователя " +
                "с id={}", comment, itemId, userId);
        return itemClient.createComment(userId, itemId, comment);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestBody ItemDto newItem,
                                         @PathVariable Long itemId,
                                         @RequestHeader(SHARER_USER_ID_HEADER) Long ownerId) {
        log.info("Получен запрос на обновление предмета с id={} от пользователя с id={}. Поля, которые нужно обновить: {}", itemId, ownerId, newItem);
        return itemClient.update(ownerId, itemId, newItem);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findById(@PathVariable Long itemId) {
        log.info("Получен запрос на получение предмета с id={}", itemId);
        return itemClient.findById(itemId);
    }

    @GetMapping
    public ResponseEntity<Object> findByOwnerId(@RequestHeader(SHARER_USER_ID_HEADER) Long ownerId) {
        log.info("Получен запрос на получение всех предметов пользователя с id={}", ownerId);
        return itemClient.findByOwnerId(ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchByText(@RequestHeader(SHARER_USER_ID_HEADER) Long userId,
                                               @RequestParam(defaultValue = "") String text) {
        log.info("Получен запрос на поиск предмета по тексту text={}", text);
        return itemClient.searchByText(userId, text);
    }
}