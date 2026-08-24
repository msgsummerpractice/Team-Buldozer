package com.example.CheckInApp.service;

import com.example.CheckInApp.dto.mapper.EventMapper;
import com.example.CheckInApp.dto.request.EventRequest;
import com.example.CheckInApp.dto.request.EventUpdateRequest;
import com.example.CheckInApp.dto.response.CreateEventResponse;
import com.example.CheckInApp.dto.response.EventCodesResponse;
import com.example.CheckInApp.dto.response.EventResponse;
import com.example.CheckInApp.exception.EventNotEditableException;
import com.example.CheckInApp.exception.InvalidEventDataException;
import com.example.CheckInApp.exception.InvalidFileException;
import com.example.CheckInApp.exception.PosterNotReadException;
import com.example.CheckInApp.exception.QrCodeGenerationException;
import com.example.CheckInApp.exception.CodesAlreadyGeneratedException;
import com.example.CheckInApp.exception.CheckInCodeGenerationException;
import com.example.CheckInApp.exception.ResourceNotFoundException;
import com.example.CheckInApp.model.*;
import com.example.CheckInApp.repository.AttendanceRecordRepository;
import com.example.CheckInApp.repository.EventRepository;
import com.example.CheckInApp.repository.UserRepository;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EventService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final byte[] JPEG_SIGNATURE = { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF };
    private static final byte[] PNG_SIGNATURE = { (byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47 };
    private static final int MAX_CHECK_IN_CODE_ATTEMPTS = 3;
    private static final int QR_CODE_SIZE = 300;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final AttendanceRecordRepository attendanceRecordRepository;

    @Transactional
    public CreateEventResponse addEvent(EventRequest request, String userEmail) {

        Event event = eventMapper.toEntity(request);
        event.setStatus(EventStatus.DRAFT);
        validateDates(event);
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + userEmail));
        event.setCreatedBy(currentUser);
        event.setCreatedAt(LocalDateTime.now());

        applyTypeSpecificRules(event, request.getType(), request.getLocation(), request.getFoodProvided());

        if (request.getPoster() != null && !request.getPoster().trim().isEmpty()) {
            byte[] posterBytes = decodePoster(request.getPoster());
            validatePosterBytes(posterBytes);
            event.setPoster(posterBytes);
        }

        Event saved = eventRepository.save(event);

        return new CreateEventResponse(saved.getId());
    }

    @Transactional(readOnly = true)
    public EventResponse getEventById(Long id, String userEmail) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + id));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + userEmail));

        if (!hasFullAccess(user) && !isEventVisibleTo(event, user)) {
            throw new ResourceNotFoundException("Event not found with id " + id);
        }

        return eventMapper.toResponse(event);
    }

    protected boolean hasFullAccess(User user) {
        return user.getRoles().contains(UserRole.MARKETING) || user.getRoles().contains(UserRole.HR);
    }

    protected boolean isEventVisibleTo(Event event, User user) {
        EventLocation userLocation = EventLocation.valueOf(user.getLocation().name());
        return event.getStatus() == EventStatus.PUBLISHED &&
                (event.getLocation() == userLocation || event.getLocation() == EventLocation.ALL) &&
                !event.getRegistrationEndDate().isBefore(LocalDate.now());
    }

    @Transactional
    public EventResponse updateEvent(Long eventId, EventUpdateRequest request) {
        Event existingEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + eventId));

        if (existingEvent.getStatus() != EventStatus.DRAFT) {
            throw new EventNotEditableException("Event can only be edited while in DRAFT status.");
        }

        if (request.getName() != null) {
            existingEvent.setName(request.getName());
        }
        if (request.getStartDateTime() != null) {
            existingEvent.setStartDateTime(request.getStartDateTime());
        }
        if (request.getEndDateTime() != null) {
            existingEvent.setEndDateTime(request.getEndDateTime());
        }
        if (request.getRegistrationStartDate() != null) {
            existingEvent.setRegistrationStartDate(request.getRegistrationStartDate());
        }
        if (request.getRegistrationEndDate() != null) {
            existingEvent.setRegistrationEndDate(request.getRegistrationEndDate());
        }
        if (request.getAddress() != null) {
            existingEvent.setAddress(request.getAddress());
        }
        if (request.getDescription() != null) {
            existingEvent.setDescription(request.getDescription());
        }

        validateDates(existingEvent);

        if (request.getType() != null) {
            applyTypeSpecificRules(existingEvent, request.getType(), request.getLocation(), request.getFoodProvided());
        } else if (request.getLocation() != null || request.getFoodProvided() != null) {

            applyTypeSpecificRules(existingEvent, existingEvent.getType(), request.getLocation(),
                    request.getFoodProvided());
        }

        if (request.getPoster() != null && !request.getPoster().trim().isEmpty()) {
            byte[] posterBytes = decodePoster(request.getPoster());
            validatePosterBytes(posterBytes);
            existingEvent.setPoster(posterBytes);
        }

        Event updated = eventRepository.save(existingEvent);
        return eventMapper.toResponse(updated);
    }

    private void applyTypeSpecificRules(Event event, EventType type, EventLocation location, Boolean foodProvided) {
        event.setType(type);

        switch (type) {
            case INTERNAL -> {
                event.setLocation(EventLocation.ALL);
                event.setFoodProvided(Boolean.TRUE.equals(foodProvided));
            }
            case EXTERNAL -> {
                validateSpecificCityLocation(location, type);
                event.setLocation(location);
                event.setFoodProvided(null);
            }
            case LOCAL -> {
                validateSpecificCityLocation(location, type);
                event.setLocation(location);
                event.setFoodProvided(Boolean.TRUE.equals(foodProvided));
            }
        }
    }

    private void validateSpecificCityLocation(EventLocation location, EventType type) {
        if (location == null || location == EventLocation.ALL) {
            throw new InvalidEventDataException(
                    "For type " + type + ", location needs to be: CLUJ, TIMISOARA or MURES.");
        }
    }

    private byte[] decodePoster(String base64Poster) {
        try {
            return Base64.getDecoder().decode(base64Poster);
        } catch (IllegalArgumentException e) {
            throw new PosterNotReadException("Poster could not be decoded.");
        }
    }

    private void validatePosterBytes(byte[] posterBytes) {
        if (posterBytes == null || posterBytes.length == 0) {
            return;
        }

        if (posterBytes.length > MAX_FILE_SIZE) {
            throw new InvalidFileException("File is over the maximum size of 5MB.");
        }

        if (!isValidImageFormat(posterBytes)) {
            throw new InvalidFileException("File format should either be JPEG or PNG.");
        }
    }

    private boolean isValidImageFormat(byte[] imageBytes) {
        if (imageBytes == null || imageBytes.length < 4) {
            return false;
        }

        return startsWithSignature(imageBytes, JPEG_SIGNATURE) ||
                startsWithSignature(imageBytes, PNG_SIGNATURE);
    }

    private boolean startsWithSignature(byte[] data, byte[] signature) {
        if (data.length < signature.length) {
            return false;
        }

        return Arrays.equals(data, 0, signature.length, signature, 0, signature.length);
    }

    private void validateDates(Event request) {
        if (request.getStartDateTime().isBefore(LocalDateTime.now())) {
            throw new InvalidEventDataException("Start date time cannot be in the past.");
        }
        if (request.getRegistrationStartDate().isBefore(LocalDate.now())) {
            throw new InvalidEventDataException("Registration start date cannot be in the past.");
        }
        if (request.getEndDateTime().isBefore(request.getStartDateTime())) {
            throw new InvalidEventDataException("End date time must be after start date time.");
        }
        if (request.getRegistrationEndDate().isBefore(request.getRegistrationStartDate())) {
            throw new InvalidEventDataException("Registration end date must be after registration start date.");
        }
    }

    @Transactional
    public EventCodesResponse generateCodes(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + eventId));

        if (event.getStatus() != EventStatus.PUBLISHED) {
            throw new InvalidEventDataException("Codes can only be generated for published events.");
        }

        if (event.getCheckInCode() != null) {
            throw new CodesAlreadyGeneratedException("Codes have already been generated for this event.");
        }

        event.setCheckInCode(generateUniqueCheckInCode());
        event.setQrCode(generateQrCodeImage(event.getId() + "-" + event.getName()));

        Event saved = eventRepository.save(event);
        return new EventCodesResponse(saved.getCheckInCode(), Base64.getEncoder().encodeToString(saved.getQrCode()));
    }

    private String generateUniqueCheckInCode() {
        for (int attempt = 0; attempt < MAX_CHECK_IN_CODE_ATTEMPTS; attempt++) {
            String candidate = String.format("%06d", RANDOM.nextInt(1_000_000));
            if (!eventRepository.existsByCheckInCode(candidate)) {
                return candidate;
            }
        }
        throw new CheckInCodeGenerationException("Could not generate a unique check-in code after "
                + MAX_CHECK_IN_CODE_ATTEMPTS + " attempts.");
    }

    private byte[] generateQrCodeImage(String content) {
        try {
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, QR_CODE_SIZE, QR_CODE_SIZE);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            return outputStream.toByteArray();
        } catch (WriterException | IOException e) {
            throw new QrCodeGenerationException("QR code could not be generated.");
        }
    }

    @Transactional(readOnly = true)
    public EventCodesResponse getEventCodes(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + eventId));

        if (event.getStatus() != EventStatus.PUBLISHED) {
            throw new InvalidEventDataException("Codes are only available for published events.");
        }

        if (event.getCheckInCode() == null || event.getQrCode() == null) {
            throw new ResourceNotFoundException(
                    "Check-in codes have not been generated yet for event with id " + eventId);
        }

        return new EventCodesResponse(event.getCheckInCode(), Base64.getEncoder().encodeToString(event.getQrCode()));
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getAllEvents(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + userEmail));

        List<Event> events;

        if (hasFullAccess(user)) {
            events = eventRepository.findAllByOrderByStartDateTimeDesc();
        } else {
            EventLocation userLocation = EventLocation.valueOf(user.getLocation().name());
            events = eventRepository
                    .findByStatusAndLocationInAndRegistrationEndDateGreaterThanEqualOrderByStartDateTimeDesc(
                            EventStatus.PUBLISHED, List.of(userLocation, EventLocation.ALL), LocalDate.now());
        }

        List<Long> eventIds = events.stream().map(Event::getId).toList();
        Set<Long> registeredEventIds = attendanceRecordRepository.findEventIdsByUserIdAndEventIdIn(user.getId(), eventIds);

        return events.stream()
                .map(event -> eventMapper.toResponse(event, registeredEventIds.contains(event.getId())))
                .toList();
    }

    @Transactional
    public EventResponse publishEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + eventId));

        if (event.getStatus() != EventStatus.DRAFT) {
            throw new EventNotEditableException("Only DRAFT events can be published.");
        }

        event.setStatus(EventStatus.PUBLISHED);

        Event saved = eventRepository.save(event);
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    emailService.notifyEventPublished(saved);
                }
            });
        }
        return eventMapper.toResponse(saved);
    }
    

    @Transactional
    public EventResponse completeEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + eventId));

        if (event.getStatus() == EventStatus.COMPLETED) {
            throw new EventNotEditableException("Event is already completed.");
        }

        if (event.getStatus() == EventStatus.DRAFT) {
            throw new EventNotEditableException("Only published events can be completed.");
        }

        if (event.getEndDateTime().isAfter(LocalDateTime.now())) {
            throw new EventNotEditableException("Event cannot be completed before its end time.");
        }

        event.setStatus(EventStatus.COMPLETED);
        return eventMapper.toResponse(eventRepository.save(event));
    }
}
