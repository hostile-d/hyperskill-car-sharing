package carsharing.presentation;

import carsharing.persistance.DBManager;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.*;

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
                    "Incorrect input, type a number from 0 to %s to continue\n",
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
    public Callable addCompany() {
        System.out.println("Enter the company name:");
        String name = scanner.nextLine();
        dbManager.addCompany(name);
        System.out.println("The company was created!\n");
        createMenu();
        currentMenuNode = menu.getChildren().get(1);
        return null;
    }
    public Callable addCarToCompany() {
        System.out.println("Enter the car name:");
        String carName = scanner.nextLine();
        dbManager.addCarToCompany(currentMenuNode.getName(), carName);
        System.out.println("The car was added!\n");
        var menuBefore = currentMenuNode;
        createMenu();
        currentMenuNode = menuBefore;
        return null;
    }
    public Callable addCustomer() {
        System.out.println("Enter the customer name:");
        String name = scanner.nextLine();
        dbManager.addCustomer(name, Optional.empty());
        System.out.println("The customer was added!\n");
        createMenu();
        currentMenuNode = menu;
        return null;
    }

    public Callable printCarsList() {
        var cars = dbManager.listCompanyCars(currentMenuNode.getName());
        if (cars.isEmpty()) {
            System.out.println("The car list is empty!");
        } else {
            System.out.printf("'%s' cars:%n", currentMenuNode.getName());
            for (int i = 0; i < cars.size(); i++) {
                System.out.println((i + 1) + ". " + cars.get(i));
            }
        }
        System.out.println();
        return null;
    }

    public Callable printCompanyList() {
        System.out.println(dbManager.listCompanies().isEmpty() ? "The company list is empty!\n" : "Choose the company:");
        return null;
    }

    public Callable printCustomerList() {
        System.out.println(dbManager.listCustomers().isEmpty() ? "The customer list is empty!\n" : "Customer list:");
        return null;
    }

    public Consumer<String> printCompanyMenu(String companyName) {
        System.out.printf("'%s' company%n", companyName);
        return null;
    }

    public Callable printCustomerCar() {
        var carDetails = dbManager.getCustomerCar(currentMenuNode.getName());
        if (carDetails.isEmpty()) {
            System.out.println("You didn't rent a car!");
        } else {
            var car = carDetails.get(0);
            System.out.println("Your rented car:");
            System.out.println(car.getName());
            System.out.println("Company:");
            System.out.println(car.getCompanyName());
        }
        System.out.println();
        return null;
    }

    private void createMenu() {
        var menuRoot = new Menu(
                dbManager,
                this::printCompanyMenu,
                this::printCompanyList,
                this::printCarsList,
                this::printCustomerList,
                this::printCustomerCar,
                this::addCompany,
                this::addCarToCompany,
                this::addCustomer
        );
        menu = menuRoot;
        currentMenuNode = menuRoot;
    }
}
