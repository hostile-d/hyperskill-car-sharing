package carsharing.persistance;

import carsharing.business.Car;
import carsharing.business.Company;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.Callable;

public class DBManager {
    final String JDBC_DRIVER = "org.h2.Driver";
    final String DB_URL = "jdbc:h2:./src/carsharing/db/";
    Connection connection = null;
    Statement statement = null;
    final String dbFileName;
    public DBManager (String dbFileName) {
        this.dbFileName = dbFileName;
    }
    public void createDB() {
        exceptionHandler(() -> {
            statement = connection.createStatement();
            String createCompanyTable = "CREATE TABLE IF NOT EXISTS company (" +
                    "id INTEGER NOT NULL AUTO_INCREMENT, " +
                    " name VARCHAR(255) NOT NULL UNIQUE, " +
                    " PRIMARY KEY (id)" +
                    ")";
            statement.executeUpdate(createCompanyTable);

            String createCarTable = "CREATE TABLE IF NOT EXISTS car (" +
                    " id INTEGER NOT NULL AUTO_INCREMENT, " +
                    " name VARCHAR NOT NULL UNIQUE, " +
                    " company_id INT NOT NULL, " +
                    " CONSTRAINT fk_company FOREIGN KEY (company_id)" +
                    " REFERENCES company(id), " +
                    " PRIMARY KEY (id)" +
                    ")";
            statement.executeUpdate(createCarTable);

            String createCustomerTable = "CREATE TABLE IF NOT EXISTS customer (" +
                    " id INTEGER NOT NULL AUTO_INCREMENT, " +
                    " name VARCHAR(255) NOT NULL UNIQUE, " +
                    " rented_car_id INTEGER, " +
                    " CONSTRAINT fk_car FOREIGN KEY (rented_car_id)" +
                    " REFERENCES car(id), " +
                    " PRIMARY KEY (id)" +
                    ")";
            statement.executeUpdate(createCustomerTable);

            statement.close();
            return null;
        });
    }

    public void addCompany(String name) {
        exceptionHandler(() -> {
            statement = connection.createStatement();
            String sql =  "INSERT INTO company (NAME) VALUES ('" + name + "')";
            statement.executeUpdate(sql);

            statement.close();
            return null;
        });
    }

    public ArrayList<Company> listCompanies() {
        ArrayList<Company> companies = new ArrayList<Company>();
        exceptionHandler(() -> {
            statement = connection.createStatement();
            String sql =  "SELECT * FROM company";
            var result = statement.executeQuery(sql);

            while(result.next()) {
                var id = result.getInt("id");
                var name = result.getString("name");
                companies.add(new Company(id, name));
            }

            statement.close();
            return null;
        });
        return companies;
    }

    public void addCarToCompany(String companyName, String carName) {
        exceptionHandler(() -> {
            statement = connection.createStatement();
            String getId =  "SELECT id FROM company WHERE name='" + companyName + "'";
            var idResult = statement.executeQuery(getId);
            Integer companyId = null;
            while (idResult.next()) {
                companyId = idResult.getInt("id");
            }
            statement.close();

            if (companyId != null) {
                statement = connection.createStatement();
                String addCar = "INSERT INTO car (company_id, name) VALUES ('" + companyId + "','" + carName + "')";
                statement.executeUpdate(addCar);
            }
            return null;
        });
    }

    public void addCustomer(String customerName, Optional<Integer> customerCarId) {
        var carId = customerCarId.isEmpty() ? null : "'" + customerCarId.get() + "'";
        exceptionHandler(() -> {
            statement = connection.createStatement();
            String addCar = "INSERT INTO customer (name, rented_car_id) VALUES ('"+customerName+"', "+carId+")";
            statement.executeUpdate(addCar);
            return null;
        });
    }

    public ArrayList<String> listCustomers() {
        ArrayList<String> customers = new ArrayList<String>();
        exceptionHandler(() -> {
            statement = connection.createStatement();
            String sql =  "SELECT * FROM customer";
            var result = statement.executeQuery(sql);

            while(result.next()) {
                customers.add(result.getString("name"));
            }

            statement.close();
            return null;
        });
        return customers;
    }


    public Car getCustomerCar(String customerName) {
        var result = new ArrayList<Car>();
        exceptionHandler(() -> {
            var carId = "";
            var carName = "";
            var companyId = "";
            var companyName = "";
            statement = connection.createStatement();

            String customerQuery =  "SELECT * FROM customer WHERE name = '"+customerName+"'";
            var customerResult = statement.executeQuery(customerQuery);
            while(customerResult.next()) {
                carId = customerResult.getString("rented_car_id");
                if (carId == null) {
                    return null;
                }
            }

            String carQuery = "SELECT * FROM car WHERE id = " + carId;
            var carResult = statement.executeQuery(carQuery);
            while(carResult.next()) {
                carName = carResult.getString("name");
                companyId = carResult.getString("company_id");
                if (companyId == null) {
                    return null;
                }
            }

            String companyQuery = "SELECT * FROM company WHERE id = " + companyId;
            var companyResult = statement.executeQuery(companyQuery);
            while(companyResult.next()) {
                companyName = companyResult.getString("name");
            }

            result.add(new Car(carName, companyName));
            statement.close();
            return null;
        });
        if (!result.isEmpty()) {
            return result.get(0);
        }
        return null;
    }

    public void returnCar(String customerName) {
        exceptionHandler(() -> {
            statement = connection.createStatement();
            String returnCar = "UPDATE customer SET rented_car_id = null WHERE name = '" + customerName + "'";
            statement.executeUpdate(returnCar);
            return null;
        });
    }

    public void rentCar(Integer carId, String customerName) {
        exceptionHandler(() -> {
            statement = connection.createStatement();
            String returnCar = "UPDATE customer SET rented_car_id = '"+carId+"' WHERE name = '" + customerName + "'";
            statement.executeUpdate(returnCar);
            return null;
        });
    }

    public ArrayList<String> listCompanyCars(Integer companyId) {
        ArrayList<String> cars = new ArrayList<String>();
        exceptionHandler(() -> {
            ResultSet carsList = null;

            if (companyId != null) {
                statement = connection.createStatement();
                String listCars = "SELECT * FROM car WHERE company_id='" + companyId + "'";
                carsList = statement.executeQuery(listCars);
            }
            if (carsList != null) {
                while (carsList.next()) {
                    cars.add(carsList.getString("name"));
                }
            }
            return null;
        });
        return cars;
    }

    public Integer getCompanyIdByName(String companyName) {
        var result = new ArrayList<Integer>();
        exceptionHandler(() -> {
            statement = connection.createStatement();
            String getId =  "SELECT * FROM company WHERE name='" + companyName + "'";
            var idResult = statement.executeQuery(getId);
            if (idResult != null) {
                if (idResult.next()) {
                    result.add(idResult.getInt("id"));
                }
            }
            return null;
        });
        return result.get(0);
    }

    public void exceptionHandler(Callable<?> callback) {
        try {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL + dbFileName);
            connection.setAutoCommit(true);
            callback.call();
            connection.close();
        } catch (Exception se) {
            se.printStackTrace();
        } finally {
            try {
                if (statement!=null) {
                    statement.close();
                }
            } catch (SQLException ignored) {
            }
            try {
                if (connection!=null) {
                    connection.close();
                }
            } catch (SQLException se){
                se.printStackTrace();
            }
        }
    }
}
