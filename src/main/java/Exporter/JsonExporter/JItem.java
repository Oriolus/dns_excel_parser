package Exporter.JsonExporter;

import java.util.Date;
import java.util.List;

public class JItem {

    private Date date;
    private String city;

    private String category;
    private String code;
    private String title;

    private int price;
    private int bonus;
    private List<String> shops;

    public JItem() { }

    public JItem(Date date, String city, String category, String code, String title, int price, int bonus, List<String> shops) {
        this.date = date;
        this.city = city;
        this.category = category;
        this.code = code;
        this.title = title;
        this.price = price;
        this.bonus = bonus;
        this.shops = shops;
    }

    public JItem(Date date, String city) {
        this.date = date;
        this.city = city;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

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
