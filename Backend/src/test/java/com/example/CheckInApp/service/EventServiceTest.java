package com.example.CheckInApp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.CheckInApp.dto.mapper.EventMapper;
import com.example.CheckInApp.dto.request.EventRequest;
import com.example.CheckInApp.dto.request.EventUpdateRequest;
import com.example.CheckInApp.dto.response.CreateEventResponse;
import com.example.CheckInApp.dto.response.EventResponse;
import com.example.CheckInApp.exception.EventNotEditableException;
import com.example.CheckInApp.exception.InvalidEventDataException;
import com.example.CheckInApp.exception.InvalidFileException;
import com.example.CheckInApp.exception.PosterNotReadException;
import com.example.CheckInApp.exception.ResourceNotFoundException;
import com.example.CheckInApp.model.Event;
import com.example.CheckInApp.model.EventLocation;
import com.example.CheckInApp.model.EventStatus;
import com.example.CheckInApp.model.EventType;
import com.example.CheckInApp.model.User;
import com.example.CheckInApp.model.UserLocation;
import com.example.CheckInApp.model.UserRole;
import com.example.CheckInApp.repository.EventRepository;
import com.example.CheckInApp.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EventService eventService;

    private static final LocalDateTime FUTURE_START = LocalDateTime.now().plusDays(10);
    private static final LocalDateTime FUTURE_END = LocalDateTime.now().plusDays(11);
    private static final LocalDate REG_START = LocalDate.now().plusDays(1);
    private static final LocalDate REG_END = LocalDate.now().plusDays(5);

    @Test
    void addEvent_savesEventWithDraftStatus() {
        EventRequest request = buildRequest(EventType.INTERNAL, EventLocation.ALL, null);
        Event mappedEvent = buildEventEntity(EventType.INTERNAL, EventLocation.ALL);
        Event savedEvent = Event.builder().id(1L).status(EventStatus.DRAFT).build();

        when(eventMapper.toEntity(request)).thenReturn(mappedEvent);
        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(marketingUser()));
        when(eventRepository.save(any(Event.class))).thenReturn(savedEvent);

        CreateEventResponse resultResponse = eventService.addEvent(request, "user@example.com");

        ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
        verify(eventRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(EventStatus.DRAFT);
        assertThat(resultResponse.id()).isEqualTo(1L);
    }

    @Test
    void addEvent_throwsResourceNotFoundException_whenUserNotFound() {
        EventRequest request = buildRequest(EventType.INTERNAL, EventLocation.ALL, null);
        when(eventMapper.toEntity(request)).thenReturn(buildEventEntity(EventType.INTERNAL, EventLocation.ALL));
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.addEvent(request, "unknown@example.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void addEvent_setsLocationToAll_whenTypeIsInternal() {
        EventRequest request = buildRequest(EventType.INTERNAL, EventLocation.ALL, null);
        when(eventMapper.toEntity(request)).thenReturn(buildEventEntity(EventType.INTERNAL, EventLocation.ALL));
        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(marketingUser()));
        when(eventRepository.save(any(Event.class)))
                .thenReturn(Event.builder().id(1L).location(EventLocation.ALL).build());

        eventService.addEvent(request, "user@example.com");

        ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
        verify(eventRepository).save(captor.capture());
        assertThat(captor.getValue().getLocation()).isEqualTo(EventLocation.ALL);
    }

    @Test
    void addEvent_throwsInvalidEventDataException_whenTypeIsLocalAndLocationIsAll() {
        EventRequest request = buildRequest(EventType.LOCAL, EventLocation.ALL, null);
        when(eventMapper.toEntity(request)).thenReturn(buildEventEntity(EventType.LOCAL, EventLocation.ALL));
        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(marketingUser()));

        assertThatThrownBy(() -> eventService.addEvent(request, "user@example.com"))
                .isInstanceOf(InvalidEventDataException.class);
    }

    @Test
    void addEvent_throwsPosterNotReadException_whenPosterIsInvalidBase64() {
        EventRequest request = buildRequest(EventType.INTERNAL, EventLocation.ALL, "not-valid!!!");
        when(eventMapper.toEntity(request)).thenReturn(buildEventEntity(EventType.INTERNAL, EventLocation.ALL));
        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(marketingUser()));

        assertThatThrownBy(() -> eventService.addEvent(request, "user@example.com"))
                .isInstanceOf(PosterNotReadException.class);
    }

    @Test
    void addEvent_throwsInvalidFileException_whenPosterFormatIsNotImageType() {
        String invalidImageBase64 = Base64.getEncoder().encodeToString("not-an-image".getBytes());
        EventRequest request = buildRequest(EventType.INTERNAL, EventLocation.ALL, invalidImageBase64);
        when(eventMapper.toEntity(request)).thenReturn(buildEventEntity(EventType.INTERNAL, EventLocation.ALL));
        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(marketingUser()));

        assertThatThrownBy(() -> eventService.addEvent(request, "user@example.com"))
                .isInstanceOf(InvalidFileException.class);
    }

    @Test
    void getEventById_throwsResourceNotFoundException_whenEventNotFound() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.getEventById(99L, "user@example.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getEventById_returnsEvent_whenUserHasMarketingRole() {
        Event event = Event.builder().id(1L).status(EventStatus.DRAFT).build();
        EventResponse response = EventResponse.builder().id(1L).status(EventStatus.DRAFT).build();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(marketingUser()));
        when(eventMapper.toResponse(event)).thenReturn(response);

        assertThat(eventService.getEventById(1L, "user@example.com").getId()).isEqualTo(1L);
    }

    @Test
    void getEventById_throwsResourceNotFoundException_whenParticipantTriesToSeeDraftEvent() {
        Event event = Event.builder().id(1L).status(EventStatus.DRAFT).location(EventLocation.CLUJ).build();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(participantUser(UserLocation.CLUJ)));

        assertThatThrownBy(() -> eventService.getEventById(1L, "user@example.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getEventById_returnsEvent_whenParticipantSeesPublishedEventInTheirLocation() {
        Event event = Event.builder().id(1L).status(EventStatus.PUBLISHED).location(EventLocation.CLUJ).build();
        EventResponse response = EventResponse.builder().id(1L).status(EventStatus.PUBLISHED).build();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(participantUser(UserLocation.CLUJ)));
        when(eventMapper.toResponse(event)).thenReturn(response);

        assertThat(eventService.getEventById(1L, "user@example.com").getId()).isEqualTo(1L);
    }

    @Test
    void updateEvent_throwsResourceNotFoundException_whenEventNotFound() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.updateEvent(99L, new EventUpdateRequest()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateEvent_throwsEventNotEditableException_whenEventIsNotDraft() {
        Event event = Event.builder().id(1L).status(EventStatus.PUBLISHED).build();
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> eventService.updateEvent(1L, new EventUpdateRequest()))
                .isInstanceOf(EventNotEditableException.class);
    }

    @Test
    void updateEvent_updatesName_whenNameIsProvided() {
        Event event = Event.builder().id(1L).name("Old Name").status(EventStatus.DRAFT)
                .startDateTime(FUTURE_START).endDateTime(FUTURE_END)
                .registrationStartDate(REG_START).registrationEndDate(REG_END)
                .type(EventType.INTERNAL).location(EventLocation.ALL).build();
        EventUpdateRequest request = new EventUpdateRequest();
        request.setName("New Name");
        Event savedEvent = Event.builder().id(1L).name("New Name").status(EventStatus.DRAFT).build();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(savedEvent);
        when(eventMapper.toResponse(savedEvent))
                .thenReturn(EventResponse.builder().id(1L).name("New Name").build());

        EventResponse result = eventService.updateEvent(1L, request);

        assertThat(result.getName()).isEqualTo("New Name");
    }

    @Test
    void getAllEvents_throwsResourceNotFoundException_whenUserNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.getAllEvents("unknown@example.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getAllEvents_returnsAllEvents_whenUserIsMarketing() {
        Event event = Event.builder().id(1L).build();
        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(marketingUser()));
        when(eventRepository.findAllByOrderByStartDateTimeDesc()).thenReturn(List.of(event));
        when(eventMapper.toResponse(event)).thenReturn(EventResponse.builder().id(1L).build());

        List<EventResponse> result = eventService.getAllEvents("user@example.com");

        assertThat(result).hasSize(1);
        verify(eventRepository).findAllByOrderByStartDateTimeDesc();
    }

    @Test
    void getAllEvents_returnsOnlyPublishedEventsInUserLocation_whenUserIsParticipant() {
        Event event = Event.builder().id(1L).status(EventStatus.PUBLISHED).location(EventLocation.CLUJ).build();
        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(participantUser(UserLocation.CLUJ)));
        when(eventRepository.findByStatusAndLocationInOrderByStartDateTimeDesc(
                EventStatus.PUBLISHED, List.of(EventLocation.CLUJ, EventLocation.ALL)))
                .thenReturn(List.of(event));
        when(eventMapper.toResponse(event)).thenReturn(EventResponse.builder().id(1L).build());

        List<EventResponse> result = eventService.getAllEvents("user@example.com");

        assertThat(result).hasSize(1);
        verify(eventRepository).findByStatusAndLocationInOrderByStartDateTimeDesc(
                EventStatus.PUBLISHED, List.of(EventLocation.CLUJ, EventLocation.ALL));
    }

    @Test
    void completeEvent_throwsResourceNotFoundException_whenEventNotFound() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.completeEvent(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void completeEvent_throwsEventNotEditableException_whenEventIsAlreadyCompleted() {
        Event event = Event.builder().id(1L).status(EventStatus.COMPLETED)
                .endDateTime(LocalDateTime.now().minusDays(1)).build();
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> eventService.completeEvent(1L))
                .isInstanceOf(EventNotEditableException.class);
    }

    @Test
    void completeEvent_throwsEventNotEditableException_whenEventIsDraft() {
        Event event = Event.builder().id(1L).status(EventStatus.DRAFT)
                .endDateTime(LocalDateTime.now().minusDays(1)).build();
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> eventService.completeEvent(1L))
                .isInstanceOf(EventNotEditableException.class);
    }

    @Test
    void completeEvent_throwsInvalidEventDataException_whenEndDateTimeIsInFuture() {
        Event event = Event.builder().id(1L).status(EventStatus.PUBLISHED)
                .endDateTime(FUTURE_END).build();
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> eventService.completeEvent(1L))
                .isInstanceOf(EventNotEditableException.class);
    }

    @Test
    void completeEvent_setsStatusToCompleted_whenPublishedAndEndDateInPast() {
        Event event = Event.builder().id(1L).status(EventStatus.PUBLISHED)
                .endDateTime(LocalDateTime.now().minusHours(1)).build();
        Event saved = Event.builder().id(1L).status(EventStatus.COMPLETED).build();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(saved);
        when(eventMapper.toResponse(saved))
                .thenReturn(EventResponse.builder().id(1L).status(EventStatus.COMPLETED).build());

        EventResponse result = eventService.completeEvent(1L);

        ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
        verify(eventRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(EventStatus.COMPLETED);
        assertThat(result.getStatus()).isEqualTo(EventStatus.COMPLETED);
    }

    private EventRequest buildRequest(EventType type, EventLocation location, String poster) {
        return new EventRequest("Event", location, FUTURE_START, FUTURE_END,
                type, poster, REG_START, REG_END, "Address", "Description", true);
    }

    private Event buildEventEntity(EventType type, EventLocation location) {
        return Event.builder()
                .name("Event").startDateTime(FUTURE_START).endDateTime(FUTURE_END)
                .registrationStartDate(REG_START).registrationEndDate(REG_END)
                .address("Address").description("Description").build();
    }

    private User marketingUser() {
        return User.builder().email("user@example.com").roles(Set.of(UserRole.MARKETING)).build();
    }

    private User participantUser(UserLocation location) {
        return User.builder().email("user@example.com")
                .roles(Set.of(UserRole.PARTICIPANT)).location(location).build();
    }
}
