package clear.solutions.test.assignment.exception;

import org.springframework.http.HttpStatus;

public enum Error {
    INVALID_AGE(HttpStatus.UNPROCESSABLE_ENTITY, "Age is below the minimum");

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
