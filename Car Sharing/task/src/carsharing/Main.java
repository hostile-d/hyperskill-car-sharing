package carsharing;

import carsharing.presentation.CLI;
import carsharing.persistance.DBManager;

public class Main {
    public static void main(String[] args) {
        var dbManager = new DBManager(CLI.getParameter(args));
        dbManager.createDB();
        var cli = new CLI(dbManager);
        cli.startNavigation();
    }
}