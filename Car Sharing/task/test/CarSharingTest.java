import org.hyperskill.hstest.dynamic.DynamicTest;
import org.hyperskill.hstest.exception.outcomes.WrongAnswer;
import org.hyperskill.hstest.stage.StageTest;
import org.hyperskill.hstest.testcase.CheckResult;
import org.hyperskill.hstest.testing.TestedProgram;
import org.junit.BeforeClass;

import java.io.File;

public class CarSharingTest extends StageTest<Void> {

    private static final String databaseFileName = "src/carsharing/db/carsharing.mv.db";
    private static DatabaseUtil db = new DatabaseUtil();

    @BeforeClass
    public static void deleteDatabaseFile() {
        File file = new File(databaseFileName);

        if (!file.exists()) {
            return;
        }

        if (!file.delete()) {
            throw new WrongAnswer("Can't delete database file before starting your program.\n" +
                "Make sure you close all the connections with the database file!");
        }
    }

    @DynamicTest(order = -1)
    public CheckResult test2_ifDatabaseExist() {

        TestedProgram program = new TestedProgram();
        program.start("-databaseFileName", "carsharing");
        program.execute("0");

        if (!program.isFinished()) {
            return CheckResult.wrong("After choosing 'Exit' item your program should stop.");
        }

        File file = new File(databaseFileName);

        if (!file.exists()) {
            return CheckResult.wrong("Can't find a database file. It should be named 'carsharing.mv.db'" +
                " and located in /carsharing/db/ folder.\n" +
                "The file should be created right after starting the program!");
        }

        return correct();
    }

    @DynamicTest
    public CheckResult test1_testMenu() {
        TestedProgram program = new TestedProgram();
        String output = program.start("-databaseFileName", "carsharing");

        if (!output.contains("1. Log in as a manager")) {
            return CheckResult.wrong("Start menu should contain \"1. Log in as a manager\"");
        }

        if (!output.contains("0. Exit")) {
            return CheckResult.wrong("Start menu should contain \"0. Exit\"");
        }

        output = program.execute("1");

        if (!output.contains("1. Company list")) {
            return CheckResult.wrong("After choosing 'Log in as a manager' item you should print menu that contains '1. Company list' item");
        }

        if (!output.contains("2. Create a company")) {
            return CheckResult.wrong("After choosing 'Log in as a manager' item you should print menu that contains '2. Create a company' item");
        }

        if (!output.contains("0. Back")) {
            return CheckResult.wrong("After choosing 'Log in as a manager' item you should print menu that contains '0. Back' item");
        }

        output = program.execute("0");

        if (!output.contains("1. Log in as a manager")) {
            return CheckResult.wrong("After choosing '0. Back' item you should print previous menu and it should contain \"1. Log in as a manager\"");
        }

        if (!output.contains("0. Exit")) {
            return CheckResult.wrong("After choosing '0. Back' item you should print previous menu and it should contain \"0. Exit\"");
        }

        return CheckResult.correct();
    }

    @DynamicTest
    public CheckResult test3_checkDatabaseConnection() {
        db.getConnection();
        return correct();
    }

    @DynamicTest
    public CheckResult test4_checkIfTableExists() {
        if (!db.ifTableExist("company")) {
            return wrong("Can't find table named 'company'");
        }
        if (!db.ifTableExist("car")) {
            return wrong("Can't find table named 'car'");
        }
        return correct();
    }

    @DynamicTest
    public CheckResult test5_checkTableColumns() {
        String[][] companyColumns = {{"ID", "INT"}, {"NAME", "VARCHAR"}};
        db.ifColumnsExist("company", companyColumns);
        db.checkCompanyColumnProperties();

        String[][] carColumns = {{"ID", "INT"}, {"NAME", "VARCHAR"}, {"COMPANY_ID", "INT"}};
        db.ifColumnsExist("car", carColumns);
        db.checkCarColumnProperties();
        return correct();
    }

