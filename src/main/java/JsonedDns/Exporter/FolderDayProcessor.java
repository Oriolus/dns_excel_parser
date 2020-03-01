package JsonedDns.Exporter;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;

public abstract class FolderDayProcessor implements DayProcessor {

    private String archFolder;
    private String dstFolder;

    protected FolderDayProcessor() {}

    public FolderDayProcessor(String archFolder, String dstFolder) {
        this.archFolder = archFolder;
        this.dstFolder = dstFolder;
    }

    public String getArchFolder() {
        return archFolder;
    }

    public void setArchFolder(String archFolder) {
        this.archFolder = archFolder;
    }

    public String getDstFolder() {
        return dstFolder;
    }

    public void setDstFolder(String dstFolder) {
        this.dstFolder = dstFolder;
    }

    @Override
    public abstract void process(LocalDate date, boolean deleteExisting) throws IOException, ParseException, BreakException;
}
