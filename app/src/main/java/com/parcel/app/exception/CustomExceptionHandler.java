package com.parcel.app.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.parcel.app.dto.shared.ErrorResponse;
import com.parcel.app.util.MainLogger;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler implements AccessDeniedHandler {

    private final MainLogger logger = MainLogger.getLogger(CustomExceptionHandler.class);


    public static final String VALIDATION_FAILED_MSG = "Validation failed!";
    public static final String UNSUPPORTED_METHOD_MSG = "This http method is not allowed here!";
    public static final String INTERNAL_ERROR_MSG = "Request cannot be processed by the server!";
    public static final String RECORD_NOT_FOUND_MSG = "Record not found!";
    public static final String ILLEGAL_STATE_MSG = "Forbidden! Invalid token provided";
    public static final String PARAM_TYPE_MISMATCH_MSG = "Parameter type is incorrect!";


    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return getError(BAD_REQUEST, VALIDATION_FAILED_MSG, ex.getMostSpecificCause().getMessage(), ex, request);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            TypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return getError(BAD_REQUEST, PARAM_TYPE_MISMATCH_MSG, ex.getMostSpecificCause().getMessage(), ex, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String errorDetail = ex.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining("; "));
        return getError(BAD_REQUEST, VALIDATION_FAILED_MSG, errorDetail, ex, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return getError(METHOD_NOT_ALLOWED, UNSUPPORTED_METHOD_MSG, ex.getMessage(), ex, request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return getError(BAD_REQUEST, VALIDATION_FAILED_MSG, ex.getLocalizedMessage(), ex, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                             HttpStatus status, WebRequest request) {
        return getError(BAD_REQUEST, INTERNAL_ERROR_MSG, ex.getLocalizedMessage(), ex, request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<Object> handleIllegalStateException(AccessDeniedException ex, WebRequest request) {
        String errorDetail = ex.getLocalizedMessage();
        return getError(FORBIDDEN, ILLEGAL_STATE_MSG, errorDetail, ex, request); // STILL DOES NOT WORK
    }

    @ExceptionHandler(RecordNotFoundException.class)
    protected ResponseEntity<Object> handleRecordNotFoundException(RecordNotFoundException ex, WebRequest request) {
        String errorDetail = ex.getLocalizedMessage();
        return getError(NOT_FOUND, RECORD_NOT_FOUND_MSG, errorDetail, ex, request);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        String errorDetail = ex.getMessage();
        return getError(INTERNAL_SERVER_ERROR, INTERNAL_ERROR_MSG, errorDetail, ex, request);
    }

    private ResponseEntity<Object> getError(
            HttpStatus status, String message, String errorDetail, Exception ex, WebRequest request) {
        logger.error("{} : {}", ex.getClass().getSimpleName(), ex.getMessage());
        return ResponseEntity.status(status)
                .body(ErrorResponse.builder()
                        .status(status.value())
                        .error(status.getReasonPhrase())
                        .message(message)
                        .errorDetail(errorDetail)
                        .path(request.getDescription(false))
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException e) throws IOException, ServletException {
        logger.error(e.getLocalizedMessage(), e);
        String message = ErrorResponse.builder()
                .status(FORBIDDEN.value())
                .message("Invalid Authorization token")
                .path(request.getRequestURI())
                .build().toString();
        response.setStatus(FORBIDDEN.value());
        response.setContentType(APPLICATION_JSON_VALUE);
        response.getWriter().write(message); // DOES NOT WORK. STILL NO MESSAGE DISPLAYED
    }
}
