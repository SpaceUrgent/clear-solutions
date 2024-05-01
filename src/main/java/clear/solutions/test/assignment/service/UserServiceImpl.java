package clear.solutions.test.assignment.service;

import clear.solutions.test.assignment.configuration.UserConfigurationProperties;
import clear.solutions.test.assignment.dao.UserDaoImpl;
import clear.solutions.test.assignment.exception.ApiException;
import clear.solutions.test.assignment.exception.Error;
import clear.solutions.test.assignment.model.User;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    private final UserConfigurationProperties properties;
    private final UserDaoImpl userDao;

    public UserServiceImpl(UserConfigurationProperties properties,
                           UserDaoImpl userDao) {
        this.properties = properties;
        this.userDao = userDao;
    }

    @Override
    public User save(final User user) {
        this.checkMinAge(user);
        return this.userDao.save(user);
    }

    @Override
    public User find(Long userId) {
        return this.userDao.findById(userId)
                .orElseThrow(() -> new ApiException(Error.USER_NOT_FOUND));
    }

    @Override
    public List<User> findByBirthDateRange(LocalDate from, LocalDate to) {
        checkRange(from, to);
        return this.userDao.findByBirthDateRange(from, to);
    }

    @Override
    public void delete(Long userId) {
        this.find(userId);
        this.userDao.deleteById(userId);
    }

    private void checkMinAge(User user) {
        final var lowestBirthDate = LocalDate.now().minusYears(properties.getMinAge());
        if (user.getBirthDate() == null
                || user.getBirthDate().isAfter(lowestBirthDate)) {
            throw new ApiException(Error.INVALID_AGE);
        }
    }

    private void checkRange(LocalDate from, LocalDate to) {
        final var errorDetails = new HashMap<String, String>();
        if (Objects.isNull(from)) {
            errorDetails.put("from", "Required parameter 'from' is not present.");
        }
        if (Objects.isNull(to)) {
            errorDetails.put("to", "Required parameter 'to' is not present.");
        }
        if (Objects.nonNull(from) && Objects.nonNull(to) && from.isAfter(to)) {
            errorDetails.put("to, from", "To must be greater or equals from.");
        }
        if (errorDetails.isEmpty()) {
            return;
        }
        throw new ApiException(Error.BAD_REQUEST, errorDetails);
    }
}
