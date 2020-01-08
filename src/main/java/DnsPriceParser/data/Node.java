package DnsPriceParser.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;

@JsonIgnoreProperties(value = { "parent" })
public class Node {
    private String data;
    private Node parent;
    private HashMap<String, Node> children;

    public Node() { }

    public Node(String data, Node parent) {
        this.data = data;
        this.parent = parent;
        this.children = new HashMap<>();
    }

    public Node(String data, Node parent, HashMap<String, Node> children) {
        this.data = data;
        this.parent = parent;
        this.children = children;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public HashMap<String, Node> getChildren() {
        return children;
    }

    public void setChildren(HashMap<String, Node> children) {
        this.children = children;
    }
}
