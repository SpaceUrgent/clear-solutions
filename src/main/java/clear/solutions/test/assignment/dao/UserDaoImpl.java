package clear.solutions.test.assignment.dao;

import clear.solutions.test.assignment.model.User;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserDaoImpl extends AbstractDao<User> implements UserDao {

    @Override
    public List<User> findByBirthDateRange(final LocalDate from, final LocalDate to) {
        Assert.notNull(from, "from must be not null");
        Assert.notNull(to, "to must be not null");
        return this.entities.values().stream()
                .filter(user -> user.getBirthDate().isAfter(from) || user.getBirthDate().equals(from))
                .filter(user -> user.getBirthDate().isBefore(to) || user.getBirthDate().equals(to))
                .collect(Collectors.toList());
    }
}
