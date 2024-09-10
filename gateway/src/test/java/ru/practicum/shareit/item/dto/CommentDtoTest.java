package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static ru.practicum.shareit.booking.dto.ResponseBookingDtoTest.LOCAL_DATE_TIME;


@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommentDtoTest {
    private final JacksonTester<CommentDto> json;
    private CommentDto comment;

    @BeforeEach
    void setUp() {
        comment = CommentDto.builder()
                .id(1L)
                .text("qwer")
                .itemId(1L)
                .authorName("Max")
                .created(LOCAL_DATE_TIME)
                .build();
    }

    @Test
    void testResponseBookingDto() throws Exception {
        JsonContent<CommentDto> result = json.write(comment);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("qwer");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo("2000-10-20, 10:20:10");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Max");
    }
}