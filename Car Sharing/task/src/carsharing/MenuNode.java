package carsharing;

import java.util.LinkedHashMap;
import java.util.concurrent.Callable;

public class MenuNode {
    Integer id;
    String name;
    MenuNode parent;
    LinkedHashMap<Integer, MenuNode> children;
    Callable<?> action;
    public MenuNode(Integer id, String name, Callable<?> action) {
        this.id = id;
        this.name = name;
        this.children = new LinkedHashMap<Integer, MenuNode>();
        this.action = action;
    }
    private void setParent(MenuNode node) {
        this.parent = node;
    }
    public Integer getId() {
        return id;
    }
    public MenuNode getParent() {
        return parent;
    }
    public MenuNode() {
        this.id = null;
        this.name = null;
        this.children = new LinkedHashMap<Integer, MenuNode>();
    }
    public void addChild(MenuNode child) {
        children.put(child.id, child);
        child.setParent(this);
    }
    public void callAction() throws Exception {
        this.action.call();
    }
    @Override
    public String toString() {
        return this.id + ". " + this.name;
    }
}