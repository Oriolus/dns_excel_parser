package Exporter.JsonExporter;

import DnsPriceParser.data.Prices;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class AppendableFileExporter {

    private static int WriteBufferSize = 1000000;

    public AppendableFileExporter() { }

    private String getShopsFileName(Date date) {
        return String.format("dns_shops_%s.json", date);
    }

    private String getItemsFileName(Date date) {
        return String.format("dns_items_%s.json", date);
    }

    private void createFile(Path filename) throws IOException {
        Files.createFile(filename);
    }

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
        ItemWriter.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        ItemWriter.setDateFormat(new StdDateFormat().withColonInTimeZone(true));

        BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true), WriteBufferSize);

        for (JItem item : items) {
            writer.write(ItemWriter.writeValueAsString(item));
            writer.newLine();
        }

        writer.close();
    }


    public void export(Prices prices, String destinationFolder) throws IOException {

        Path shopsFilename = Paths.get(destinationFolder, getShopsFileName(Date.from(prices.getOfDate().toInstant())));

        if (Files.notExists(shopsFilename)) {
            createFile(shopsFilename);
        }

        List<JShop> shops = prices.getShops().stream().map(i -> JConverter.toJShop(i, prices.getOfDate())).collect(Collectors.toList());
        appendShops(shops, shopsFilename.toString());
        shops = null;

        Path itemsFileName = Paths.get(destinationFolder, getItemsFileName(Date.from(prices.getOfDate().toInstant())));
        if (Files.notExists(itemsFileName)) {
            createFile(itemsFileName);
        }

        List<JItem> items = prices.getItems().stream().map(i -> JConverter.toJItem(i, prices.getCity(), prices.getOfDate())).collect(Collectors.toList());
        appendItems(items, itemsFileName.toString());
        items = null;

    }

}
