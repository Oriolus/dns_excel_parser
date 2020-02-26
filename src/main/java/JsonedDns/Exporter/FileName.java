package JsonedDns.Exporter;

import java.text.ParseException;
import java.time.LocalDate;

public class FileName {

    private String city;
    private LocalDate date;
    private String ext;

    public FileName() { }

    public FileName(String city, LocalDate date, String ext) {
        this.city = city;
        this.date = date;
        this.ext = ext;
    }

    public static FileName split(String filename) throws ParseException {
        String[] extSplit = filename.split("\\.");
        String[] citySplit = extSplit[0].split("_");

        return new FileName(
                citySplit[0],
                LocalDate.parse(citySplit[1], FileHelper.formatter),
                extSplit[1]
        );
    }

    public String union() {
        return String.format("%s_%s.%s", this.city, FileHelper.formatter.format(this.date), this.ext);
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }
}
