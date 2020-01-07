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
        return String.format("dns_shops_%s.json", FileHelper.sdf.format(date));
    }

    public static String getItemsFileName(Date date) {
        return String.format("dns_items_%s.json", FileHelper.sdf.format(date));
    }

    public static void createFile(Path filename) throws IOException {
        Files.createFile(filename);
    }

    public static void deleteFiles(String fromFolder, Date byDate) throws IOException {

        Path shopFile = Paths.get(fromFolder, getShopsFileName(byDate));
        if (Files.exists(shopFile)) {
            Files.delete(shopFile);
        }

        Path itemFile = Paths.get(fromFolder, getItemsFileName(byDate));
        if (Files.exists(itemFile)) {
            Files.delete(itemFile);
        }

    }


}
