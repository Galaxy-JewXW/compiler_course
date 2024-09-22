package error;

import java.util.ArrayList;
import java.util.Collections;

/**
 * 异常处理类，运用单例模式
 * 对于错误的源程序完成语义分析后不进行中间代码生成。
 */
public class ErrorHandler {
    public static final ErrorHandler INSTANCE = new ErrorHandler();
    private final ArrayList<Error> errors = new ArrayList<>();

    private ErrorHandler() {
    }

    public static ErrorHandler getInstance() {
        return INSTANCE;
    }

    public void addError(Error error) {
        errors.add(error);
    }

    public ArrayList<Error> getErrors() {
        Collections.sort(errors);
        return errors;
    }
}
