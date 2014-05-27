package exceptions;

public class StatsException extends RuntimeException {

    public StatsException(String msg, Exception e) {
        super(msg, e);
    }
}
