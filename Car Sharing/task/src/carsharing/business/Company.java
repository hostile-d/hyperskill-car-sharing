package carsharing.business;

public class Company {
    private final Integer id;
    private final String name;
    public Company(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
    public String getName() {
        return this.name;
    }
    public Integer getId() {
        return id;
    }
}
