package clear.solutions.test.assignment.model;

public interface Entity {

    Long getId();

    void setId(Long id);

    Entity clone();
}
