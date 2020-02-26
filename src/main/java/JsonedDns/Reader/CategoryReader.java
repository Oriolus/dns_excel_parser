package JsonedDns.Reader;

import JsonedDns.Exporter.JData.JCategory;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryReader {

    public List<JCategory> do_import(String path) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JCategory[] categories = mapper.readValue(Files.newBufferedReader(Paths.get(path)), JCategory[].class);
        return Arrays.stream(categories).collect(Collectors.toList());
    }

}
