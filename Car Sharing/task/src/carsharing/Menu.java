package carsharing;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

public class Menu extends MenuNode {
    public Menu(
            DBManager dbManager,
            Callable printCompanyList,
            Callable createCompany,
            Consumer<String> printCompanyMenu,
            Callable printCarsList,
            Callable addCarToCompany
    ) {
        this.addChild(new MenuNode(1, "Log in as a manager"));
        this.addChild(new MenuNode(0, "Exit"));

        this.getChildren().get(1).addChild(new MenuNode(1, "Company list", printCompanyList));
        this.getChildren().get(1).addChild(new MenuNode(2, "Create a company", createCompany));
        this.getChildren().get(1).addChild(new MenuNode(0, "Back"));

        var companyNames = dbManager.listCompanies();
        for (int i = 0; i < companyNames.size(); i++) {
            var companyName = companyNames.get(i);
            var companyMenuNode = new MenuNode(i + 1, companyName, () -> {
                printCompanyMenu.accept(companyName);
                return null;
            });
            this.getChildren().get(1).getChildren().get(1).addChild(companyMenuNode, this.getChildren().get(1));
            companyMenuNode.addChild(new MenuNode(1, "Car list", printCarsList));
            companyMenuNode.addChild(new MenuNode(2, "Create a car", addCarToCompany));
            companyMenuNode.addChild(new MenuNode(0, "Back"));
        }
        if (companyNames.size() > 0) {
            this.getChildren().get(1).getChildren().get(1).addChild(new MenuNode(0, "Back"));
        }
    }
}
