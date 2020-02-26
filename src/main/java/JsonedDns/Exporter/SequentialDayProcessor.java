package JsonedDns.Exporter;

import DnsPriceParser.data.Prices;
import DnsPriceParser.data.Tree;
import DnsPriceParser.service.ExcelZipExtractor;
import DnsPriceParser.service.FileParser;
import DnsPriceParser.service.WorkbookParser;
import JsonedDns.Exporter.JData.JCategory;
import JsonedDns.Exporter.JData.JConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class SequentialDayProcessor implements DayProcessor{

    private static final Logger logger = LogManager.getLogger(SequentialDayProcessor.class);

    @Override
    public void process(String archFolder, String dstFolder, LocalDate date, boolean deleteExisting)
            throws IOException, ParseException {
        logger.info(String.format("Processing date: %s", date));
        List<Path> files = FileHelper.getDayFiles(archFolder, date);

        if (deleteExisting) {
            FileHelper.deleteFiles(dstFolder, date);
        }

        Tree categoryTree = new Tree();
        HashSet<String> cities = new HashSet<>();
        AppendableFileExporter fileExporter = new AppendableFileExporter();

        for (Path path : files) {
            logger.debug(String.format("Processing %s", path));
            boolean hasFilesError = false;
            Prices prices = null;

            try {
                prices = FileParser.getInstance().parse(path);

                cities.add(prices.getCity());
                prices.getItems().forEach(item -> {
                    categoryTree.addCategories(item.getCategory());
                });
            }
            catch (Exception e) {
                hasFilesError = true;
                logger.catching(e);
                logger.error(String.format("Error processing %s file", path.toString()), e);
            }

            if (!hasFilesError && prices != null) {
                fileExporter.exportShops(prices.getShops(), prices.getOfDate(), dstFolder);
                fileExporter.exportItems(prices.getItems(), prices.getCity(), prices.getOfDate(), dstFolder);
            }
            logger.debug(String.format("Processed %s", path));
        }

        List<JCategory> categories = JConverter.toJCategory(categoryTree);
        fileExporter.exportCategory(categories, date, dstFolder);
        fileExporter.exportCities(new ArrayList<>(cities), date, dstFolder);

        logger.info(String.format("%s files was processed for %s date", files.size(), date));
    }


}
