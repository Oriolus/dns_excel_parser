package JsonedDns.Exporter;

public class BreakException extends Exception {

    public BreakException(String s) {
        super(s);
    }

    public BreakException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public BreakException(Throwable e) {
        super(e);
    }
}
