package error;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

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
