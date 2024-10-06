package backend;

import backend.global.GlobalAssembly;
import backend.text.Label;
import backend.text.TextAssembly;

import java.util.ArrayList;

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
        StringBuilder sb = new StringBuilder(".data:\n");
        for (GlobalAssembly assembly : dataSegment) {
            sb.append(assembly).append("\n");
        }
        sb.append(".text:\n");
        for (int i = 0; i < textSegment.size(); i++) {
            TextAssembly assembly = textSegment.get(i);
            if (assembly instanceof Label) {
                sb.append(assembly).append("\n");
                continue;
            }
            sb.append("\t").append(assembly).append("\n");
            if (i + 1 < textSegment.size()
                    && textSegment.get(i + 1) instanceof Label) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
