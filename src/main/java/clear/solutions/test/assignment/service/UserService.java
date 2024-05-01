package clear.solutions.test.assignment.service;

import clear.solutions.test.assignment.model.User;

import java.time.LocalDate;
import java.util.List;

public interface UserService {
    User save(User user);

    User find(Long userId);

    List<User> findByBirthDateRange(LocalDate from, LocalDate to);

    void delete(Long userId);
}
