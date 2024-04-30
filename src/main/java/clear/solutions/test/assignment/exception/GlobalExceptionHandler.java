package clear.solutions.test.assignment.exception;

import clear.solutions.test.assignment.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BindException.class)
    public ResponseEntity<?> handleBindException(BindException exception,
                                                 HttpServletRequest request) {
        final var details = exception.getBindingResult().getAllErrors().stream()
                .collect(Collectors.toUnmodifiableMap(
                        objectError -> ((FieldError) objectError).getField(),
                        DefaultMessageSourceResolvable::getDefaultMessage));
        final var errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(400);
        errorResponse.setReason("Bad request, missing or invalid request arguments");
        errorResponse.setDetails(details);
        errorResponse.setPath(getPath(request));
        return ResponseEntity.status(400).body(errorResponse);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<?> handleApiException(ApiException exception,
                                                HttpServletRequest request) {
        final var error = exception.getError();
        final var errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(error.getHttpStatus().value());
        errorResponse.setReason(error.getReason());
        errorResponse.setPath(getPath(request));
        return ResponseEntity.status(error.getHttpStatus().value()).body(errorResponse);
    }

    private String getPath(final HttpServletRequest request) {
        return ServletUriComponentsBuilder.fromRequest(request).build().getPath();
    }
}