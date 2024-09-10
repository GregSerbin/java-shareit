package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    public static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private LocalDateTime localDateTime;

    private RequestBookingDto requestBookingDto;

    private ResponseBookingDto responseBookingDto;

    @BeforeEach
    void setUp() {
        localDateTime = LocalDateTime.now();
        requestBookingDto = RequestBookingDto.builder()
                .id(1L)
                .start(localDateTime)
                .end(localDateTime.plusHours(1))
                .itemId(1L)
                .build();
        responseBookingDto = ResponseBookingDto.builder()
                .id(1L)
                .start(localDateTime)
                .end(localDateTime.plusHours(1))
                .status(Status.APPROVED)
                .build();
    }

    @Test
    void create() throws Exception {
        when(bookingService.create(requestBookingDto, 1L))
                .thenReturn(responseBookingDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(requestBookingDto))
                        .header(SHARER_USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(Status.APPROVED.name())));

    }

    @Test
    void update() throws Exception {
        when(bookingService.update(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(responseBookingDto);

        mvc.perform(patch("/bookings/1?approved=true")
                        .content(mapper.writeValueAsString(requestBookingDto))
                        .header(SHARER_USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(Status.APPROVED.name())));
    }

    @Test
    void findById() throws Exception {
        when(bookingService.findById(anyLong(), anyLong()))
                .thenReturn(responseBookingDto);

        mvc.perform(get("/bookings/1")
                        .header(SHARER_USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(Status.APPROVED.name())));
    }

    @Test
    void findByBooker() throws Exception {
        List<ResponseBookingDto> listBookings = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ResponseBookingDto responseBookingDto = ResponseBookingDto.builder()
                    .id(Long.valueOf(i))
                    .start(localDateTime)
                    .end(localDateTime.plusHours(1))
                    .status(Status.APPROVED)
                    .build();
            listBookings.add(responseBookingDto);
        }

        when(bookingService.findByBooker(1L, State.ALL))
                .thenReturn(listBookings);

        mvc.perform(get("/bookings?state=ALL")
                        .header(SHARER_USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5));
    }

    @Test
    void findByOwner() throws Exception {
        List<ResponseBookingDto> listBookings = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ResponseBookingDto responseBookingDto = ResponseBookingDto.builder()
                    .id(Long.valueOf(i))
                    .start(localDateTime)
                    .end(localDateTime.plusHours(1))
                    .status(Status.APPROVED)
                    .build();
            listBookings.add(responseBookingDto);
        }

        when(bookingService.findByOwner(1L, State.ALL))
                .thenReturn(listBookings);

        mvc.perform(get("/bookings/owner?state=ALL")
                        .header(SHARER_USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5));
    }
}