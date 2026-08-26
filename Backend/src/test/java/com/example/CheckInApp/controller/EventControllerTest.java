package com.example.CheckInApp.controller;

import com.example.CheckInApp.dto.request.EventRequest;
import com.example.CheckInApp.dto.request.EventUpdateRequest;
import com.example.CheckInApp.dto.response.CreateEventResponse;
import com.example.CheckInApp.dto.response.EventCodesResponse;
import com.example.CheckInApp.dto.response.EventResponse;
import com.example.CheckInApp.model.EventLocation;
import com.example.CheckInApp.model.EventStatus;
import com.example.CheckInApp.model.EventType;
import com.example.CheckInApp.repository.UserRepository;
import com.example.CheckInApp.security.JwtUtil;
import com.example.CheckInApp.service.EventExportService;
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

import java.time.Instant;
import java.util.List;

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
    private EventExportService eventExportService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private UserRepository userRepository;

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
                .startDateTime(Instant.parse("2026-09-15T10:00:00Z"))
                .endDateTime(Instant.parse("2026-09-15T18:00:00Z"))
                .type(EventType.LOCAL)
                .status(EventStatus.DRAFT)
                .registrationStartDate(Instant.parse("2026-09-01T00:00:00Z"))
                .registrationEndDate(Instant.parse("2026-09-10T00:00:00Z"))
                .address("Central Park, Cluj")
                .description("Annual team building event with activities")
                .foodProvided(true)
                .createdById(1L)
                .createdAt(Instant.now())
                .build();

        when(eventService.getEventById(eq(1L), eq("user@example.com")))
                .thenReturn(response);

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

        verify(eventService).getEventById(1L, "user@example.com");
    }

    @Test
    void getEventById_returnsCorrectHttpStatus() throws Exception {
        EventResponse response = EventResponse.builder()
                .id(1L)
                .name("Event")
                .status(EventStatus.DRAFT)
                .build();

        when(eventService.getEventById(eq(1L), eq("user@example.com")))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/events/1")
                        .with(user("user@example.com")))
                .andExpect(status().isOk());

        verify(eventService).getEventById(1L, "user@example.com");
    }

    @Test
    void addEvent_returnsCreatedWithEventResponse_whenUserHasMarketingRole() throws Exception {
        EventRequest request = new EventRequest(
                "Team Building Event",
                EventLocation.CLUJ,
                Instant.parse("2026-09-15T10:00:00Z"),
                Instant.parse("2026-09-15T18:00:00Z"),
                EventType.LOCAL,
                null,
                Instant.parse("2026-09-01T00:00:00Z"),
                Instant.parse("2026-09-10T00:00:00Z"),
                "Central Park, Cluj",
                "Annual team building event with activities",
                true);

        when(eventService.addEvent(any(EventRequest.class), anyString()))
                .thenReturn(new CreateEventResponse(1L));

        mockMvc.perform(post("/api/v1/events")
                        .with(user("user@example.com").roles("MARKETING"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)));

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

    @Test
    void getAllEvents_returnsOk() throws Exception {
        EventResponse event = EventResponse.builder()
                .id(1L)
                .name("Team Building Event")
                .status(EventStatus.PUBLISHED)
                .build();

        when(eventService.getAllEvents("user@example.com"))
                .thenReturn(List.of(event));

        mockMvc.perform(get("/api/v1/events")
                        .with(user("user@example.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Team Building Event")));

        verify(eventService).getAllEvents("user@example.com");
    }

    @Test
    void completeEvent_returnsOkWithCompletedStatus_whenUserHasMarketingRole() throws Exception {
        EventResponse response = EventResponse.builder()
                .id(1L)
                .name("Team Building Event")
                .status(EventStatus.COMPLETED)
                .build();

        when(eventService.completeEvent(1L)).thenReturn(response);

        mockMvc.perform(patch("/api/v1/events/1/complete")
                        .with(user("user@example.com").roles("MARKETING")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("COMPLETED")));

        verify(eventService).completeEvent(1L);
    }

    @Test
    void completeEvent_returnsForbidden_whenUserIsNotMarketing() throws Exception {
        mockMvc.perform(patch("/api/v1/events/1/complete")
                        .with(user("user@example.com").roles("PARTICIPANT")))
                .andExpect(status().isForbidden());
    }

    @Test
    void publishEvent_returnsOkWithPublishedStatus_whenUserHasMarketingRole() throws Exception {
        EventResponse response = EventResponse.builder()
                .id(1L)
                .name("Team Building Event")
                .status(EventStatus.PUBLISHED)
                .build();

        when(eventService.publishEvent(1L)).thenReturn(response);

        mockMvc.perform(patch("/api/v1/events/1/publish")
                        .with(user("user@example.com").roles("MARKETING")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("PUBLISHED")));

        verify(eventService).publishEvent(1L);
    }

    @Test
    void publishEvent_returnsForbidden_whenUserIsNotMarketing() throws Exception {
        mockMvc.perform(patch("/api/v1/events/1/publish")
                        .with(user("user@example.com").roles("PARTICIPANT")))
                .andExpect(status().isForbidden());
    }

    @Test
    void generateCodes_returnsOkWithCodes_whenUserHasMarketingRole() throws Exception {
        EventCodesResponse response = new EventCodesResponse("123456", "base64qr");

        when(eventService.generateCodes(1L)).thenReturn(response);

        mockMvc.perform(post("/api/v1/events/1/codes")
                        .with(user("user@example.com").roles("MARKETING")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.checkInCode", is("123456")))
                .andExpect(jsonPath("$.qrCode", is("base64qr")));

        verify(eventService).generateCodes(1L);
    }

    @Test
    void generateCodes_returnsForbidden_whenUserIsNotMarketing() throws Exception {
        mockMvc.perform(post("/api/v1/events/1/codes")
                        .with(user("user@example.com").roles("PARTICIPANT")))
                .andExpect(status().isForbidden());
    }

    @Test
    void getEventCodes_returnsOkWithCodes_whenUserHasMarketingRole() throws Exception {
        EventCodesResponse response = new EventCodesResponse("123456", "base64qr");

        when(eventService.getEventCodes(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/events/1/codes")
                        .with(user("user@example.com").roles("MARKETING")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.checkInCode", is("123456")))
                .andExpect(jsonPath("$.qrCode", is("base64qr")));

        verify(eventService).getEventCodes(1L);
    }

    @Test
    void getEventCodes_returnsForbidden_whenUserIsNotMarketing() throws Exception {
        mockMvc.perform(get("/api/v1/events/1/codes")
                        .with(user("user@example.com").roles("PARTICIPANT")))
                .andExpect(status().isForbidden());
    }

    @Test
    void exportAttendance_returnsXlsxFile_whenUserHasHrRole() throws Exception {
        byte[] xlsxData = new byte[]{1, 2, 3};
        when(eventExportService.exportAttendance(1L))
                .thenReturn(new EventExportService.ExportResult("Team Building Event", xlsxData));

        mockMvc.perform(get("/api/v1/events/1/export")
                        .with(user("user@example.com").roles("HR")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .andExpect(header().string("Content-Disposition",
                        "attachment; filename=\"Team Building Event.xlsx\""))
                .andExpect(content().bytes(xlsxData));

        verify(eventExportService).exportAttendance(1L);
    }

    @Test
    void exportAttendance_returnsForbidden_whenUserIsNotHr() throws Exception {
        mockMvc.perform(get("/api/v1/events/1/export")
                        .with(user("user@example.com").roles("PARTICIPANT")))
                .andExpect(status().isForbidden());
    }

}
