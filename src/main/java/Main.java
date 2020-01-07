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
import java.util.Date;

public class Main {

    public static void main(String[] args) throws IOException, ParseException
    {


        String workFolder = "/home/oriolus/IdeaProjects/work/dns_extract";
//        String fromFile = "sterlitamak_2020-01-01.zip";
        String fromFile = "spb_2019-12-25.zip";

        new DayProcessor().process(workFolder, workFolder, FileHelper.sdf.parse("2019-12-24"));


//        String fileLocation = Paths.get(workFolder, fromFile).toString();
//        String[] fileSplit = fileLocation.split("/");
//        String[] filenameSplit = fileSplit[fileSplit.length - 1].split("\\.");
//        String filename = filenameSplit[0];
//
//        ExcelZipExtractor extractor = new ExcelZipExtractor();
//        Workbook excel = extractor.getExcel(fileLocation);
//
//        WorkbookParser parser = new WorkbookParser();
//        Prices prices = parser.parse(ArchiveHelper.getCity(filename), ArchiveHelper.getDate(filename), excel);
//
//        new AppendableFileExporter().export(prices, workFolder);


    }

}
