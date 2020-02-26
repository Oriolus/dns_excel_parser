package JsonedDns.Exporter.JData;

import DnsPriceParser.data.Item;
import DnsPriceParser.data.Node;
import DnsPriceParser.data.Shop;
import DnsPriceParser.data.Tree;
import com.sun.istack.internal.NotNull;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class JConverter {

    private JConverter() { }

    public static JItem toJItem(@NotNull Item from, @NotNull String city, @NotNull LocalDate date) {
        return new JItem(
                date,
                city,
                from.getCategory(),
                from.getCode(),
                from.getTitle(),
                from.getPrice(),
                from.getBonus(),
                from.getShops()
        );
    }

    public static JShop toJShop(@NotNull Shop from, @NotNull LocalDate date) {
        return new JShop(
                date,
                from.getCity(),
                from.getAddress(),
                from.getSchedule(),
                from.getTitle(),
                from.getCode()
        );
    }

    private static String getCategoryHierarchy(Node currentNode) {
        if (currentNode == null) {
            return "";
        }
        return getCategoryHierarchy(currentNode.getParent()) + (currentNode.getParent() != null ? " / " : "") + currentNode.getData();
    }

    public static List<JCategory> toJCategory(Tree categoryTree) {
        List<JCategory> categories = new LinkedList<>();
        Queue<Node> nodeQueue = new LinkedBlockingQueue<>();

        nodeQueue.add(categoryTree.getRoot());

        while (!nodeQueue.isEmpty()) {
            Node currentNode = nodeQueue.remove();
            if (currentNode != null) {
                categories.add(new JCategory(currentNode.getData(), getCategoryHierarchy(currentNode.getParent())));

                if (currentNode.getChildren() != null && !currentNode.getChildren().isEmpty()) {
                    currentNode.getChildren().forEach((key, val) -> {
                        nodeQueue.add(val);
                    });
                }
            }
        }
        return categories;
    }

}
