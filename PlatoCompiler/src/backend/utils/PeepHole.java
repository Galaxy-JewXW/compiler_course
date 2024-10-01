package backend.utils;

import backend.MipsFile;
import backend.text.JumpAsm;
import backend.text.Label;
import backend.text.TextAssembly;

import java.util.ArrayList;

public class PeepHole {
    public static void run() {
        MipsFile.getInstance().setInsect(false);
        removeJump();
    }

    private static void removeJump() {
        ArrayList<TextAssembly> textAssemblies = new ArrayList<>(
                MipsFile.getInstance().getTextSegment());
        for (int i = 0; i < textAssemblies.size() - 1; i++) {
            TextAssembly textAssembly = textAssemblies.get(i);
            TextAssembly textAssembly1 = textAssemblies.get(i + 1);
            if (textAssembly instanceof JumpAsm jumpAsm
                    && textAssembly1 instanceof Label label
                    && jumpAsm.getTarget() != null
                    && jumpAsm.getTarget().equals(label.getLabel())) {
                MipsFile.getInstance().getTextSegment().remove(jumpAsm);
            }
        }
    }
}
