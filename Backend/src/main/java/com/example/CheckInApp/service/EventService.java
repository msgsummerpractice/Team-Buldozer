package com.example.CheckInApp.service;

import com.example.CheckInApp.dto.mapper.EventMapper;
import com.example.CheckInApp.dto.request.EventRequest;
import com.example.CheckInApp.dto.response.EventResponse;
import com.example.CheckInApp.exception.InvalidEventDataException;
import com.example.CheckInApp.exception.InvalidFileException;
import com.example.CheckInApp.exception.PosterNotReadException;
import com.example.CheckInApp.exception.ResourceNotFoundException;
import com.example.CheckInApp.model.*;
import com.example.CheckInApp.repository.EventRepository;
import com.example.CheckInApp.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EventService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/jpg");

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserRepository userRepository;

    @Transactional
    public EventResponse addEvent(EventRequest request, String userEmail) {
        validateDates(request);
        validatePoster(request.getPoster());

        Event event = eventMapper.toEntity(request);
        event.setStatus(EventStatus.DRAFT);

        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + userEmail));
        event.setCreatedBy(currentUser);
        event.setCreatedAt(LocalDateTime.now());

        applyTypeSpecificRules(event, request.getType(), request.getLocation(), request.getFoodProvided());

        if (request.getPoster() != null && !request.getPoster().isEmpty()) {
            event.setPoster(extractBytes(request.getPoster()));
        }

        Event saved = eventRepository.save(event);
        return eventMapper.toResponse(saved);
    }

    private void applyTypeSpecificRules(Event event, EventType type, EventLocation location, Boolean foodProvided) {
        event.setType(type);

        switch (type) {
            case INTERNAL -> {
                event.setLocation(EventLocation.ALL);
                event.setFoodProvided(foodProvided != null ? foodProvided : false);
            }
            case EXTERNAL -> {
                validateSpecificCityLocation(location, type);
                event.setLocation(location);
                event.setFoodProvided(null);
            }
            case LOCAL -> {
                validateSpecificCityLocation(location, type);
                event.setLocation(location);
                event.setFoodProvided(foodProvided != null ? foodProvided : false);
            }
        }
    }

    private void validateSpecificCityLocation(EventLocation location, EventType type) {
        if (location == null || location == EventLocation.ALL) {
            throw new InvalidEventDataException("For type " + type + ", location needs to be: CLUJ, TIMISOARA or MURES.");
        }
    }

    private void validatePoster(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return;
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidFileException("File is over the maximum size of 5MB.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new InvalidFileException("File format should either be JPEG or PNG.");
        }
    }

    private void validateDates(EventRequest request) {
        if (request.getEndDateTime().isBefore(request.getStartDateTime())) {
            throw new InvalidEventDataException("End date time must be after start date time.");
        }
        if (request.getRegistrationEndDate().isBefore(request.getRegistrationStartDate())) {
            throw new InvalidEventDataException("Registration end date must be after registration start date.");
        }
    }

    private byte[] extractBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new PosterNotReadException("Poster could not be read.");
        }
    }

}
