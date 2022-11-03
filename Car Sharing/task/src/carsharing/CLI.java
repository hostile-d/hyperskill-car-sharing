package carsharing;

import java.util.*;
import java.util.concurrent.Callable;

public class CLI {
    final String DB_FILE_NAME_ARG = "-databaseFileName";
    private MenuNode menu = new MenuNode();
    private MenuNode currentMenuNode;
    private Scanner scanner = new Scanner(System.in);

    private DBManager dbManager;
    public CLI() {
        createMenu();
        currentMenuNode = menu;
    }
    public String getParameter(String[] args) {
        var dbFileName = "default";
        for (int i = 0; i < args.length; i++) {
            if (Objects.equals(args[i], DB_FILE_NAME_ARG) && args[i + 1] != null) {
                dbFileName = args[i + 1];
            }
        }
        return  dbFileName;
    }
    public void setDbManager(DBManager dbManager) {
        this.dbManager = dbManager;
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
    private void createMenu() {
        menu.addChild(new MenuNode(1, "Log in as a manager"));
        menu.addChild(new MenuNode(0, "Exit"));

        menu.getChildren().get(1).addChild(new MenuNode(1, "Company list", this::printCompanyList));
        menu.getChildren().get(1).addChild(new MenuNode(2, "Create a company", this::createCompany));
        menu.getChildren().get(1).addChild(new MenuNode(0, "Back"));
    }
    public Callable printCompanyList() {
        var companies = dbManager.getCompanies();
        if (companies.isEmpty()) {
            System.out.println("The company list is empty!");
        } else {
            for (int i = 0; i < companies.size(); i++) {
                System.out.println((i + 1) + ". " + companies.get(i));
            }
        }
        System.out.println();
        return null;
    }
    public Callable createCompany() {
        System.out.println("Enter the company name:");
        String name = scanner.nextLine();
        dbManager.creteCompany(name);
        System.out.println("The company was created!");
        System.out.println();
        return null;
    }
}
