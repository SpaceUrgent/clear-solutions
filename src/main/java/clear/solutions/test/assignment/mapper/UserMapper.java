package clear.solutions.test.assignment.mapper;

import clear.solutions.test.assignment.dto.CreateUserDto;
import clear.solutions.test.assignment.dto.UserDto;
import clear.solutions.test.assignment.model.User;

public interface UserMapper {
    User toUser(CreateUserDto request);

    void updateUser(User target, CreateUserDto request);

    UserDto toRegisterResponse(User user);
}
