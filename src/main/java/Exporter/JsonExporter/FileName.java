package Exporter.JsonExporter;

import java.io.File;
import java.text.ParseException;
import java.util.Date;

public class FileName {

    private String city;
    private Date date;
    private String ext;

    public FileName() { }

    public FileName(String city, Date date, String ext) {
        this.city = city;
        this.date = date;
        this.ext = ext;
    }

    public static FileName split(String filename) throws ParseException {
        String[] extSplit = filename.split("\\.");
        String[] citySplit = extSplit[0].split("_");

        return new FileName(
                citySplit[0],
                FileHelper.sdf.parse(citySplit[1]),
                extSplit[1]
        );
    }

    public String union() {
        return String.format("%s_%s.%s", this.city, FileHelper.sdf.format(this.date), this.ext);
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }
}
