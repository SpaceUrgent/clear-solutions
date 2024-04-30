package clear.solutions.test.assignment.mapper;

import clear.solutions.test.assignment.dto.RegisterUserRequest;
import clear.solutions.test.assignment.dto.RegisterUserResponse;
import clear.solutions.test.assignment.model.User;

public interface UserMapper {
    User toUser(RegisterUserRequest request);

    void updateUser(User target, RegisterUserRequest request);

    RegisterUserResponse toRegisterResponse(User user);
}
