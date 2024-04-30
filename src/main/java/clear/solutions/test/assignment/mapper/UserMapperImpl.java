package clear.solutions.test.assignment.mapper;

import clear.solutions.test.assignment.dto.RegisterUserRequest;
import clear.solutions.test.assignment.dto.RegisterUserResponse;
import clear.solutions.test.assignment.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toUser(final RegisterUserRequest request) {
        final var user = new User();
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setBirthDate(request.getBirthDate());
        user.setAddress(request.getAddress());
        user.setPhone(request.getPhone());
        return user;
    }

    @Override
    public RegisterUserResponse toRegisterResponse(final User user) {
        final var response = new RegisterUserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setBirthDate(user.getBirthDate());
        response.setAddress(user.getAddress());
        response.setPhone(user.getPhone());
        return response;
    }
}
