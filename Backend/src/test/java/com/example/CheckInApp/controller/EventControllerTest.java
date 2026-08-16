package com.example.CheckInApp.controller;

import com.example.CheckInApp.dto.request.EventRequest;
import com.example.CheckInApp.dto.request.EventUpdateRequest;
import com.example.CheckInApp.dto.response.EventResponse;
import com.example.CheckInApp.model.EventLocation;
import com.example.CheckInApp.model.EventStatus;
import com.example.CheckInApp.model.EventType;
import com.example.CheckInApp.service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private EventService eventService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private EventController eventController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(eventController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void addEvent_returnsCreatedWithEventResponse_whenValidRequest() throws Exception {
        EventRequest request = new EventRequest(
                "Team Building Event",
                EventLocation.CLUJ,
                LocalDateTime.of(2026, 9, 15, 10, 0),
                LocalDateTime.of(2026, 9, 15, 18, 0),
                EventType.LOCAL,
                null,
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 10),
                "Central Park, Cluj",
                "Annual team building event with activities",
                true
        );

        EventResponse response = EventResponse.builder()
                .id(1L)
                .name("Team Building Event")
                .location(EventLocation.CLUJ)
                .startDateTime(LocalDateTime.of(2026, 9, 15, 10, 0))
                .endDateTime(LocalDateTime.of(2026, 9, 15, 18, 0))
                .type(EventType.LOCAL)
                .status(EventStatus.DRAFT)
                .registrationStartDate(LocalDate.of(2026, 9, 1))
                .registrationEndDate(LocalDate.of(2026, 9, 10))
                .address("Central Park, Cluj")
                .description("Annual team building event with activities")
                .foodProvided(true)
                .createdById(1L)
                .createdAt(LocalDateTime.now())
                .build();

        when(authentication.getName()).thenReturn("marketing@example.com");
        when(eventService.addEvent(any(EventRequest.class), eq("marketing@example.com"))).thenReturn(response);

        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .principal(authentication))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Team Building Event")))
                .andExpect(jsonPath("$.location", is("CLUJ")))
                .andExpect(jsonPath("$.type", is("LOCAL")))
                .andExpect(jsonPath("$.status", is("DRAFT")))
                .andExpect(jsonPath("$.foodProvided", is(true)));

        verify(eventService).addEvent(any(EventRequest.class), eq("marketing@example.com"));
    }

    @Test
    void addEvent_returnsCorrectHttpStatus() throws Exception {
        EventRequest request = new EventRequest(
                "Conference",
                EventLocation.TIMISOARA,
                LocalDateTime.of(2026, 10, 20, 9, 0),
                LocalDateTime.of(2026, 10, 20, 17, 0),
                EventType.EXTERNAL,
                null,
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 15),
                "Business Center, Timisoara",
                "Tech conference",
                null
        );

        EventResponse response = EventResponse.builder()
                .id(2L)
                .name("Conference")
                .status(EventStatus.DRAFT)
                .build();

        when(authentication.getName()).thenReturn("marketing@example.com");
        when(eventService.addEvent(any(EventRequest.class), anyString())).thenReturn(response);

        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .principal(authentication))
                .andExpect(status().isCreated());

        verify(eventService).addEvent(any(EventRequest.class), anyString());
    }

    @Test
    void updateEvent_returnsOkWithUpdatedEvent_whenValidRequest() throws Exception {
        EventUpdateRequest request = new EventUpdateRequest();
        request.setName("Updated Event Name");
        request.setDescription("Updated description");
        request.setFoodProvided(false);

        EventResponse response = EventResponse.builder()
                .id(1L)
                .name("Updated Event Name")
                .location(EventLocation.CLUJ)
                .startDateTime(LocalDateTime.of(2026, 9, 15, 10, 0))
                .endDateTime(LocalDateTime.of(2026, 9, 15, 18, 0))
                .type(EventType.LOCAL)
                .status(EventStatus.DRAFT)
                .registrationStartDate(LocalDate.of(2026, 9, 1))
                .registrationEndDate(LocalDate.of(2026, 9, 10))
                .address("Central Park, Cluj")
                .description("Updated description")
                .foodProvided(false)
                .createdById(1L)
                .createdAt(LocalDateTime.now())
                .build();

        when(authentication.getName()).thenReturn("marketing@example.com");
        when(eventService.updateEvent(eq(1L), any(EventUpdateRequest.class), eq("marketing@example.com"))).thenReturn(response);

        mockMvc.perform(put("/api/v1/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Event Name")))
                .andExpect(jsonPath("$.description", is("Updated description")))
                .andExpect(jsonPath("$.foodProvided", is(false)));

        verify(eventService).updateEvent(eq(1L), any(EventUpdateRequest.class), eq("marketing@example.com"));
    }

    @Test
    void updateEvent_returnsCorrectHttpStatus() throws Exception {
        EventUpdateRequest request = new EventUpdateRequest();
        request.setName("Updated Name");

        EventResponse response = EventResponse.builder()
                .id(1L)
                .name("Updated Name")
                .status(EventStatus.DRAFT)
                .build();

        when(authentication.getName()).thenReturn("marketing@example.com");
        when(eventService.updateEvent(eq(1L), any(EventUpdateRequest.class), anyString())).thenReturn(response);

        mockMvc.perform(put("/api/v1/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .principal(authentication))
                .andExpect(status().isOk());

        verify(eventService).updateEvent(eq(1L), any(EventUpdateRequest.class), anyString());
    }
}
