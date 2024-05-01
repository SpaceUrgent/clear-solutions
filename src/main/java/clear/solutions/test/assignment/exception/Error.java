package clear.solutions.test.assignment.exception;

import org.springframework.http.HttpStatus;

public enum Error {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad request, missing or invalid request arguments"),
    INVALID_AGE(HttpStatus.UNPROCESSABLE_ENTITY, "Age is below the minimum"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found");

    private final HttpStatus httpStatus;
    private final String reason;

    Error(HttpStatus httpStatus, String reason) {
        this.httpStatus = httpStatus;
        this.reason = reason;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getReason() {
        return reason;
    }
}
