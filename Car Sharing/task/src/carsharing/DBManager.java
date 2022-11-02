package carsharing;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
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
    public void init() {
        exceptionHandler(() -> {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL + dbFileName);
            connection.setAutoCommit(true);

            statement = connection.createStatement();
            String sql =  "CREATE TABLE COMPANY " +
                    "(ID INTEGER NOT NULL AUTO_INCREMENT, " +
                    " NAME VARCHAR(255) NOT NULL UNIQUE, " +
                    " PRIMARY KEY ( id ))";
            statement.executeUpdate(sql);

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

    public ArrayList<String> getCompanies() {
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
