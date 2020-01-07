package Exporter.JsonExporter;

import java.util.Date;

public class JShop {

    private Date date;
    private String city;
    private String address;
    private String schedule;
    private String title;
    private String code;

    public JShop() { }

    public JShop(Date date, String city, String address, String schedule, String title, String code) {
        this.date = date;
        this.city = city;
        this.address = address;
        this.schedule = schedule;
        this.title = title;
        this.code = code;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

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
}
