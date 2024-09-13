package error;

public class ErrorLog implements Comparable<ErrorLog> {
    private final ErrorType errorType;
    private final int line;

    public ErrorLog(ErrorType errorType, int line) {
        this.errorType = errorType;
        this.line = line;
    }

    @Override
    public String toString() {
        return line + " " + errorType.toString();
    }

    @Override
    public int compareTo(ErrorLog log) {
        return this.line - log.line;
    }
}
