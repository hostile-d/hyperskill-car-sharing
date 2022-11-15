package carsharing;

import java.sql.*;
import java.util.ArrayList;
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
    public void dropOldDB() {
        exceptionHandler(() -> {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL + dbFileName);
            connection.setAutoCommit(true);

            statement = connection.createStatement();
            statement.execute("DROP TABLE IF EXISTS car");
            statement.close();

            statement = connection.createStatement();
            statement.execute("DROP TABLE IF EXISTS company");
            statement.close();

            statement.close();
            connection.close();
            return null;
        });
    }
    public void createDB() {
        exceptionHandler(() -> {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL + dbFileName);
            connection.setAutoCommit(true);


            statement = connection.createStatement();
            String createCompanyTable =  "CREATE TABLE  COMPANY (" +
                    "ID INTEGER NOT NULL AUTO_INCREMENT, " +
                    " NAME VARCHAR(255) NOT NULL UNIQUE, " +
                    " PRIMARY KEY ( id )" +
                    ")";
            statement.executeUpdate(createCompanyTable);
            String createCarTable =  "CREATE TABLE CAR (" +
                    " ID INTEGER NOT NULL AUTO_INCREMENT, " +
                    " NAME VARCHAR NOT NULL UNIQUE, " +
                    " COMPANY_ID INT NOT NULL, " +
                    " CONSTRAINT fk_company FOREIGN KEY (company_id)" +
                    " REFERENCES company(id), " +
                    " PRIMARY KEY ( id )" +
                    ")";
            statement.executeUpdate(createCarTable);

            statement.close();
            connection.close();
            return null;
        });
    }

    public void creteCompany(String name) {
        exceptionHandler(() -> {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL + dbFileName);
            connection.setAutoCommit(true);

            statement = connection.createStatement();
            String sql =  "INSERT INTO COMPANY (NAME) VALUES ('" + name + "')";
            statement.executeUpdate(sql);

            statement.close();
            connection.close();
            return null;
        });
    }

    public ArrayList<String> listCompanies() {
        ArrayList<String> companies = new ArrayList<String>();
        exceptionHandler(() -> {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL + dbFileName);
            connection.setAutoCommit(true);

            statement = connection.createStatement();
            String sql =  "SELECT * FROM COMPANY";
            var result = statement.executeQuery(sql);

            while(result.next()) {
                companies.add(result.getString("name"));
            }

            statement.close();
            connection.close();
            return null;
        });
        return companies;
    }

    public void addCarToCompany(String companyName, String carName) {
        exceptionHandler(() -> {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL + dbFileName);
            connection.setAutoCommit(true);

            statement = connection.createStatement();
            String getId =  "SELECT ID FROM COMPANY WHERE NAME='" + companyName + "'";
            var idResult = statement.executeQuery(getId);
            Integer companyId = null;
            while (idResult.next()) {
                companyId = idResult.getInt("id");
            }
            statement.close();

            if (companyId != null) {
                statement = connection.createStatement();
                String addCar = "INSERT INTO CAR (COMPANY_ID, NAME) VALUES ('" + companyId + "','" + carName + "')";
                statement.executeUpdate(addCar);
            }
            connection.close();
            return null;
        });
    }

    public ArrayList<String> listCompanyCars(String companyName) {
        ArrayList<String> cars = new ArrayList<String>();
        exceptionHandler(() -> {
            ResultSet carsList = null;
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL + dbFileName);
            connection.setAutoCommit(true);

            statement = connection.createStatement();
            String getId =  "SELECT ID FROM COMPANY WHERE NAME='" + companyName + "'";
            var idResult = statement.executeQuery(getId);
            Integer companyId = null;
            while (idResult.next()) {
                companyId = idResult.getInt("id");
            }
            statement.close();

            if (companyId != null) {
                statement = connection.createStatement();
                String listCars = "SELECT * FROM CAR WHERE COMPANY_ID='" + companyId + "'";
                carsList = statement.executeQuery(listCars);
            }
            if (carsList != null) {
                while (carsList.next()) {
                    cars.add(carsList.getString("name"));
                }
            }
            connection.close();
            return null;
        });
        return cars;
    }

    public void exceptionHandler(Callable<?> callback) {
        try {
            callback.call();
        } catch(SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch(Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try{
                if(statement!=null) statement.close();
            } catch(SQLException se2) {
            } // nothing we can do
            try {
                if(connection!=null) connection.close();
            } catch(SQLException se){
                se.printStackTrace();
            } //end finally try
        } //end try
    }
}
