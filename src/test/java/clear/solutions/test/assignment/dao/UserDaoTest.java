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
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        assertEquals(1, userDao.countAll());
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
        assertEquals(1, userDao.countAll());
    }

    @Test
    @DisplayName("Find by id return optional of entity")
    void findById_returnsOptionalEntity() {
        final var id = userDao.save(USER).getId();
        final var userFound = userDao.findById(id).orElse(null);
        assertNotNull(userFound);
        assertNotSame(USER, userFound);
        assertEquals(USER, userFound);
    }

    @Test
    @DisplayName("Find by id return optional empty")
    void findById_returnsOptionalEmpty() {
        assertTrue(userDao.findById(100L).isEmpty());
    }

    @Test
    @DisplayName("Delete all")
    void deleteAll() {
        final var userCount = 10;
        for (int i = 0; i < userCount; i++) {
            final var user = new User();
            user.setEmail("email%d@gmail.com".formatted(i));
            user.setFirstName("name%d".formatted(i));
            user.setLastName("last%d".formatted(i));
            user.setBirthDate(LocalDate.now().minusYears(50));
            user.setAddress("address%d".formatted(i));
            user.setPhone("phone%d".formatted(i));
            userDao.save(user);
        }
        assertEquals(userCount, userDao.countAll());
        userDao.deleteAll();
        assertEquals(0, userDao.countAll());
    }

    @Test
    @DisplayName("Delete by id")
    void deleteById() {
        final var userId = userDao.save(USER).getId();
        assertTrue(userDao.findById(userId).isPresent());
        userDao.deleteById(userId);
        assertTrue(userDao.findById(userId).isEmpty());
    }
}