package clear.solutions.test.assignment.dao;

import clear.solutions.test.assignment.model.Entity;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractDao<T extends Entity> implements Dao<T> {

    private final AtomicLong sequence;
    protected final Map<Long, T> entities;

    public AbstractDao() {
        this.sequence = new AtomicLong(0L);
        this.entities = new ConcurrentHashMap<>();
    }

    public T save(T entity) {
        if (entity.getId() == null) {
            entity.setId(sequence.incrementAndGet());
        }
        entities.put(entity.getId(), entity);
        return (T) entity.clone();
    }

    public Optional<T> findById(Long id) {
        Assert.notNull(id, "id must be not null");
        return Optional.ofNullable(entities.get(id))
                .map(entity -> (T) entity.clone());
    }

    public void deleteAll() {
        this.entities.clear();
    }

    public void deleteById(Long userId) {
        this.entities.remove(userId);
    }

    public long countAll() {
        return this.entities.size();
    }
}
