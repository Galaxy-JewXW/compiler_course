package error;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public void printErrors(String path) throws IOException {
        System.out.println("Got error(s) in this case. " +
                "Check error.txt for more information.");
        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        Collections.sort(errors);
        for (Error error : errors) {
            System.out.println(error.showMessage());
            writer.write(error.toString());
            writer.newLine();
        }
        writer.close();
    }
}
