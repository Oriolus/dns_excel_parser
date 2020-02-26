package JsonedDns.Reader;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CityReader {

    public List<String> getCities(String path) throws IOException {
        BufferedReader reader = Files.newBufferedReader(Paths.get(path));

        ObjectMapper mapper = new ObjectMapper();
        List<String> cities = mapper.readValue(reader, ArrayList.class);
        reader.close();

        return cities;
    }

}
