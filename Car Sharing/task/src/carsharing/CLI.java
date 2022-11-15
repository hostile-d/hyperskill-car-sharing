package carsharing;

import org.h2.command.dml.Call;

import java.util.*;
import java.util.concurrent.Callable;

public class CLI {
    final static String DB_FILE_NAME_ARG = "-databaseFileName";
    private MenuNode menu = new MenuNode();
    private MenuNode currentMenuNode;
    private final Scanner scanner = new Scanner(System.in);
    private final DBManager dbManager;
    public CLI(DBManager dbManager) {
        this.dbManager = dbManager;
        createMenu();
    }
    public static String getParameter(String[] args) {
        var dbFileName = "default";
        for (int i = 0; i < args.length; i++) {
            if (Objects.equals(args[i], DB_FILE_NAME_ARG) && args[i + 1] != null) {
                dbFileName = args[i + 1];
            }
        }
        return  dbFileName;
    }
    public void startNavigation() {
        printCurrentMenu();
        var nextChoise = scanner.nextLine();
        try {
            handleMenuChange(Integer.parseInt(nextChoise));
        } catch (NumberFormatException e) {
            System.out.printf(
                    "Incorrect input, type a number from 0 to %s to continue\n\n",
                    currentMenuNode.getChildren().size() - 1
            );
            startNavigation();
        }
    }
    private void handleMenuChange(Integer selectedOption) {
        var nextNode = currentMenuNode.getNextNode(selectedOption);

        if (Objects.equals(nextNode, null)) {
            scanner.close();
            return;
        }

        callAction(nextNode);

        if (!nextNode.getChildren().isEmpty()) {
            currentMenuNode = nextNode;
        }

        startNavigation();
    }
    private void callAction(MenuNode nextNode) {
        try {
            nextNode.callAction();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    private void printCurrentMenu() {
        if (Objects.equals(currentMenuNode, null)) {
            return;
        }
        currentMenuNode.getChildren().forEach((id, node) -> System.out.println(node));
    }
    public Callable createCompany() {
        System.out.println("Enter the company name:");
        String name = scanner.nextLine();
        dbManager.creteCompany(name);
        System.out.println("The company was created!");
        System.out.println();
        createMenu();
        currentMenuNode = menu.getChildren().get(1);
        return null;
    }
    public Callable addCarToCompany() {
        System.out.println("Enter the car name:");
        String carName = scanner.nextLine();
        dbManager.addCarToCompany(currentMenuNode.getName(), carName);
        System.out.println("The car was added!");
        System.out.println();
        var menuBefore = currentMenuNode;
        createMenu();
        currentMenuNode = menuBefore;
        return null;
    }
    public Callable printCarsList() {
        var cars = dbManager.listCompanyCars(currentMenuNode.getName());
        if (cars.isEmpty()) {
            System.out.println("The car list is empty!");
        } else {
            System.out.println(String.format("'%s' cars:", currentMenuNode.getName()));
            for (int i = 0; i < cars.size(); i++) {
                System.out.println((i + 1) + ". " + cars.get(i));
            }
        }
        System.out.println();
        return null;
    }
    public Callable printCompanyList() {
        System.out.println(dbManager.listCompanies().isEmpty() ? "The company list is empty!" : "Choose the company:");
        return null;
    }
    public Callable printCompanyMenu(String companyName) {
        System.out.println(String.format("'%s' company", companyName));
        return null;
    }
    private void createMenu() {
        menu = new MenuNode();
        menu.addChild(new MenuNode(1, "Log in as a manager"));
        menu.addChild(new MenuNode(0, "Exit"));

        menu.getChildren().get(1).addChild(new MenuNode(1, "Company list", this::printCompanyList));
        menu.getChildren().get(1).addChild(new MenuNode(2, "Create a company", this::createCompany));
        menu.getChildren().get(1).addChild(new MenuNode(0, "Back"));

        var companyNames = dbManager.listCompanies();
        for (int i = 0; i < companyNames.size(); i++) {
            var companyName = companyNames.get(i);
            var companyMenuNode = new MenuNode(i + 1, companyName, () -> printCompanyMenu(companyName));
            menu.getChildren().get(1).getChildren().get(1).addChild(companyMenuNode, menu.getChildren().get(1));
            companyMenuNode.addChild(new MenuNode(1, "Car list", this::printCarsList));
            companyMenuNode.addChild(new MenuNode(2, "Create a car", this::addCarToCompany));
            companyMenuNode.addChild(new MenuNode(0, "Back"));
        }
        if (companyNames.size() > 0) {
            menu.getChildren().get(1).getChildren().get(1).addChild(new MenuNode(0, "Back"));
        }
        currentMenuNode = menu;
    }
}
