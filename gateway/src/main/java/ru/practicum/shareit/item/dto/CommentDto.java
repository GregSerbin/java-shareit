package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

import static ru.practicum.shareit.booking.controller.BookingController.PATTERN;

@Data
@Builder
public class CommentDto {
    private Long id;
    @NotBlank
    private String text;
    private Long itemId;
    private String authorName;
    @JsonFormat(pattern = PATTERN)
    private LocalDateTime created;
}
