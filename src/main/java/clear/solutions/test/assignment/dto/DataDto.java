package clear.solutions.test.assignment.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class DataDto<T> {
    @NotNull(message = "Data must be present")
    @Valid
    private T data;

    public static <T> DataDto<T> of(T data) {
        final var dataDto = new DataDto<T>();
        dataDto.setData(data);
        return dataDto;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
