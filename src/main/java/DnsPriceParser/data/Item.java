package DnsPriceParser.data;

import java.util.List;

public class Item {

    private String code;
    private String category;
    private String title;
    private int price;
    private int bonus;

    private List<String> shops;

    public Item(String code, String category, String title, int price, int bonus, List<String> shops) {
        this.code = code;
        this.category = category;
        this.title = title;
        this.price = price;
        this.bonus = bonus;
        this.shops = shops;
    }

    public Item(String code, String category, String title, int price, int bonus) {
        this.code = code;
        this.category = category;
        this.title = title;
        this.price = price;
        this.bonus = bonus;
    }

    public Item() { }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getBonus() {
        return bonus;
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }

    public List<String> getShops() {
        return shops;
    }

    public void setShops(List<String> shops) {
        this.shops = shops;
    }


}
