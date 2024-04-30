package clear.solutions.test.assignment.service;

import clear.solutions.test.assignment.configuration.UserConfigurationProperties;
import clear.solutions.test.assignment.dao.UserDao;
import clear.solutions.test.assignment.exception.ApiException;
import clear.solutions.test.assignment.exception.Error;
import clear.solutions.test.assignment.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private static final Integer MIN_AGE = 18;

    private static User USER;

    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserConfigurationProperties properties;
    @Mock
    private UserDao userDao;

    @BeforeEach
    void setUp() {
        USER = new User();
        USER.setEmail("test@mail.com");
        USER.setBirthDate(LocalDate.now().minusYears(MIN_AGE + 1));
    }

    @Test
    @DisplayName("Save - OK")
    void save_ok() {
        var saved = new User();
        saved.setBirthDate(USER.getBirthDate());
        saved.setEmail(USER.getEmail());
        saved.setId(1L);

        doReturn(MIN_AGE).when(properties).getMinAge();
        doReturn(saved).when(userDao).save(eq(USER));
        final var registered = userService.save(USER);
        assertSame(saved, registered);
        verify(userDao).save(eq(USER));
    }

    @Test
    @DisplayName("Save with null birth date throws")
    void save_withNullBirthDate_throws() {
        final var user = new User();
        doReturn(MIN_AGE).when(properties).getMinAge();
        final var exception = assertThrows(
                ApiException.class,
                () -> userService.save(user)
        );
        assertEquals(Error.INVALID_AGE, exception.getError());
        verify(userDao, times(0)).save(any());
    }

    @Test
    @DisplayName("Save with low age throws")
    void save_withLowAge_throws() {
        final var user = new User();
        user.setBirthDate(LocalDate.now().minusYears(MIN_AGE).plusDays(1));
        doReturn(MIN_AGE).when(properties).getMinAge();
        final var exception = assertThrows(
                ApiException.class,
                () -> userService.save(user)
        );
        assertEquals(Error.INVALID_AGE, exception.getError());
        verify(userDao, times(0)).save(any());
    }

    @Test
    @DisplayName("Find - OK")
    void find_ok() {
        final var id = 1L;
        doReturn(Optional.of(USER)).when(userDao).findById(id);
        assertEquals(USER, userService.find(id));
    }

    @Test
    @DisplayName("Find with non-existing id throws api exception")
    void find_withNonExistingId_throws() {
        final var id = 1L;
        doReturn(Optional.empty()).when(userDao).findById(id);
        final var exception = assertThrows(
                ApiException.class,
                () -> userService.find(id)
        );
        assertEquals(Error.USER_NOT_FOUND, exception.getError());
    }
}