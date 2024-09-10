package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    public static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto create(@RequestBody ItemRequestDto request, @RequestHeader(SHARER_USER_ID_HEADER) Long userId) {
        log.info("Получен запрос на создание запроса {} пользователем с id={}", request, userId);
        return itemRequestService.create(request, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findByRequestId(@PathVariable Long requestId) {
        log.info("Получен запрос на получение запроса с id={}", requestId);
        return itemRequestService.findByRequestId(requestId);
    }

    @GetMapping
    public List<ItemRequestDto> findByUserId(@RequestHeader(SHARER_USER_ID_HEADER) Long userId) {
        log.info("Получен запрос на получение всех запросов пользователя с id={}", userId);
        return itemRequestService.findByUserId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAll(@RequestHeader(SHARER_USER_ID_HEADER) Long userId) {
        log.info("Получен запрос на получение всех запросов, созданных другими пользователями для пользователя с id={}", userId);
        return itemRequestService.findAll(userId);
    }
}