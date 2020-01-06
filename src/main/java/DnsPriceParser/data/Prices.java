package DnsPriceParser.data;

import java.util.*;
import java.time.ZonedDateTime;

public class Prices {

    private String city;
    private ZonedDateTime ofDate;

    private List<Shop> shops;
    private List<Item> items;


    public Prices(String city, ZonedDateTime ofDate) {
        this.city = city;
        this.ofDate = ofDate;
    }

    public Prices(String city, ZonedDateTime ofDate, int shopCount) {
        this.city = city;
        this.ofDate = ofDate;
        this.shops = new ArrayList<Shop>(shopCount);
        this.items = new LinkedList<>();
    }

    public void extendShops(List<Shop> shops) {
        for (Shop shop : shops) {
            if (!this.shops.contains(shop)) {
                this.shops.add(shop);
            }
        }
    }

    public void setShops(List<Shop> shops) {
        this.shops = shops;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public String getCity() {
        return city;
    }

    public ZonedDateTime getOfDate() {
        return ofDate;
    }

    public List<Shop> getShops() {
        return shops;
    }

    public List<Item> getItems() {
        return items;
    }
}
