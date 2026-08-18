package com.example.CheckInApp.controller;

import com.example.CheckInApp.dto.request.EventRequest;
import com.example.CheckInApp.dto.request.EventUpdateRequest;
import com.example.CheckInApp.dto.response.EventResponse;
import com.example.CheckInApp.model.EventLocation;
import com.example.CheckInApp.model.EventStatus;
import com.example.CheckInApp.model.EventType;
import com.example.CheckInApp.security.JwtUtil;
import com.example.CheckInApp.service.EventService;
import com.example.CheckInApp.config.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = EventController.class)
@Import(SecurityConfig.class)
class EventControllerTest {

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private EventService eventService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void getEventById_returnsOkWithEventResponse_whenEventExists() throws Exception {
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

        when(eventService.getEventById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/events/1")
                        .with(user("user@example.com")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Team Building Event")))
                .andExpect(jsonPath("$.location", is("CLUJ")))
                .andExpect(jsonPath("$.type", is("LOCAL")))
                .andExpect(jsonPath("$.status", is("DRAFT")))
                .andExpect(jsonPath("$.address", is("Central Park, Cluj")))
                .andExpect(jsonPath("$.foodProvided", is(true)));

        verify(eventService).getEventById(1L);
    }

    @Test
    void getEventById_returnsCorrectHttpStatus() throws Exception {
        EventResponse response = EventResponse.builder()
                .id(1L)
                .name("Event")
                .status(EventStatus.DRAFT)
                .build();

        when(eventService.getEventById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/events/1")
                        .with(user("user@example.com")))
                .andExpect(status().isOk());

        verify(eventService).getEventById(1L);
    }

    @Test
    void addEvent_returnsCreatedWithEventId_whenUserHasMarketingRole() throws Exception {
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

        when(eventService.addEvent(any(EventRequest.class), anyString())).thenReturn(1L);

        mockMvc.perform(post("/api/v1/events")
                        .with(user("user@example.com").roles("MARKETING"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("1"));

        verify(eventService).addEvent(any(EventRequest.class), anyString());
    }

    @Test
    void updateEvent_returnsOkWithUpdatedEventResponse_whenUserHasMarketingRole() throws Exception {
        EventUpdateRequest request = new EventUpdateRequest();
        request.setName("Updated Event Name");

        EventResponse response = EventResponse.builder()
                .id(1L)
                .name("Updated Event Name")
                .status(EventStatus.DRAFT)
                .build();

        when(eventService.updateEvent(eq(1L), any(EventUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/api/v1/events/1")
                        .with(user("user@example.com").roles("MARKETING"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Event Name")));

        verify(eventService).updateEvent(eq(1L), any(EventUpdateRequest.class));
    }

}
