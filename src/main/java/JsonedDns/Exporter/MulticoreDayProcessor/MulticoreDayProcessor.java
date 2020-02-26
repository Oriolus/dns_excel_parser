package JsonedDns.Exporter.MulticoreDayProcessor;

import DnsPriceParser.data.Prices;
import DnsPriceParser.data.Tree;
import JsonedDns.Exporter.AppendableFileExporter;
import JsonedDns.Exporter.BreakException;
import JsonedDns.Exporter.DayProcessor;
import JsonedDns.Exporter.FileHelper;
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

public class MulticoreDayProcessor implements DayProcessor {

    private static final Logger logger = LogManager.getLogger(MulticoreDayProcessor.class);

    private int threadCount;

    private MulticoreDayProcessor() {
    }

    public MulticoreDayProcessor(int threadCount) {
        this.threadCount = threadCount;
    }

    public static MulticoreDayProcessor getInstance(int threadCount) {
        return new MulticoreDayProcessor(threadCount);
    }

    public int getThreadCount() {
        return threadCount;
    }

    @Override
    public void process(String archFolder, String dstFolder, LocalDate date, boolean deleteExisting)
            throws IOException, ParseException, BreakException {
        ExecutorService executorService = Executors.newFixedThreadPool(this.getThreadCount());
        logger.info(String.format("Processing date: %s", date));

        List<Path> files = FileHelper.getDayFiles(archFolder, date);

        if (deleteExisting) {
            FileHelper.deleteFiles(dstFolder, date);
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

                    fileExporter.exportShops(prices.getShops(), prices.getOfDate(), dstFolder);
                    fileExporter.exportItems(prices.getItems(), prices.getCity(), prices.getOfDate(), dstFolder);
                } catch (ExecutionException e) {
                    logger.warn(e);
                }

            }

            fileExporter.exportCategory(JConverter.toJCategory(categoryTree), date, dstFolder);
            fileExporter.exportCities(new ArrayList<>(cities), date, dstFolder);

        } catch (InterruptedException e) {
            logger.error(String.format("Error processing %s day", date.toString()), e);
            throw new BreakException(e);
        }
        logger.info(String.format("Processed %s. File count %s", date.toString(), files.size()));
    }
}

