package clear.solutions.test.assignment.controller;

import clear.solutions.test.assignment.dto.DataDto;
import clear.solutions.test.assignment.dto.RegisterUserRequest;
import clear.solutions.test.assignment.dto.RegisterUserResponse;
import clear.solutions.test.assignment.mapper.UserMapper;
import clear.solutions.test.assignment.model.User;
import clear.solutions.test.assignment.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping
    public DataDto<RegisterUserResponse> register(@Valid @RequestBody final DataDto<RegisterUserRequest> dataDto,
                                                  final HttpServletResponse servletResponse) {
        final var registered = userService.register(userMapper.toUser(dataDto.getData()));
        servletResponse.addHeader(HttpHeaders.LOCATION, "/users/%d".formatted(registered.getId()));
        return DataDto.of(userMapper.toRegisterResponse(registered));
    }

    @PutMapping("/{userId}")
    public DataDto<RegisterUserResponse> updateUser(@PathVariable final Long userId,
                                                    @Valid @RequestBody final DataDto<RegisterUserRequest> dataDto) {
        var user = userService.find(userId);
        userMapper.updateUser(user, dataDto.getData());
        user = userService.register(user);
        return DataDto.of(userMapper.toRegisterResponse(user));
    }
}
