package DnsPriceParser.service;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ExcelZipExtractor implements ExcelExtractor {

    public ExcelZipExtractor() {
    }

    public Workbook getExcel(String fileLocation) throws FileNotFoundException, IOException {

        FileInputStream inputArchive = null;

        try
        {
            inputArchive = new FileInputStream(fileLocation);
            ZipInputStream zis = new ZipInputStream(inputArchive);
            ZipEntry entry = zis.getNextEntry();

            Workbook excelWorkbook = null;

            if (entry != null) {
                byte[] excelBytes = new byte[(int)entry.getSize()];
                int bytesRead = 0, bytesReadAll = 0;

                excelBytes = IOUtils.toByteArray(zis);
                InputStream excelStream = new ByteArrayInputStream(excelBytes);
                excelWorkbook = new HSSFWorkbook(excelStream);
            }

            zis.closeEntry();
            zis.close();

            return excelWorkbook;
        }
        finally {

            if (inputArchive != null) {
                inputArchive.close();
            }

        }
    }

}
