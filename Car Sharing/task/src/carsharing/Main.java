package carsharing;

public class Main {
    public static void main(String[] args) {
        var dbManager = new DBManager(CLI.getParameter(args));
        dbManager.dropOldDB();
        dbManager.createDB();
        var cli = new CLI(dbManager);
        cli.startNavigation();
    }
}