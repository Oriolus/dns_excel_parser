import DnsPriceParser.data.Prices;
import DnsPriceParser.service.ExcelZipExtractor;
import DnsPriceParser.service.WorkbookParser;
import Exporter.JsonExporter.AppendableFileExporter;
import Exporter.JsonExporter.DayProcessor;
import Exporter.JsonExporter.FileHelper;
import helper.ArchiveHelper;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.Date;

public class Main {

    public static void main(String[] args) throws IOException, ParseException
    {
        String workFolder = "/home/oriolus/IdeaProjects/work/dns_extract";

        String dstFolder = "/home/oriolus/docker_data/dns_data";
        String archiveFolder = "/home/oriolus/PycharmProjects/dns_pricing_download/data/business";

        LocalDate fromDate = LocalDate.parse("2019-12-25");
        LocalDate toDate = LocalDate.parse("2020-01-08");

        for (LocalDate curDate = LocalDate.from(fromDate); curDate.isBefore(toDate); curDate = curDate.plusDays(1)) {
            new DayProcessor().process(
                    archiveFolder,
                    dstFolder,
                    Date.from(curDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                    true
            );
            System.out.println(String.format("%s %s processed", ZonedDateTime.now(), curDate.toString()));
        }

//        new DayProcessor().process(workFolder, workFolder, FileHelper.sdf.parse("2019-12-23"));

    }

}
