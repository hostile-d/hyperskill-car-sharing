package carsharing;

import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.concurrent.Callable;

public class MenuNode {
    private Integer id;
    private String name;
    private MenuNode parent;
    private LinkedHashMap<Integer, MenuNode> children;
    private Callable<?> action = null;
    final private Integer BACK_OPTION = 0;

    public MenuNode(Integer id, String name, Callable<?> action) {
        this.id = id;
        this.name = name;
        this.children = new LinkedHashMap<Integer, MenuNode>();
        this.action = action;
    }
    public MenuNode(Integer id, String name) {
        this.id = id;
        this.name = name;
        this.children = new LinkedHashMap<Integer, MenuNode>();
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

    public LinkedHashMap<Integer, MenuNode> getChildren() {
        return children;
    }
    public MenuNode() {
        this.id = null;
        this.name = null;
        this.children = new LinkedHashMap<Integer, MenuNode>();
    }
    public MenuNode getNextNode(Integer next) {
        MenuNode nextNode;
        if (Objects.equals(next, BACK_OPTION)) {
            nextNode = this.getParent();
        } else {
            nextNode = this.children.get(next);
        }
        return nextNode;
    }
    public void addChild(MenuNode child) {
        children.put(child.id, child);
        child.setParent(this);
    }
    public void callAction() throws Exception {
        if (!Objects.equals(this.action, null)) {
            this.action.call();
        }
    }
    @Override
    public String toString() {
        return this.id + ". " + this.name;
    }
}