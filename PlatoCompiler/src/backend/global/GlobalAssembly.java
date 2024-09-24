package backend.global;

import backend.Assembly;
import backend.MipsFile;

public class GlobalAssembly extends Assembly {
    public GlobalAssembly() {
        MipsFile.getInstance().addToDataSegment(this);
    }
}
