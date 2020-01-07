package Exporter.JsonExporter;

import DnsPriceParser.data.Prices;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    private static final int WriteBufferSize = 1000000;
    private static final Logger logger = LogManager.getLogger(AppendableFileExporter.class);

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

        logger.info(String.format("Start exporting %s, city: %s", FileHelper.sdf.format(prices.getOfDate()), prices.getCity()));

        Path shopsFilename = Paths.get(destinationFolder, FileHelper.getShopsFileName(Date.from(prices.getOfDate().toInstant())));

        if (Files.notExists(shopsFilename)) {
            logger.info(String.format("Day %s. Creating file %s",  FileHelper.sdf.format(prices.getOfDate()), shopsFilename.toString()));
            FileHelper.createFile(shopsFilename);
        }

        List<JShop> shops = prices.getShops().stream().map(i -> JConverter.toJShop(i, prices.getOfDate())).collect(Collectors.toList());
        appendShops(shops, shopsFilename.toString());
        shops = null;

        Path itemsFileName = Paths.get(destinationFolder, FileHelper.getItemsFileName(Date.from(prices.getOfDate().toInstant())));
        if (Files.notExists(itemsFileName)) {
            logger.info(String.format("Day %s. Creating file %s", FileHelper.sdf.format(prices.getOfDate()), shopsFilename.toString()));
            FileHelper.createFile(itemsFileName);
        }

        List<JItem> items = prices.getItems().stream().map(i -> JConverter.toJItem(i, prices.getCity(), prices.getOfDate())).collect(Collectors.toList());
        appendItems(items, itemsFileName.toString());
        items = null;

        logger.info(String.format("End exporting %s, city: %s", FileHelper.sdf.format(prices.getOfDate()), prices.getCity()));

    }

}
