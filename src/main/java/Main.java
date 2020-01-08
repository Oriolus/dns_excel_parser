import DnsPriceParser.data.Prices;
import DnsPriceParser.data.Tree;
import DnsPriceParser.service.ExcelZipExtractor;
import DnsPriceParser.service.WorkbookParser;
import Exporter.JsonExporter.AppendableFileExporter;
import Exporter.JsonExporter.DayProcessor;
import Exporter.JsonExporter.FileHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import helper.ArchiveHelper;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) throws IOException, ParseException
    {
        String workFolder = "/home/oriolus/IdeaProjects/work/dns_extract";

        String dstFolder = "/home/oriolus/docker_data/dns_data";
        String archiveFolder = "/home/oriolus/PycharmProjects/dns_pricing_download/data/business";

        LocalDate fromDate = LocalDate.parse("2019-12-23");
        LocalDate toDate = LocalDate.parse("2020-01-09");
//        LocalDate toDate = LocalDate.parse("2019-12-24");

        for (LocalDate curr = LocalDate.from(fromDate); curr.isBefore(toDate); curr = curr.plusDays(1L)) {
            new DayProcessor().process(
                    archiveFolder,
                    dstFolder,
                    Date.from(curr.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                    true
            );
        }
    }

}
