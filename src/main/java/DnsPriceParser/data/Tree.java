package DnsPriceParser.data;

import java.util.HashMap;



public class Tree {

    private Node root;

    public Tree() {
        root = new Node("all", null);
        root.setChildren(new HashMap<>());
    }

    public void addCategories(String category) {
        // input data example
        // "Бытовая техника / Красота и здоровье / Бритье и эпиляция / Аксессуары к бритвам"

        String[] categories = category.split("/");

        Node curNode = root;

        for (String _category : categories) {
            String c = _category.trim().toLowerCase();

            Node nextLevelNode = curNode.getChildren().getOrDefault(c, null);
            if (nextLevelNode == null) {
                Node newOne = new Node(c, curNode);
                curNode.getChildren().put(c, newOne);
                curNode = newOne;
            } else {
                curNode = nextLevelNode;
            }
        }
    }

    public Node getRoot() {
        return root;
    }
}
