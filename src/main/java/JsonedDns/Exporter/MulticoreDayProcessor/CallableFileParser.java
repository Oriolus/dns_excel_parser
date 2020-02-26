package JsonedDns.Exporter.MulticoreDayProcessor;

import DnsPriceParser.data.Prices;
import DnsPriceParser.service.FileParser;

import java.nio.file.Path;
import java.util.concurrent.Callable;

public class CallableFileParser implements Callable<Prices> {

    private Path file;

    private CallableFileParser() {
    }

    public CallableFileParser(Path file) {
        this.file = file;
    }

    public static CallableFileParser getInstance(Path file) {
        return new CallableFileParser(file);
    }

    @Override
    public Prices call() throws Exception {
        return FileParser.getInstance().parse(this.getFile());
    }

    public Path getFile() {
        return file;
    }
}
