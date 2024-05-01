package clear.solutions.test.assignment.dao;

import clear.solutions.test.assignment.model.User;

import java.time.LocalDate;
import java.util.List;

public interface UserDao extends Dao<User> {
    List<User> findByBirthDateRange(final LocalDate from, final LocalDate to);
}
