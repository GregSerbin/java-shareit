package ru.practicum.shareit.request.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestClient;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("requests")
public class ItemRequestController {
    private final RequestClient requestClient;
    public static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ItemRequestDto request, @RequestHeader(SHARER_USER_ID_HEADER) Long userId) {
        log.info("Получен запрос на создание запроса {} пользователем с id={}", request, userId);
        return requestClient.create(userId, request);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findByRequestId(@PathVariable Long requestId) {
        log.info("Получен запрос на получение запроса с id={}", requestId);
        return requestClient.findByRequestId(requestId);
    }

    @GetMapping
    public ResponseEntity<Object> findByUserId(@RequestHeader(SHARER_USER_ID_HEADER) Long userId) {
        log.info("Получен запрос на получение всех запросов пользователя с id={}", userId);
        return requestClient.findByUserId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAll(@RequestHeader(SHARER_USER_ID_HEADER) Long userId) {
        log.info("Получен запрос на получение всех запросов, созданных другими пользователями. id={}", userId);
        return requestClient.findAll(userId);
    }
}