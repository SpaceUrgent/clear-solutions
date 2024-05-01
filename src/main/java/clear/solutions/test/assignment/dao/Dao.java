package clear.solutions.test.assignment.dao;

import clear.solutions.test.assignment.model.Entity;

import java.util.Optional;

public interface Dao<T extends Entity> {

    T save(T entity);

    Optional<T> findById(Long id);

    void deleteAll();

    void deleteById(Long userId);

    long countAll();
}
