package carsharing.presentation;

import carsharing.persistance.DBManager;
import org.h2.command.dml.Call;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class Menu extends MenuNode {
    public Menu(
            DBManager dbManager,
            Consumer<String> printCompanyMenu,
            Callable printCompanyList,
            Callable printCarsList,
            Callable printCustomerList,
            Callable printCustomerCar,
            Callable addCompany,
            Callable addCarToCompany,
            Callable addCustomer,
            Callable returnCar,
            Callable rentCar
    ) {
        this.addChild(new MenuNode(1, "Log in as a manager"));
        this.addChild(new MenuNode(2, "Log in as a customer", printCustomerList));
        this.addChild(new MenuNode(3, "Create a customer", addCustomer));
        this.addChild(new MenuNode(0, "Exit"));

        this.getChildren().get(1).addChild(new MenuNode(1, "Company list", printCompanyList));
        this.getChildren().get(1).addChild(new MenuNode(2, "Create a company", addCompany));
        this.getChildren().get(1).addChild(new MenuNode(0, "Back"));

        var companyNames = dbManager.listCompanies();
        var companyParentNode = this.getChildren().get(1);
        for (int i = 0; i < companyNames.size(); i++) {
            var companyName = companyNames.get(i).getName();
            var companyMenuNode = new MenuNode(i + 1, companyName, () -> {
                printCompanyMenu.accept(companyName);
                return null;
            });
            companyParentNode.getChildren().get(1).addChild(companyMenuNode, companyParentNode);
            companyMenuNode.addChild(new MenuNode(1, "Car list", printCarsList));
            companyMenuNode.addChild(new MenuNode(2, "Create a car", addCarToCompany));
            companyMenuNode.addChild(new MenuNode(0, "Back"));
        }
        if (companyNames.size() > 0) {
            companyParentNode.getChildren().get(1).addChild(new MenuNode(0, "Back"));
        }

        var customerNames = dbManager.listCustomers();
        var customerParentNode = this.getChildren().get(2);
        for (int i = 0; i < customerNames.size(); i++) {
            var customerName = customerNames.get(i);
            var customerMenuNode = new MenuNode(i + 1, customerName);
            customerParentNode.addChild(customerMenuNode, this);
            customerMenuNode.addChild(new MenuNode(1, "Rent a car", rentCar));
            customerMenuNode.addChild(new MenuNode(2, "Return a rented car", returnCar));
            customerMenuNode.addChild(new MenuNode(3, "My rented car", printCustomerCar));
            customerMenuNode.addChild(new MenuNode(0, "Back"));
        }
        if (customerNames.size() > 0) {
            customerParentNode.addChild(new MenuNode(0, "Back"));
        }
    }
}
