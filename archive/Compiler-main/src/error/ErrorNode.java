package error;

public class ErrorNode implements Comparable<ErrorNode> {
    private ErrorType errorType;
    private int line;

    public ErrorNode(ErrorType errorType, int line) {
        this.errorType = errorType;
        this.line = line;
    }

    @Override
    public String toString() {
        return line + " " + errorType.toString();
    }

    @Override
    public int compareTo(ErrorNode o) {
        return this.line - o.line;
    }
}
