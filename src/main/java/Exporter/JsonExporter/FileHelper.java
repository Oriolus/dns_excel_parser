package Exporter.JsonExporter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileHelper {

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private FileHelper() { }

    public static String getShopsFileName(Date date) {
        return String.format("%s_dns_shops.json", FileHelper.sdf.format(date));
    }

    public static String getItemsFileName(Date date) {
        return String.format("%s_dns_items.json", FileHelper.sdf.format(date));
    }

    public static String getCategoriesFilename(Date date) {
        return String.format("%s_categories.json", FileHelper.sdf.format(date));
    }

    public static String getCitiesFilename(Date date) {
        return String.format("%s_cities.json", FileHelper.sdf.format(date));
    }

    public static void createFile(Path filename) throws IOException {
        Files.createFile(filename);
    }

    public static void deleteFiles(String fromFolder, Date byDate) throws IOException {
        Files.deleteIfExists(Paths.get(fromFolder, getShopsFileName(byDate)));
        Files.deleteIfExists(Paths.get(fromFolder, getItemsFileName(byDate)));
        Files.deleteIfExists(Paths.get(fromFolder, getCategoriesFilename(byDate)));
        Files.deleteIfExists(Paths.get(fromFolder, getCitiesFilename(byDate)));
    }
}
