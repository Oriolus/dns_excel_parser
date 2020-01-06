import DnsPriceParser.data.Prices;
import DnsPriceParser.service.ExcelZipExtractor;
import DnsPriceParser.service.WorkbookParser;
import helper.ArchiveHelper;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.OptionalInt;
import java.util.function.IntBinaryOperator;

public class Main {

    public static void main(String[] args) throws IOException
    {

//        String fileLocation = "/home/oriolus/IdeaProjects/work/dns_extract/sterlitamak_2020-01-01.zip";
        String fileLocation = "/home/oriolus/IdeaProjects/work/dns_extract/spb_2019-12-25.zip";
        String[] fileSplit = fileLocation.split("/");
        String[] filenameSplit = fileSplit[fileSplit.length - 1].split("\\.");
        String filename = filenameSplit[0];

        ExcelZipExtractor extractor = new ExcelZipExtractor();
        Workbook excel = extractor.getExcel(fileLocation);

        WorkbookParser parser = new WorkbookParser();
        Prices prices = parser.parse(ArchiveHelper.getCity(filename), ArchiveHelper.getDate(filename), excel);

        OptionalInt facts = prices.getItems().stream().mapToInt(item -> item.getShops().size()).reduce(new IntBinaryOperator() {
            @Override
            public int applyAsInt(int i, int i1) {
                return i + i1;
            }
        });


        System.out.println(prices.getItems().size());
        System.out.println(facts.getAsInt());

    }

}
