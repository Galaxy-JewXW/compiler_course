package backend;

import backend.global.GlobalAssembly;
import backend.text.TextAssembly;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class MipsFile {
    private static final MipsFile INSTANCE = new MipsFile();
    private final ArrayList<GlobalAssembly> dataSegment = new ArrayList<>();
    private final ArrayList<TextAssembly> textSegment = new ArrayList<>();
    private boolean insect = true;

    private MipsFile() {
    }

    public static MipsFile getInstance() {
        return INSTANCE;
    }

    public void addToDataSegment(GlobalAssembly assembly) {
        if (isInsect()) {
            dataSegment.add(assembly);
        }
    }

    public void addToTextSegment(TextAssembly assembly) {
        if (isInsect()) {
            textSegment.add(assembly);
        }
    }

    public ArrayList<TextAssembly> getTextSegment() {
        return textSegment;
    }

    public boolean isInsect() {
        return insect;
    }

    public void setInsect(boolean insect) {
        this.insect = insect;
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
