package clear.solutions.test.assignment.service;

import clear.solutions.test.assignment.configuration.UserConfigurationProperties;
import clear.solutions.test.assignment.dao.UserDao;
import clear.solutions.test.assignment.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private static final Integer MIN_AGE = 18;

    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserConfigurationProperties properties;
    @Mock
    private UserDao userDao;

    @Test
    @DisplayName("Register - OK")
    void register_ok() {
        final var email = "test@mail.com";
        final var birthDate = LocalDate.now().minusYears(MIN_AGE + 1);
        var user = new User();
        user.setEmail(email);
        user.setBirthDate(birthDate);
        var saved = new User();
        saved.setBirthDate(birthDate);
        saved.setEmail(email);
        saved.setId(1L);

        doReturn(MIN_AGE).when(properties).getMinAge();
        doReturn(saved).when(userDao).save(eq(user));
        final var registered = userService.register(user);
        assertSame(saved, registered);
        verify(userDao).save(user);
    }

    @Test
    @DisplayName("Register with null birth date throws")
    void register_withNullBirthDate_throws() {
        final var user = new User();
        doReturn(MIN_AGE).when(properties).getMinAge();
        assertThrows(
                IllegalArgumentException.class,
                () -> userService.register(user)
        );
        verify(userDao, times(0)).save(any());
    }

    @Test
    @DisplayName("Register with low age throws")
    void register_withLowAge_throws() {
        final var user = new User();
        user.setBirthDate(LocalDate.now().minusYears(MIN_AGE).plusDays(1));
        doReturn(MIN_AGE).when(properties).getMinAge();
        assertThrows(
                IllegalArgumentException.class,
                () -> userService.register(user)
        );
        verify(userDao, times(0)).save(any());
    }
}