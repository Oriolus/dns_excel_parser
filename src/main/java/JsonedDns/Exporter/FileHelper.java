package JsonedDns.Exporter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class FileHelper {

    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private FileHelper() { }

    public static String getShopsFileName(LocalDate date) {
        return String.format("%s_dns_shops.json", FileHelper.formatter.format(date));
    }

    public static String getItemsFileName(LocalDate date) {
        return String.format("%s_dns_items.json", FileHelper.formatter.format(date));
    }

    public static String getCategoriesFilename(LocalDate date) {
        return String.format("%s_categories.json", FileHelper.formatter.format(date));
    }

    public static String getCitiesFilename(LocalDate date) {
        return String.format("%s_cities.json", FileHelper.formatter.format(date));
    }

    public static void createFile(Path filename) throws IOException {
        Files.createFile(filename);
    }

    public static void deleteFiles(String fromFolder, LocalDate byDate) throws IOException {
        Files.deleteIfExists(Paths.get(fromFolder, getShopsFileName(byDate)));
        Files.deleteIfExists(Paths.get(fromFolder, getItemsFileName(byDate)));
        Files.deleteIfExists(Paths.get(fromFolder, getCategoriesFilename(byDate)));
        Files.deleteIfExists(Paths.get(fromFolder, getCitiesFilename(byDate)));
    }

    public static List<Path> getDayFiles(String archFolder, LocalDate date) throws IOException {
        return Files.list(Paths.get(archFolder)).filter(
                i ->  {
                    try {
                        return FileName.split(i.getFileName().toString()).getDate().equals(date);
                    }
                    catch (ParseException e) {
                        return false;
                    }
                }
        ).collect(Collectors.toList());
    }
}
