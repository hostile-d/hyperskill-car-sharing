package carsharing;

public class Main {
    public static void main(String[] args) {
        var cli = new CLI();
        var dbManager = new DBManager(cli.getParameter(args));
        dbManager.init();
        cli.setDbManager(dbManager);
        cli.startNavigation();
    }
}