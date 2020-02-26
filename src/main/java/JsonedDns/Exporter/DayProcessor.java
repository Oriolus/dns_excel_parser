package JsonedDns.Exporter;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;

public interface DayProcessor {

    void process(String archFolder, String dstFolder, LocalDate date, boolean deleteExisting)
            throws IOException, ParseException, BreakException;

}
