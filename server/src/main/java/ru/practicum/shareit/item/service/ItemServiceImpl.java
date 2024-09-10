package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.DataAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithDateDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final BookingMapper bookingMapper;
    private final ItemRequestRepository itemRequestRepository;

    @Transactional
    @Override
    public ItemDto createItem(ItemDto itemDto, Long ownerId) {
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%d не найден", ownerId)));

        ItemRequest itemRequest = null;
        if (itemDto.getRequestId() != null) {
            itemRequest = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException(String.format("Запрос с id=%d не найден", itemDto.getRequestId())));
        }


        Item item = itemMapper.itemDtoToItem(itemDto);
        item.setOwner(user);
        item.setRequest(itemRequest);
        item = itemRepository.save(item);
        log.info("Предмет создан: {}", item);
        return itemMapper.itemToItemDto(item);
    }

    @Transactional
    @Override
    public CommentDto createComment(CommentDto newComment, Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Предмета с id=%d не существует", itemId)));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователя с id=%d не существует", userId)));

        if (bookingRepository.findByUserId(userId, LocalDateTime.now()).isEmpty()) {
            throw new ValidationException(String.format("Пользователь с id=%d не использовал вещь с id=%d", userId, itemId));
        }

        Comment comment = commentMapper.commentDtoToComment(newComment);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        comment = commentRepository.save(comment);
        log.info("Создан комментарий {}", comment);
        return commentMapper.commentToCommentDto(comment);
    }

    @Transactional
    @Override
    public ItemDto updateItem(ItemDto newItem, Long itemId, Long ownerId) {
        Item oldItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Предмет с id=%d не найден", itemId)));

        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException(String.format("Владелец с id=%d не найден", ownerId));
        }
        if (!oldItem.getOwner().getId().equals(ownerId)) {
            throw new DataAccessException("Доступ к информации о вещах других пользователей запрещен");
        }

        if (newItem.getName() != null) {
            oldItem.setName(newItem.getName());
        }
        if (newItem.getDescription() != null) {
            oldItem.setDescription(newItem.getDescription());
        }
        if (newItem.getAvailable() != null) {
            oldItem.setAvailable(newItem.getAvailable());
        }

        Item updatedItem = itemRepository.save(oldItem);
        log.info("Обновлен предмет {}", updatedItem);
        return itemMapper.itemToItemDto(updatedItem);
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDto getItemById(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Предмет с id=%d не найден", itemId)));
        List<CommentDto> comment = commentRepository.findByItemId(itemId).stream()
                .map(commentMapper::commentToCommentDto)
                .toList();
        ItemDto itemDto = itemMapper.itemToItemDto(item);
        itemDto.setComments(comment);
        itemDto.setLastBooking(bookingMapper
                .bookingToResponseBookingDto(bookingRepository.findByItemIdPast(itemId, LocalDateTime.now(), Status.REJECTED)));
        itemDto.setNextBooking(bookingMapper
                .bookingToResponseBookingDto(bookingRepository.findByItemIdFuture(itemId, LocalDateTime.now(), Status.REJECTED)));

        log.info("Получен предмет {}", itemDto);
        return itemDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemWithDateDto> getAllItemsByOwner(Long ownerId) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException(String.format("Пользователь с id=%d не найден", ownerId));
        }

        List<Item> items = itemRepository.findByOwnerId(ownerId);
        List<ItemWithDateDto> itemsWithDateDto = items.stream()
                .map(itemMapper::itemToItemWithDateDto)
                .toList();

        for (ItemWithDateDto item : itemsWithDateDto) {
            Booking booking = bookingRepository.findByItemId(item.getId());
            List<CommentDto> comment = commentRepository.findByItemId(item.getId()).stream()
                    .map(commentMapper::commentToCommentDto).toList();

            if (booking != null) {
                item.setStart(booking.getStart());
                item.setEnd(booking.getEnd());
                item.setComments(comment);
            }
        }

        log.info("Получен список всех предметов пользователя с id={}: {}", ownerId, itemsWithDateDto);
        return itemsWithDateDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            log.info("Запрос на поиск содержит пустую строку");
            return new ArrayList<>();
        }

        List<Item> items = itemRepository.findBySearch(text.toLowerCase());
        log.info("Получен список предметов в рамках поиска по '{}': {}", text, items);
        return items.stream()
                .map(itemMapper::itemToItemDto)
                .toList();
    }
}
