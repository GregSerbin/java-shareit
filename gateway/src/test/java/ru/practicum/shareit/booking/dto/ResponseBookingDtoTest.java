package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ResponseBookingDtoTest {
    public static final LocalDateTime LOCAL_DATE_TIME = LocalDateTime.of(2000, 10, 20, 10,
            20, 10, 10);

    private final JacksonTester<ResponseBookingDto> json;
    private ResponseBookingDto booking;

    @BeforeEach
    void setUp() {
        booking = ResponseBookingDto.builder()
                .id(1L)
                .start(LOCAL_DATE_TIME)
                .end(LOCAL_DATE_TIME.plusHours(1))
                .status(Status.APPROVED)
                .build();
    }

    @Test
    void testResponseBookingDto() throws Exception {
        JsonContent<ResponseBookingDto> result = json.write(booking);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2000-10-20, 10:20:10");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2000-10-20, 11:20:10");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(Status.APPROVED.name());
    }
}