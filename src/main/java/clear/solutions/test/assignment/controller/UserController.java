package clear.solutions.test.assignment.controller;

import clear.solutions.test.assignment.dto.DataDto;
import clear.solutions.test.assignment.dto.CreateUserDto;
import clear.solutions.test.assignment.dto.UserContactsDto;
import clear.solutions.test.assignment.dto.UserDto;
import clear.solutions.test.assignment.mapper.UserMapper;
import clear.solutions.test.assignment.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
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
    public DataDto<UserDto> register(@Valid @RequestBody final DataDto<CreateUserDto> dataDto,
                                     final HttpServletResponse servletResponse) {
        final var registered = userService.save(userMapper.toUser(dataDto.getData()));
        servletResponse.addHeader(HttpHeaders.LOCATION, "/users/%d".formatted(registered.getId()));
        return DataDto.of(userMapper.toRegisterResponse(registered));
    }

    @PutMapping("/{userId}")
    public DataDto<UserDto> updateUser(@PathVariable final Long userId,
                                       @Valid @RequestBody final DataDto<CreateUserDto> dataDto) {
        var user = userService.find(userId);
        userMapper.updateUser(user, dataDto.getData());
        user = userService.save(user);
        return DataDto.of(userMapper.toRegisterResponse(user));
    }

    @PatchMapping("/{userId}/contacts")
    public DataDto<UserDto> patchUser(@PathVariable final Long userId,
                                       @Valid @RequestBody final DataDto<UserContactsDto> dataDto) {
        var user = userService.find(userId);
        userMapper.patchUser(user, dataDto.getData());
        user = userService.save(user);
        return DataDto.of(userMapper.toRegisterResponse(user));
    }
}
