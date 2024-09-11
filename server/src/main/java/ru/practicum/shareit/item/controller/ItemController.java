package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    public static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ItemDto create(@Valid @RequestBody ItemDto item, @RequestHeader(SHARER_USER_ID_HEADER) Long ownerId) {
        log.info("Получен запрос на создание предмета {} пользователя с id={}", item, ownerId);
        return itemService.createItem(item, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto newItem,
                          @PathVariable Long itemId,
                          @RequestHeader(SHARER_USER_ID_HEADER) Long ownerId) {
        log.info("Получен запрос на обновление предмета с id={} от пользователя с id={}. Поля: {}", itemId, ownerId, newItem);
        return itemService.updateItem(newItem, itemId, ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId) {
        log.info("Получен запрос на получение предмета с id={}", itemId);
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> getAllItemsByOwner(@RequestHeader(SHARER_USER_ID_HEADER) Long ownerId) {
        log.info("Получен запрос на получение всех предметов пользователя с id={}", ownerId);
        return itemService.getAllItemsByOwner(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(defaultValue = "") String text) {
        log.info("Получен запрос на поиск предмета по тексту text={}", text);
        return itemService.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@Valid @RequestBody CommentDto comment,
                                    @PathVariable Long itemId,
                                    @RequestHeader(SHARER_USER_ID_HEADER) Long userId) {
        log.info("Получен запрос на добавление комментария {} к предмету с id={} от пользователя с id={}", comment, itemId, userId);
        return itemService.createComment(comment, itemId, userId);
    }


}
