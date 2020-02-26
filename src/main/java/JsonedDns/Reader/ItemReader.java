package JsonedDns.Reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ItemReader {

    public BufferedReader getReader(String path) throws IOException {
        return Files.newBufferedReader(Paths.get(path));
    }

}
