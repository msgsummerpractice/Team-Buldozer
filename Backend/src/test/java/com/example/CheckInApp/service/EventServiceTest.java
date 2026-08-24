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
import com.example.CheckInApp.dto.response.EventCodesResponse;
import com.example.CheckInApp.dto.response.EventResponse;
import com.example.CheckInApp.exception.CheckInCodeGenerationException;
import com.example.CheckInApp.exception.CodesAlreadyGeneratedException;
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
import com.example.CheckInApp.repository.AttendanceRecordRepository;
import com.example.CheckInApp.repository.EventRepository;
import com.example.CheckInApp.repository.UserRepository;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

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

        @Mock
        private AttendanceRecordRepository attendanceRecordRepository;

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
                assertThat(resultResponse.getId()).isEqualTo(1L);
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
                Event event = Event.builder().id(1L).status(EventStatus.PUBLISHED).location(EventLocation.CLUJ)
                                .registrationEndDate(LocalDate.now().plusDays(1)).build();
                EventResponse response = EventResponse.builder().id(1L).status(EventStatus.PUBLISHED).build();

                when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
                when(userRepository.findByEmail("user@example.com"))
                                .thenReturn(Optional.of(participantUser(UserLocation.CLUJ)));
                when(eventMapper.toResponse(event)).thenReturn(response);

                assertThat(eventService.getEventById(1L, "user@example.com").getId()).isEqualTo(1L);
        }

        @Test
        void getEventById_throwsResourceNotFoundException_whenRegistrationEndDateIsInPast() {
                Event event = Event.builder().id(1L).status(EventStatus.PUBLISHED).location(EventLocation.CLUJ)
                                .registrationEndDate(LocalDate.now().minusDays(1)).build();

                when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
                when(userRepository.findByEmail("user@example.com"))
                                .thenReturn(Optional.of(participantUser(UserLocation.CLUJ)));

                assertThatThrownBy(() -> eventService.getEventById(1L, "user@example.com"))
                                .isInstanceOf(ResourceNotFoundException.class);
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
                when(attendanceRecordRepository.findEventIdsByUserIdAndEventIdIn(any(), any())).thenReturn(Set.of());
                when(eventMapper.toResponse(event, false)).thenReturn(EventResponse.builder().id(1L).build());

                List<EventResponse> result = eventService.getAllEvents("user@example.com");

                assertThat(result).hasSize(1);
                verify(eventRepository).findAllByOrderByStartDateTimeDesc();
        }

        @Test
        void getAllEvents_returnsOnlyPublishedEventsInUserLocation_whenUserIsParticipant() {
                Event event = Event.builder().id(1L).status(EventStatus.PUBLISHED).location(EventLocation.CLUJ).build();
                when(userRepository.findByEmail("user@example.com"))
                                .thenReturn(Optional.of(participantUser(UserLocation.CLUJ)));
                when(eventRepository
                                .findByStatusAndLocationInAndRegistrationEndDateGreaterThanEqualOrderByStartDateTimeDesc(
                                                eq(EventStatus.PUBLISHED),
                                                eq(List.of(EventLocation.CLUJ, EventLocation.ALL)),
                                                any(LocalDate.class)))
                                .thenReturn(List.of(event));
                when(attendanceRecordRepository.findEventIdsByUserIdAndEventIdIn(any(), any())).thenReturn(Set.of());
                when(eventMapper.toResponse(event, false)).thenReturn(EventResponse.builder().id(1L).build());

                List<EventResponse> result = eventService.getAllEvents("user@example.com");

                assertThat(result).hasSize(1);
                verify(eventRepository)
                                .findByStatusAndLocationInAndRegistrationEndDateGreaterThanEqualOrderByStartDateTimeDesc(
                                                eq(EventStatus.PUBLISHED),
                                                eq(List.of(EventLocation.CLUJ, EventLocation.ALL)),
                                                any(LocalDate.class));
        }

        @Test
        void getAllEvents_marksEventAsRegistered_whenUserHasAttendanceRecordForEvent() {
                Event event = Event.builder().id(1L).build();
                when(userRepository.findByEmail("user@example.com"))
                                .thenReturn(Optional.of(marketingUser()));
                when(eventRepository.findAllByOrderByStartDateTimeDesc()).thenReturn(List.of(event));
                when(attendanceRecordRepository.findEventIdsByUserIdAndEventIdIn(any(), any())).thenReturn(Set.of(1L));
                when(eventMapper.toResponse(event, true))
                                .thenReturn(EventResponse.builder().id(1L).isUserRegistered(true).build());

                List<EventResponse> result = eventService.getAllEvents("user@example.com");

                assertThat(result.get(0).isUserRegistered()).isTrue();
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
        void completeEvent_throwsEventNotEditableException_whenEndDateTimeIsInFuture() {
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

        @Test
        void publishEvent_throwsResourceNotFoundException_whenEventNotFound() {
                when(eventRepository.findById(99L)).thenReturn(Optional.empty());

                assertThatThrownBy(() -> eventService.publishEvent(99L))
                                .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        void publishEvent_throwsEventNotEditableException_whenEventIsNotDraft() {
                Event event = Event.builder().id(1L).status(EventStatus.PUBLISHED).build();
                when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

                assertThatThrownBy(() -> eventService.publishEvent(1L))
                                .isInstanceOf(EventNotEditableException.class);
        }

        @Test
        void publishEvent_setsStatusToPublished_whenDraft() {
                Event event = Event.builder().id(1L).status(EventStatus.DRAFT).build();
                Event saved = Event.builder().id(1L).status(EventStatus.PUBLISHED).build();

                when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
                when(eventRepository.save(any(Event.class))).thenReturn(saved);
                when(eventMapper.toResponse(saved))
                                .thenReturn(EventResponse.builder().id(1L).status(EventStatus.PUBLISHED).build());

                EventResponse result = eventService.publishEvent(1L);

                ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
                verify(eventRepository).save(captor.capture());
                assertThat(captor.getValue().getStatus()).isEqualTo(EventStatus.PUBLISHED);
                assertThat(result.getStatus()).isEqualTo(EventStatus.PUBLISHED);
        }

        @Test
        void generateCodes_throwsResourceNotFoundException_whenEventNotFound() {
                when(eventRepository.findById(99L)).thenReturn(Optional.empty());

                assertThatThrownBy(() -> eventService.generateCodes(99L))
                                .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        void generateCodes_throwsInvalidEventDataException_whenEventIsNotPublished() {
                Event event = Event.builder().id(1L).status(EventStatus.DRAFT).build();
                when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

                assertThatThrownBy(() -> eventService.generateCodes(1L))
                                .isInstanceOf(InvalidEventDataException.class);
        }

        @Test
        void generateCodes_throwsCodesAlreadyGeneratedException_whenCheckInCodeAlreadyExists() {
                Event event = Event.builder().id(1L).status(EventStatus.PUBLISHED).checkInCode("123456").build();
                when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

                assertThatThrownBy(() -> eventService.generateCodes(1L))
                                .isInstanceOf(CodesAlreadyGeneratedException.class);
        }

        @Test
        void generateCodes_generatesAndSavesCodes_whenPublishedAndNoCodesYet() {
                Event event = Event.builder().id(1L).name("Event").status(EventStatus.PUBLISHED).build();

                when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
                when(eventRepository.existsByCheckInCode(anyString())).thenReturn(false);
                when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

                EventCodesResponse result = eventService.generateCodes(1L);

                ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
                verify(eventRepository).save(captor.capture());
                assertThat(captor.getValue().getCheckInCode()).isNotBlank();
                assertThat(captor.getValue().getQrCode()).isNotEmpty();
                assertThat(result.getCheckInCode()).isEqualTo(captor.getValue().getCheckInCode());
                assertThat(result.getQrCode()).isNotBlank();
        }

        @Test
        void generateCodes_retriesAndSucceeds_whenFirstGeneratedCodeAlreadyExists() {
                Event event = Event.builder().id(1L).name("Event").status(EventStatus.PUBLISHED).build();

                when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
                when(eventRepository.existsByCheckInCode(anyString())).thenReturn(true, false);
                when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

                EventCodesResponse result = eventService.generateCodes(1L);

                verify(eventRepository, times(2)).existsByCheckInCode(anyString());
                assertThat(result.getCheckInCode()).isNotBlank();
        }

        @Test
        void generateCodes_throwsCheckInCodeGenerationException_whenAllAttemptsCollide() {
                Event event = Event.builder().id(1L).name("Event").status(EventStatus.PUBLISHED).build();

                when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
                when(eventRepository.existsByCheckInCode(anyString())).thenReturn(true);

                assertThatThrownBy(() -> eventService.generateCodes(1L))
                                .isInstanceOf(CheckInCodeGenerationException.class);

                verify(eventRepository, times(3)).existsByCheckInCode(anyString());
        }

        @Test
        void generateCodes_generatesQrCodeThatDecodesToEventIdAndName() throws Exception {
                Event event = Event.builder().id(1L).name("Team Building Event").status(EventStatus.PUBLISHED).build();

                when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
                when(eventRepository.existsByCheckInCode(anyString())).thenReturn(false);
                when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

                EventCodesResponse result = eventService.generateCodes(1L);

                byte[] qrCodeBytes = Base64.getDecoder().decode(result.getQrCode());
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(qrCodeBytes));
                BinaryBitmap binaryBitmap = new BinaryBitmap(
                                new HybridBinarizer(new BufferedImageLuminanceSource(image)));
                com.google.zxing.Result decodedResult = new MultiFormatReader().decode(binaryBitmap);

                assertThat(decodedResult.getText()).isEqualTo(event.getId() + "-" + event.getName());
        }

        @Test
        void getEventCodes_throwsResourceNotFoundException_whenEventNotFound() {
                when(eventRepository.findById(99L)).thenReturn(Optional.empty());

                assertThatThrownBy(() -> eventService.getEventCodes(99L))
                                .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        void getEventCodes_throwsInvalidEventDataException_whenEventIsNotPublished() {
                Event event = Event.builder().id(1L).status(EventStatus.DRAFT).build();
                when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

                assertThatThrownBy(() -> eventService.getEventCodes(1L))
                                .isInstanceOf(InvalidEventDataException.class);
        }

        @Test
        void getEventCodes_throwsResourceNotFoundException_whenCodesNotYetGenerated() {
                Event event = Event.builder().id(1L).status(EventStatus.PUBLISHED).build();
                when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

                assertThatThrownBy(() -> eventService.getEventCodes(1L))
                                .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        void getEventCodes_returnsCodes_whenAlreadyGenerated() {
                Event event = Event.builder().id(1L).status(EventStatus.PUBLISHED)
                                .checkInCode("123456").qrCode("qr".getBytes()).build();
                when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

                EventCodesResponse result = eventService.getEventCodes(1L);

                assertThat(result.getCheckInCode()).isEqualTo("123456");
                assertThat(result.getQrCode()).isEqualTo(Base64.getEncoder().encodeToString("qr".getBytes()));
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