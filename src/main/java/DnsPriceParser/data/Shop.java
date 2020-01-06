package DnsPriceParser.data;

import java.util.Objects;

public class Shop {

    private String city;
    private String code;
    private String title;
    private String schedule;
    private String address;

    public Shop(String city, String code, String title, String schedule, String address) {
        this.city = city;
        this.code = code;
        this.title = title;
        this.schedule = schedule;
        this.address = address;
    }

    public Shop(String city) {
        this.city = city;
    }

    public Shop() { }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shop shop = (Shop) o;
        return Objects.equals(city, shop.city) &&
                Objects.equals(title, shop.title) &&
                Objects.equals(address, shop.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(city, title, address);
    }
}
