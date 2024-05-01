package clear.solutions.test.assignment.service;

import clear.solutions.test.assignment.configuration.UserConfigurationProperties;
import clear.solutions.test.assignment.dao.UserDaoImpl;
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
import java.util.ArrayList;
import java.util.Map;
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
    private UserDaoImpl userDao;

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

    @Test
    @DisplayName("Find users by birth date range")
    void findByBirthDateRange() {
        final var users = new ArrayList<User>();
        for (long i = 0; i < 5; i++) {
            final var user = new User();
            user.setId(i);
            users.add(user);
        }
        final var from = LocalDate.now().minusDays(1);
        final var to = LocalDate.now().plusDays(1);
        doReturn(users).when(userDao).findByBirthDateRange(eq(from), eq(to));
        assertEquals(users, userService.findByBirthDateRange(from, to));
    }

    @Test
    @DisplayName("Find users with null args throws api exception")
    void findByBirthDateRange_withNullArgs_throws() {
        final var errorDetails = Map.of(
                "from", "Required parameter 'from' is not present.",
                "to", "Required parameter 'to' is not present."
        );
        final var exception = assertThrows(
                ApiException.class,
                () -> userService.findByBirthDateRange(null, null)
        );
        assertEquals(Error.BAD_REQUEST, exception.getError());
        assertEquals(errorDetails, exception.getErrorDetails());
    }

    @Test
    @DisplayName("Find users with invalid birth date range throws api exception")
    void findByBirthDateRange_withInvalidRange_throws() {
        final var exception = assertThrows(
                ApiException.class,
                () -> userService.findByBirthDateRange(LocalDate.now().plusDays(1), LocalDate.now().minusDays(1))
        );
        assertEquals(Error.BAD_REQUEST, exception.getError());
        assertEquals(Map.of("to, from", "To must be greater or equals from."), exception.getErrorDetails());
    }

    @Test
    @DisplayName("Delete - OK")
    void delete_ok() {
        final var id = 1L;
        USER.setId(id);
        doReturn(Optional.of(USER)).when(userDao).findById(id);
        userService.delete(id);
        verify(userDao).deleteById(eq(id));
    }

    @Test
    @DisplayName("Delete with non-existing id throws api exception")
    void delete_withNonExistingUserId_throws() {
        final var id = 1L;
        doReturn(Optional.empty()).when(userDao).findById(id);
        final var exception = assertThrows(
                ApiException.class,
                () -> userService.delete(id)
        );
        assertEquals(Error.USER_NOT_FOUND, exception.getError());
    }
}