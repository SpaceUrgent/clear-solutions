package clear.solutions.test.assignment.exception;

import java.util.Map;

public class ApiException extends RuntimeException {
    private final Error error;
    private final Map<String, String> errorDetails;

    public ApiException(Error error) {
        this.error = error;
        this.errorDetails = null;
    }

    public ApiException(Error error, Map<String, String> errorDetails) {
        this.error = error;
        this.errorDetails = errorDetails;
    }

    public Error getError() {
        return error;
    }

    public Map<String, String> getErrorDetails() {
        return errorDetails;
    }
}
