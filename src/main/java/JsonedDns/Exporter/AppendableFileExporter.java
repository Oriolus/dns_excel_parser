package JsonedDns.Exporter;

import DnsPriceParser.data.Item;
import DnsPriceParser.data.Shop;
import JsonedDns.Exporter.JData.JCategory;
import JsonedDns.Exporter.JData.JConverter;
import JsonedDns.Exporter.JData.JItem;
import JsonedDns.Exporter.JData.JShop;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class AppendableFileExporter {

    private static final int WriteBufferSize = 1000000;

    public AppendableFileExporter() { }

    private void appendShops(List<JShop> shops, String filename) throws IOException {
        ObjectMapper shopMapper = new ObjectMapper();
        shopMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        shopMapper.setDateFormat(new StdDateFormat().withColonInTimeZone(true));

        BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true), WriteBufferSize);

        for (JShop shop : shops) {
            writer.write(shopMapper.writeValueAsString(shop));
            writer.newLine();
        }

        writer.close();
    }

    private void appendItems(List<JItem> items, String filename) throws IOException {
        ObjectMapper ItemWriter = new ObjectMapper();
//        ItemWriter.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//        ItemWriter.setDateFormat(new StdDateFormat().withColonInTimeZone(true));
//        ItemWriter.setDateFormat(FileHelper.formatter)

        BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true), WriteBufferSize);

        for (JItem item : items) {
            writer.write(ItemWriter.writeValueAsString(item));
            writer.newLine();
        }

        writer.close();
    }

    public void exportShops(List<Shop> shops, LocalDate byDate, String destinationFolder) throws IOException {
        Path shopsFilename = Paths.get(destinationFolder, FileHelper.getShopsFileName(byDate));
        List<JShop> jShops = shops.stream().map(i -> JConverter.toJShop(i, byDate)).collect(Collectors.toList());
        appendShops(jShops, shopsFilename.toString());
    }

    public void exportItems(List<Item> items, String city, LocalDate byDate, String destinationFolder) throws IOException {
        Path itemsFileName = Paths.get(destinationFolder, FileHelper.getItemsFileName(byDate));
        List<JItem> jItems = items.stream().map(i -> JConverter.toJItem(i, city, byDate)).collect(Collectors.toList());
        appendItems(jItems, itemsFileName.toString());
    }

    public void exportCategory(List<JCategory> categories, LocalDate byDate, String destinationFolder) throws IOException {
        Path categoryPath = Paths.get(destinationFolder, FileHelper.getCategoriesFilename(byDate));
        BufferedWriter writer = Files.newBufferedWriter(categoryPath);
        writer.write(new ObjectMapper().writeValueAsString(categories));
        writer.close();
    }

    public void exportCities(List<String> cities, LocalDate byDate, String destinationFolder) throws IOException {
        Path categoryPath = Paths.get(destinationFolder, FileHelper.getCitiesFilename(byDate));
        BufferedWriter writer = Files.newBufferedWriter(categoryPath);
        writer.write(new ObjectMapper().writeValueAsString(cities));
        writer.close();
    }


}
