package JsonedDns.Exporter;

import DnsPriceParser.data.Prices;
import DnsPriceParser.data.Tree;
import DnsPriceParser.service.FileParser;
import JsonedDns.Exporter.JData.JCategory;
import JsonedDns.Exporter.JData.JConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SequentialDayProcessor extends FolderDayProcessor {

    private static final Logger logger = LogManager.getLogger(SequentialDayProcessor.class);

    public SequentialDayProcessor(String archFolder, String dstFolder) {
        super(archFolder, dstFolder);
    }

    @Override
    public void process(LocalDate date, boolean deleteExisting)
            throws IOException, ParseException {
        logger.info(String.format("Processing date: %s", date));
        List<Path> files = FileHelper.getDayFiles(super.getArchFolder(), date);

        if (deleteExisting) {
            FileHelper.deleteFiles(super.getDstFolder(), date);
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
                fileExporter.exportShops(prices.getShops(), prices.getOfDate(), super.getDstFolder());
                fileExporter.exportItems(prices.getItems(), prices.getCity(), prices.getOfDate(), super.getDstFolder());
            }
            logger.debug(String.format("Processed %s", path));
        }

        List<JCategory> categories = JConverter.toJCategory(categoryTree);
        fileExporter.exportCategory(categories, date, super.getDstFolder());
        fileExporter.exportCities(new ArrayList<>(cities), date, super.getDstFolder());

        logger.info(String.format("%s files was processed for %s date", files.size(), date));
    }


}
