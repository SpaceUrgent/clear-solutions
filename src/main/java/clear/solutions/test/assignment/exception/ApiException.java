package clear.solutions.test.assignment.exception;

public class ApiException extends RuntimeException {
    private final Error error;

    public ApiException(Error error) {
        this.error = error;
    }

    public Error getError() {
        return error;
    }
}
