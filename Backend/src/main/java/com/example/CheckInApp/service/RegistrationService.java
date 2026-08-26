package com.example.CheckInApp.service;

import com.example.CheckInApp.dto.mapper.RegistrationMapper;
import com.example.CheckInApp.dto.request.RegistrationRequest;
import com.example.CheckInApp.dto.response.RegistrationResponse;
import com.example.CheckInApp.exception.AlreadyRegisteredException;
import com.example.CheckInApp.exception.InvalidRegistrationDataException;
import com.example.CheckInApp.exception.RegistrationClosedException;
import com.example.CheckInApp.exception.ResourceNotFoundException;
import com.example.CheckInApp.exception.WithdrawnRegistrationException;
import com.example.CheckInApp.model.*;
import com.example.CheckInApp.repository.AttendanceRecordRepository;
import com.example.CheckInApp.repository.EventRepository;
import com.example.CheckInApp.repository.RegistrationRepository;
import com.example.CheckInApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RegistrationMapper registrationMapper;
    private final EventService eventService;

    @Transactional
    public RegistrationResponse register(Long eventId, RegistrationRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + userEmail));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + eventId));

        if (!eventService.hasFullAccess(user) && !eventService.isEventVisibleTo(event, user)) {
            throw new ResourceNotFoundException("Event not found with id " + eventId);
        }

        validateEligibility(event, user);
        validateConsents(event, request);

        Registration registration = Registration.builder()
                .event(event)
                .user(user)
                .gdprConsent(Boolean.TRUE.equals(request.getGdprConsent()))
                .photoConsent(Boolean.TRUE.equals(request.getPhotoConsent()))
                .registrationDate(LocalDate.now())
                .status(RegistrationStatus.CONFIRMED)
                .build();

        applyFoodPreference(event, request, registration);
        applyInternalEventDetails(event, request, registration);

        try {
            Registration saved = registrationRepository.save(registration);
            AttendanceRecord attendanceRecord = AttendanceRecord.builder()
                    .event(event)
                    .user(user)
                    .checkedIn(false)
                    .build();

            attendanceRecordRepository.save(attendanceRecord);
            return registrationMapper.toResponse(saved);
        } catch (DataIntegrityViolationException e) {
            throw new AlreadyRegisteredException("You are already registered for this event.");
        }
    }

    private void validateEligibility(Event event, User user) {
        if (event.getStatus() != EventStatus.PUBLISHED) {
            throw new InvalidRegistrationDataException("Registration is only possible for published events.");
        }

        LocalDate today = LocalDate.now();
        if (event.getRegistrationStartDate().isAfter(today)) {
            throw new RegistrationClosedException("Registration has not started yet for this event.");
        }
        if (event.getRegistrationEndDate().isBefore(today)) {
            throw new RegistrationClosedException("Registration is closed for this event.");
        }

        if (registrationRepository.existsByEventIdAndUserId(event.getId(), user.getId())) {
            throw new AlreadyRegisteredException("You are already registered for this event.");
        }
    }

    private void validateConsents(Event event, RegistrationRequest request) {
        if (event.getType() != EventType.EXTERNAL && !Boolean.TRUE.equals(request.getGdprConsent())) {
            throw new InvalidRegistrationDataException(
                    "GDPR consent is required to register for " + event.getType() + " events.");
        }
    }

    private void applyFoodPreference(Event event, RegistrationRequest request, Registration registration) {
        if (Boolean.TRUE.equals(event.getFoodProvided())) {
            if (request.getFoodPreference() == null) {
                throw new InvalidRegistrationDataException(
                        "Food preference (NONE, VEGETARIAN or VEGAN) is required because food is provided at this event.");
            }
            registration.setFoodPreference(request.getFoodPreference());
        } else {
            registration.setFoodPreference(null);
        }
    }

    private void applyInternalEventDetails(Event event, RegistrationRequest request, Registration registration) {
        if (event.getType() != EventType.INTERNAL) {
            registration.setTransportNeeded(null);
            registration.setDriverName(null);
            registration.setDriverPhoneNumber(null);
            registration.setAccommodationNeeded(null);
            registration.setAccommodationDays(null);
            return;
        }

        if (request.getTransportNeeded() == null) {
            throw new InvalidRegistrationDataException(
                    "Transportation need (yes/no) is required for internal events.");
        }
        registration.setTransportNeeded(request.getTransportNeeded());

        if (Boolean.TRUE.equals(request.getTransportNeeded())) {
            if (request.getDriverName() == null || request.getDriverName().isBlank()) {
                throw new InvalidRegistrationDataException(
                        "Driver name is required when transportation is needed.");
            }
            if (request.getDriverPhoneNumber() == null || request.getDriverPhoneNumber().isBlank()) {
                throw new InvalidRegistrationDataException(
                        "Driver telephone number is required when transportation is needed.");
            }
            registration.setDriverName(request.getDriverName());
            registration.setDriverPhoneNumber(request.getDriverPhoneNumber());
        }

        if (request.getAccommodationNeeded() == null) {
            throw new InvalidRegistrationDataException(
                    "Accommodation need (yes/no) is required for internal events.");
        }
        registration.setAccommodationNeeded(request.getAccommodationNeeded());

        if (Boolean.TRUE.equals(request.getAccommodationNeeded())) {
            if (request.getAccommodationDays() == null || request.getAccommodationDays() <= 0) {
                throw new InvalidRegistrationDataException(
                        "A positive number of accommodation days is required when accommodation is needed.");
            }
            registration.setAccommodationDays(request.getAccommodationDays());
        }
    }

    public RegistrationResponse getMyRegistration(Long eventId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + userEmail));

        Registration registration = registrationRepository.findByEventIdAndUserId(eventId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found for event " + eventId));

        return registrationMapper.toResponse(registration);
    }

    @Transactional
    public RegistrationResponse editRegistration(Long eventId, RegistrationRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + userEmail));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + eventId));

        Registration registration = registrationRepository.findByEventIdAndUserId(eventId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found for event " + eventId));

        if (registration.getStatus() == RegistrationStatus.WITHDRAWN) {
            throw new WithdrawnRegistrationException("Cannot edit a withdrawn registration.");
        }

        applyFoodPreference(event, request, registration);
        applyInternalEventDetails(event, request, registration);

        return registrationMapper.toResponse(registrationRepository.save(registration));
    }

    @Transactional
    public RegistrationResponse withdrawRegistration(Long eventId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + userEmail));

        Registration registration = registrationRepository.findByEventIdAndUserId(eventId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found for event " + eventId));

        if (registration.getStatus() == RegistrationStatus.WITHDRAWN) {
            throw new WithdrawnRegistrationException("Registration is already withdrawn.");
        }

        registration.setStatus(RegistrationStatus.WITHDRAWN);
        return registrationMapper.toResponse(registrationRepository.save(registration));
    }

}
