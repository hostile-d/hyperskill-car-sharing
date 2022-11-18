package carsharing.business;

public class Car {
    private final String name;
    private final String companyName;
    public Car(String name, String companyName) {
        this.name = name;
        this.companyName = companyName;
    }
    public String getName() {
        return this.name;
    }
    public String getCompanyName() {
        return companyName;
    }
}
