package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequestDto {
    @Positive
    private Long id;
    @NotBlank
    private String description;
    @Positive
    private Long requesterId;
    @NotNull
    private LocalDateTime created;
}
