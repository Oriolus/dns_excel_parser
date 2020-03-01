package JsonedDns.Exporter.MulticoreDayProcessor;

import DnsPriceParser.data.Prices;
import DnsPriceParser.data.Tree;
import JsonedDns.Exporter.*;
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
import java.util.concurrent.*;

public class MulticoreDayProcessor extends FolderDayProcessor {

    private static final Logger logger = LogManager.getLogger(MulticoreDayProcessor.class);

    private int threadCount;

    private MulticoreDayProcessor() { }

    public MulticoreDayProcessor(String archFolder, String dstFolder, int threadCount) {
        super(archFolder, dstFolder);
        this.threadCount = threadCount;
    }

    public static MulticoreDayProcessor getInstance(String archFolder, String dstFolder, int threadCount) {
        return new MulticoreDayProcessor(archFolder, dstFolder, threadCount);
    }

    public int getThreadCount() {
        return threadCount;
    }

    @Override
    public void process(LocalDate date, boolean deleteExisting)
            throws IOException, ParseException, BreakException {
        ExecutorService executorService = Executors.newFixedThreadPool(this.getThreadCount());
        logger.info(String.format("Processing date: %s", date));

        List<Path> files = FileHelper.getDayFiles(super.getArchFolder(), date);

        if (deleteExisting) {
            FileHelper.deleteFiles(super.getDstFolder(), date);
        }

        Tree categoryTree = new Tree();
        HashSet<String> cities = new HashSet<>();
        AppendableFileExporter fileExporter = new AppendableFileExporter();
        List<Callable<Prices>> parseOperations = new ArrayList<Callable<Prices>>(files.size());

        for (Path path : files) {
            parseOperations.add(CallableFileParser.getInstance(path));
        }

        try
        {
            List<Future<Prices>> parseResult = executorService.invokeAll(parseOperations);
            executorService.shutdown();

            for (Future<Prices> pricesResult : parseResult) {

                try {
                    Prices prices = pricesResult.get();
                    cities.add(prices.getCity());
                    prices.getItems().forEach(item -> {
                        categoryTree.addCategories(item.getCategory());
                    });

                    fileExporter.exportShops(prices.getShops(), prices.getOfDate(), super.getDstFolder());
                    fileExporter.exportItems(prices.getItems(), prices.getCity(), prices.getOfDate(), super.getDstFolder());
                } catch (ExecutionException e) {
                    logger.warn(e);
                }

            }

            fileExporter.exportCategory(JConverter.toJCategory(categoryTree), date, super.getDstFolder());
            fileExporter.exportCities(new ArrayList<>(cities), date, super.getDstFolder());

        } catch (InterruptedException e) {
            logger.error(String.format("Error processing %s day", date.toString()), e);
            throw new BreakException(e);
        }
        logger.info(String.format("Processed %s. File count %s", date.toString(), files.size()));
    }
}

