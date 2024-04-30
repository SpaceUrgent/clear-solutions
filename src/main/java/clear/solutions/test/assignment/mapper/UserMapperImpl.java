package clear.solutions.test.assignment.mapper;

import clear.solutions.test.assignment.dto.CreateUserDto;
import clear.solutions.test.assignment.dto.UserDto;
import clear.solutions.test.assignment.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toUser(final CreateUserDto request) {
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
    public void updateUser(User target, CreateUserDto request) {
        target.setEmail(request.getEmail());
        target.setFirstName(request.getFirstName());
        target.setLastName(request.getLastName());
        target.setBirthDate(request.getBirthDate());
        target.setAddress(request.getAddress());
        target.setPhone(request.getPhone());
    }

    @Override
    public UserDto toRegisterResponse(final User user) {
        final var response = new UserDto();
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
