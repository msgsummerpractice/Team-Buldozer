package com.example.CheckInApp.exception;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ErrorCode.ERR_20, ex.getMessage(), request);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ErrorCode.ERR_01, "Invalid credentials", request);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFound(UsernameNotFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ErrorCode.ERR_02, "User not found", request);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ErrorCode.ERR_01, ex.getMessage(), request);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmail(DuplicateEmailException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ErrorCode.ERR_10, ex.getMessage(), request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ErrorCode.ERR_40, ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (a, b) -> a));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(PosterNotReadException.class)
    public ResponseEntity<ErrorResponse> handlePosterNotRead(PosterNotReadException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.ERR_110, ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<ErrorResponse> handleInvalidFile(InvalidFileException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ErrorCode.ERR_111, ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidEventDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidEventData(InvalidEventDataException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ErrorCode.ERR_112, ex.getMessage(), request);
    }

    @ExceptionHandler(EventNotEditableException.class)
    public ResponseEntity<ErrorResponse> handleEventNotEditable(EventNotEditableException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ErrorCode.ERR_113, ex.getMessage(), request);
    }

    @ExceptionHandler(ForbiddenActionException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenAction(ForbiddenActionException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, ErrorCode.ERR_114, ex.getMessage(), request);

    }

    @ExceptionHandler(DataBaseException.class)
    public ResponseEntity<ErrorResponse> handleDataBaseException(DataBaseException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.ERR_115, ex.getMessage(), request);
    }

    @ExceptionHandler(QrCodeGenerationException.class)
    public ResponseEntity<ErrorResponse> handleQrCodeGeneration(QrCodeGenerationException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.ERR_216, ex.getMessage(), request);
    }

    @ExceptionHandler(CodesAlreadyGeneratedException.class)
    public ResponseEntity<ErrorResponse> handleCodesAlreadyGenerated(CodesAlreadyGeneratedException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ErrorCode.ERR_217, ex.getMessage(), request);
    }

    @ExceptionHandler(CheckInCodeGenerationException.class)
    public ResponseEntity<ErrorResponse> handleCheckInCodeGeneration(CheckInCodeGenerationException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.ERR_218, ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidCheckInCodeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCheckInCode(InvalidCheckInCodeException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ErrorCode.ERR_219, ex.getMessage(), request);
    }

    @ExceptionHandler(NotRegisteredForEventException.class)
    public ResponseEntity<ErrorResponse> handleNotRegisteredForEvent(NotRegisteredForEventException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, ErrorCode.ERR_220, ex.getMessage(), request);
    }

    @ExceptionHandler(CheckInClosedException.class)
    public ResponseEntity<ErrorResponse> handleCheckInClosed(CheckInClosedException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ErrorCode.ERR_221, ex.getMessage(), request);
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, ErrorCode errorCode, String message, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .code(errorCode.getCode())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(error);
    }
}