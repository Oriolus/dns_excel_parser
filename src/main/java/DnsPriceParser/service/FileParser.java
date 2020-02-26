package DnsPriceParser.service;

import DnsPriceParser.data.Prices;
import JsonedDns.Exporter.FileName;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;

public class FileParser {

    public static FileParser getInstance() { return new FileParser(); }

    public Prices parse(Path path) throws ParseException, IOException {
        Prices prices = null;
        Workbook excel = null;

        FileName fn = FileName.split(path.getFileName().toString());
        ExcelZipExtractor extractor = new ExcelZipExtractor();
        WorkbookParser parser = new WorkbookParser();

        excel = extractor.getExcel(path.toString());
        prices = parser.parse(fn.getCity(), fn.getDate(), excel);

        return prices;
    }

}
