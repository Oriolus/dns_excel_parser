import DatabaseImport.SimpleEtl;
import JsonedDns.Exporter.MulticoreDayProcessor.MulticoreDayProcessor;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;

public class Main {

    public static void processFiles() throws IOException, ParseException, Throwable {
        String workFolder = "/home/oriolus/IdeaProjects/work/dns_extract";

        String dstFolder = "/home/oriolus/Downloads/dns_shop/data/json";
        String archiveFolder = "/home/oriolus/Downloads/dns_shop/data/business";

        LocalDate fromDate = LocalDate.parse("2019-12-23");
        LocalDate toDate = LocalDate.parse("2020-02-23");
//        LocalDate toDate = LocalDate.parse("2019-12-24");

        for (LocalDate curr = LocalDate.from(fromDate); curr.isBefore(toDate); curr = curr.plusDays(1L)) {
            MulticoreDayProcessor.getInstance(6).process(
                    archiveFolder,
                    dstFolder,
                    curr,
                    true
            );
        }
    }

    public static void importJsonToDb() throws IOException, SQLException {

        final String srcFolder = "/home/oriolus/Downloads/dns_shop/data/json";
        LocalDate ofDay = LocalDate.parse("2019-12-24");

        SimpleEtl etl = new SimpleEtl();
        etl.processDay(srcFolder, ofDay);


    }

    public static void main(String[] args) throws IOException, ParseException, SQLException, Throwable
    {
//        processFiles();

        importJsonToDb();

    }



}
