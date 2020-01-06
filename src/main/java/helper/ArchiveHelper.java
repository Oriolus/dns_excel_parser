package helper;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

public class ArchiveHelper {

    private ArchiveHelper() {
    }

    //    spb_2019-12-25

    private static void validateFileName(String filename) throws IllegalArgumentException {
        if (filename == null) {
            throw new IllegalArgumentException("File name is null");
        }
        if (filename.split("_").length != 2) {
            throw new IllegalArgumentException("Invalid format of file name");
        }
    }

    public static String getCity(String filename) throws IllegalArgumentException {
        validateFileName(filename);
        String[] mainSplit = filename.split("_");
        return mainSplit[0];
    }

    public static ZonedDateTime getDate(String filename) {
        validateFileName(filename);
        String[] mainSplit = filename.split("_");
        return ZonedDateTime.parse(String.format("%sT00:00:00Z", mainSplit[1]));
    }

}
