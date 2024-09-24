package backend;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class MipsFile {
    private static final MipsFile INSTANCE = new MipsFile();

    private MipsFile() {
    }

    public static MipsFile getInstance() {
        return INSTANCE;
    }

    private final ArrayList<Assembly> dataSegment = new ArrayList<>();
    private final ArrayList<Assembly> textSegment = new ArrayList<>();

    public void addToDataSegment(Assembly assembly) {
        dataSegment.add(assembly);
    }

    public void addToTextSegment(Assembly assembly) {
        textSegment.add(assembly);
    }

    @Override
    public String toString() {
        return ".data\n" +
                dataSegment.stream().map(Object::toString)
                        .collect(Collectors.joining("\n")) +
                "\n\n\n.text\n" +
                textSegment.stream().map(Object::toString)
                        .collect(Collectors.joining("\n"));
    }
}
