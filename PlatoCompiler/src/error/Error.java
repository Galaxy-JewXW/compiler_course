package error;

/**
 * 错误类
 * 记录了错误类型与行号
 */
public class Error implements Comparable<Error> {
    private final ErrorType errorType;
    private final int line;

    public Error(ErrorType errorType, int line) {
        this.errorType = errorType;
        this.line = line;
    }

    public int getLine() {
        return line;
    }

    @Override
    public String toString() {
        return line + " " + errorType.toString();
    }

    public String showMessage() {
        return errorType.name() + " at line " + line;
    }

    @Override
    public int compareTo(Error o) {
        return this.line - o.line;
    }
}
