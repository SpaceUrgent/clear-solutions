package clear.solutions.test.assignment.dao;

import clear.solutions.test.assignment.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class UserDaoTest {

    public static final String EMAIL = "test@mail.com";
    public static final String FIRST_NAME = "John";
    public static final String LAST_NAME = "Johnson";
    public static final LocalDate BIRTH_DATE = LocalDate.of(1999, 1, 1);
    public static final String ADDRESS = "address";
    public static final String PHONE = "123456789";

    private static User USER;

    private UserDao userDao;

    @BeforeEach
    void setUp() {
        userDao = new UserDao();

        USER = new User();
        USER.setEmail(EMAIL);
        USER.setFirstName(FIRST_NAME);
        USER.setLastName(LAST_NAME);
        USER.setBirthDate(BIRTH_DATE);
        USER.setAddress(ADDRESS);
        USER.setPhone(PHONE);
    }

    @Test
    @DisplayName("Save new - OK")
    void save_savesNew() {
        final var saved = userDao.save(USER);

        assertNotNull(saved.getId());
        assertNotSame(saved, USER);
        assertEquals(EMAIL, saved.getEmail());
        assertEquals(FIRST_NAME, saved.getFirstName());
        assertEquals(LAST_NAME, saved.getLastName());
        assertEquals(BIRTH_DATE, saved.getBirthDate());
        assertEquals(ADDRESS, saved.getAddress());
        assertEquals(PHONE, saved.getPhone());

        assertEquals(saved, userDao.findById(saved.getId()).orElse(null));
    }

    @Test
    @DisplayName("Save updates entity by id")
    void save_updatesById() {
        final var newEmail = "newEmail";
        final var saved = userDao.save(USER);
        assertNotNull(saved);
        final var id = saved.getId();
        final var expected = new User();
        BeanUtils.copyProperties(saved, expected);
        expected.setEmail(newEmail);
        expected.setId(id);

        saved.setEmail(newEmail);
        final var updated = userDao.save(saved);
        assertEquals(expected, updated);
        assertEquals(expected, userDao.findById(id).orElse(null));
    }
}