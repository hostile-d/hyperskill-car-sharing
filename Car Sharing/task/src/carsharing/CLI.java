package carsharing;

import java.util.*;
import java.util.concurrent.Callable;

public class CLI {
    final String DB_FILE_NAME_ARG = "-databaseFileName";
    final Integer BACK_OPTION = 0;
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
    public void startNavigation() {
        printCurrentMenu();
        handleMenuChange(scanner.nextInt());
    }
    public void setDbManager(DBManager dbManager) {
        this.dbManager = dbManager;
    }
    private void handleMenuChange(Integer selectedOption) {
        MenuNode nextNode;
        if (Objects.equals(selectedOption, BACK_OPTION)) {
            nextNode = currentMenuNode.getParent();
        } else {
            nextNode = currentMenuNode.children.get(selectedOption);
            if (Objects.equals(nextNode, null)) {
                System.out.println("Incorrect input");
                return;
            }
            try {
                nextNode.callAction();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        currentMenuNode = nextNode;
        startNavigation();
    }

    private void printCurrentMenu() {
        if (Objects.equals(currentMenuNode, null)) {
            return;
        }
        currentMenuNode.children.forEach((id, node) -> System.out.println(node));
    }

    private void createMenu() {
        menu.addChild(new MenuNode(1, "Log in as a manager", this::loginAsManager));
        menu.addChild(new MenuNode(0, "Exit", this::exit));

        menu.children.get(1).addChild(new MenuNode(1, "Company list", this::printCompanyList));
        menu.children.get(1).addChild(new MenuNode(2, "Create a company", this::createCompany));
        menu.children.get(1).addChild(new MenuNode(0, "Back", this::getBack));
    }
    public Callable loginAsManager() {
        return null;
    }
    public Callable exit() {
        return null;
    }
    public Callable getBack() {
        return null;
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
        return null;
    }
    public Callable createCompany() {
        System.out.println("Enter the company name:");
        dbManager.creteCompany(scanner.nextLine());
        return null;
    }
}
