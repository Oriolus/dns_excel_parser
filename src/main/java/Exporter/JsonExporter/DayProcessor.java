package Exporter.JsonExporter;

import DnsPriceParser.data.Prices;
import DnsPriceParser.service.ExcelZipExtractor;
import DnsPriceParser.service.WorkbookParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DayProcessor {

    private static final Logger logger = LogManager.getLogger(DayProcessor.class);

    public void processFile(Path path, String dstFolder) throws ParseException, IOException {

        logger.debug(String.format("Processing %s", path));

        boolean hasFilesError = false;
        AppendableFileExporter fileExporter = new AppendableFileExporter();
        FileName fn = FileName.split(path.getFileName().toString());
        ExcelZipExtractor extractor = new ExcelZipExtractor();
        WorkbookParser parser = new WorkbookParser();

        Prices prices = null;
        Workbook excel = null;

        try {
            excel = extractor.getExcel(path.toString());
            prices = parser.parse(fn.getCity(), fn.getDate(), excel);
        }
        catch (Exception e) {
            hasFilesError = true;
            logger.catching(e);
            logger.error(String.format("Error processing %s file", path.toString()), e);
        }

        if (!hasFilesError && prices != null) {
            fileExporter.export(prices, dstFolder);
        }
        logger.debug(String.format("Processed %s", path));
    }

    public void process(String archFolder, String dstFolder, Date date) throws IOException, ParseException {
        logger.info(String.format("Processing date: %s", date));
        List<Path> files = Files.list(Paths.get(archFolder)).filter(
                i ->  {
                    try {
                        return FileName.split(i.getFileName().toString()).getDate().equals(date);
                    }
                    catch (ParseException e) {
                        return false;
                    }
                }
        ).collect(Collectors.toList());

        for (Path path : files) {
            processFile(path, dstFolder);
        }

        logger.info(String.format("%s files was processed for %s date", files.size(), date));
    }

    public void process(String archFolder, String dstFolder, Date date, boolean deleteExisting)
            throws IOException, ParseException {
        logger.info(String.format("Processing date: %s", date));
        List<Path> files = Files.list(Paths.get(archFolder)).filter(
                i ->  {
                    try {
                        return FileName.split(i.getFileName().toString()).getDate().equals(date);
                    }
                    catch (ParseException e) {
                        return false;
                    }
                }
        ).collect(Collectors.toList());

        if (deleteExisting) {
            FileHelper.deleteFiles(dstFolder, date);
        }

        for (Path path : files) {
            processFile(path, dstFolder);
        }

        logger.info(String.format("%s files was processed for %s date", files.size(), date));
    }


}
