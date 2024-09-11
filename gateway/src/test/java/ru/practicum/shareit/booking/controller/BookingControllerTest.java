package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.RequestBookingDto;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {
    @InjectMocks
    private BookingController bookingController;

    private ObjectMapper mapper;

    private MockMvc mvc;

    private RequestBookingDto bookingDto;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();

        mvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();

        bookingDto = RequestBookingDto.builder()
                .start(null)
                .end(null)
                .itemId(Long.valueOf(1))
                .build();
    }

    @Test
    void create() throws Exception {
        mvc.perform(post("/bookings")
                .content(mapper.writeValueAsString(bookingDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().is(400));
    }
}