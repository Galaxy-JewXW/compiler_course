package backend.text;

import backend.Assembly;
import backend.MipsFile;

public abstract class TextAssembly extends Assembly {
    public TextAssembly() {
        MipsFile.getInstance().addToTextSegment(this);
    }
}
