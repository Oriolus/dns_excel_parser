package DnsPriceParser.service;

import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface ExcelExtractor {

    Workbook getExcel(String fileLocation) throws IOException, FileNotFoundException;

}
