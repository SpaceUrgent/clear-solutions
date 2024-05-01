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
}