    @DynamicTest
    public CheckResult test6_testAddCompany() {

        TestedProgram program = new TestedProgram();
        program.start("-databaseFileName", "carsharing");

        db.clearCarTable();
        db.clearCompanyTable();

        program.execute("1");
        String output = program.execute("1");

        if (!output.contains("The company list is empty")) {
            return wrong("If no company has been created you should print 'The company list is empty' when '1. Company list' item is chosen");
        }

        output = program.execute("2");

        if (!output.contains("Enter the company name")) {
            return wrong("After choosing '2. Create a company' item you should ask to enter a company name.\n" +
                "Your output should contain 'Enter the company name:'");
        }

        program.execute("Super company");
        output = program.execute("1");

        if (!output.contains("1. Super company")) {
            return wrong("In the company list expected one company.\n" +
                "Your output should contain '1. Super company'");
        }

        db.checkCompany("Super company");

        program.execute("0\n2\nAnother company");
        program.execute("2\nOne more company");

        db.checkCompany("Another company");
        db.checkCompany("One more company");

        output = program.execute("1");

        if (!output.contains("1. Super company")) {
            return wrong("In the company list expected one company.\n" +
                "Your output should contain '1. Super company'.\n" +
                "Companies should be sorted by 'ID' column");
        }

        if (!output.contains("2. Another company")) {
            return wrong("In the company list expected one company.\n" +
                "Your output should contain '2. Another company'.\n" +
                "Companies should be sorted by 'ID' column");
        }

        if (!output.contains("3. One more company")) {
            return wrong("In the company list expected one company.\n" +
                "Your output should contain '2. Another company'.\n" +
                "Companies should be sorted by 'ID' column");
        }

        if (!output.contains("0. Back")) {
            return wrong("There is no back option in the company list.\n" +
                "Your output should contain '0. Back'");
        }

        program.execute("2");

        return correct();
    }

    @DynamicTest
    public CheckResult test7_testAddCar() {

        TestedProgram program = new TestedProgram();
        program.start("-databaseFileName", "carsharing");
        String output;

        db.clearCarTable();
        db.clearCompanyTable();

        program.execute("1");
        program.execute("2");
        program.execute("Car To Go");
        program.execute("2");
        program.execute("Drive Now");

        db.checkCompany("Car To Go");
        db.checkCompany("Drive Now");

        output = program.execute("1");

        if (!output.contains("1. Car To Go")) {
            return wrong("In the company list expected one company.\n" +
                "Your output should contain '1. Car To Go'.\n" +
                "Companies should be sorted by 'ID' column");
        }

        if (!output.contains("2. Drive Now")) {
            return wrong("In the company list expected one company.\n" +
                "Your output should contain '2. Drive Now'\n" +
                "Companies should be sorted by 'ID' column");
        }

        if (!output.contains("0. Back")) {
            return wrong("There is no back option in the company list.\n" +
                "Your output should contain '0. Back'");
        }

        output = program.execute("1");

        if (!output.contains("1. Car list")) {
            return wrong("After choosing company you should print menu that contains '1. Car list' item");
        }

        if (!output.contains("2. Create a car")) {
            return wrong("After choosing company you should print menu that contains '2. Create a car' item");
        }

        if (!output.contains("0. Back")) {
            return wrong("After choosing company you should print menu that contains '0. Back' item");
        }

        output = program.execute("1");

        if (!output.contains("The car list is empty!")) {
            return wrong("If no cars were added to the company you should print 'The car list is empty!'");
        }

        output = program.execute("2");

        if (!output.contains("Enter the car name:")) {
            return wrong("After choosing 'Create a car' item you should ask to enter a car name. " +
                "Your output should contain 'Enter the car name:'");
        }

        program.execute("Hyundai Venue");
        db.checkCar("Car To Go", "Hyundai Venue");

        program.execute("2");
        program.execute("Maruti Suzuki Dzire");
        db.checkCar("Car To Go", "Maruti Suzuki Dzire");

        output = program.execute("1");

        if (!output.contains("1. Hyundai Venue")) {
            return wrong("In the car list expected 'Hyundai Venue' car.\n" +
                "Your output should contain '1. Hyundai Venue'\n" +
                "Cars should be sorted by 'ID' column");
        }

        if (!output.contains("2. Maruti Suzuki Dzire")) {
            return wrong("In the car list expected 'Maruti Suzuki Dzire' car.\n" +
                "Your output should contain '2. Maruti Suzuki Dzire'\n" +
                "Cars should be sorted by 'ID' column");
        }

        program.execute("0");

        program.execute("1");
        program.execute("2");

        output = program.execute("1");

        if (!output.contains("The car list is empty!")) {
            return wrong("If no cars were added to the company you should print 'The car list is empty!'");
        }

        program.execute("2");
        program.execute("Lamborghini Urraco");

        output = program.execute("1");

        if (!output.contains("1. Lamborghini Urraco")) {
            return wrong("In the car list expected 'Lamborghini Urraco' car.\n" +
                "Your output should contain '1. Lamborghini Urraco'");
        }

        if (output.contains("Hyundai Venue")) {
            return wrong("Your output contains 'Hyundai Venue'. This car is from another company");
        }

        if (output.contains("Maruti Suzuki Dzire")) {
            return wrong("Your output contains 'Maruti Suzuki Dzire'. This car is from another company");
        }

        db.checkCar("Drive Now", "Lamborghini Urraco");

        program.execute("0");
        program.execute("0");
        program.execute("0");

        return correct();
    }

    private CheckResult wrong(String message) {
        db.closeConnection();
        return CheckResult.wrong(message);
    }

    private CheckResult correct() {
        db.closeConnection();
        return CheckResult.correct();
    }
}
