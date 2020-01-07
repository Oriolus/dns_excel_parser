package Exporter.JsonExporter;

import DnsPriceParser.data.Prices;
import DnsPriceParser.service.ExcelZipExtractor;
import DnsPriceParser.service.WorkbookParser;
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

    public void process(String archFolder, String dstFolder, Date date) throws IOException, ParseException {


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

        AppendableFileExporter fileExporter = new AppendableFileExporter();

        for (Path path : files) {
            FileName fn = FileName.split(path.getFileName().toString());

            ExcelZipExtractor extractor = new ExcelZipExtractor();
            Workbook excel = extractor.getExcel(path.toString());

            WorkbookParser parser = new WorkbookParser();
            Prices prices = parser.parse(fn.getCity(), fn.getDate(), excel);

            fileExporter.export(prices, dstFolder);
        }


    }

}
