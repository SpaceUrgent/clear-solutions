package clear.solutions.test.assignment.service;

import clear.solutions.test.assignment.configuration.UserConfigurationProperties;
import clear.solutions.test.assignment.dao.UserDao;
import clear.solutions.test.assignment.exception.ApiException;
import clear.solutions.test.assignment.exception.Error;
import clear.solutions.test.assignment.model.User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class UserServiceImpl implements UserService {

    private final UserConfigurationProperties properties;
    private final UserDao userDao;

    public UserServiceImpl(UserConfigurationProperties properties,
                           UserDao userDao) {
        this.properties = properties;
        this.userDao = userDao;
    }

    @Override
    public User register(final User user) {
        this.checkMinAge(user);
        return userDao.save(user);
    }

    private void checkMinAge(User user) {
        final var lowestBirthDate = LocalDate.now().minusYears(properties.getMinAge());
        if (user.getBirthDate() == null
                || user.getBirthDate().isAfter(lowestBirthDate)) {
            throw new ApiException(Error.INVALID_AGE);
        }
    }
}
