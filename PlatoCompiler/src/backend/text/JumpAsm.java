package backend.text;

import backend.enums.AsmOp;
import backend.enums.Register;

import java.util.ArrayList;

public class JumpAsm extends TextAssembly {
    private final AsmOp op;
    private final String target;
    private final Register rd;
    private ArrayList<MemAsm> loadWords = new ArrayList<>();
    private ArrayList<MemAsm> storeWords = new ArrayList<>();

    // j jal
    public JumpAsm(AsmOp op, String target) {
        this.op = op;
        this.target = target;
        this.rd = null;
    }

    // jr
    public JumpAsm(AsmOp op, Register rd) {
        this.op = op;
        this.rd = rd;
        this.target = null;
    }

    public AsmOp getOp() {
        return op;
    }

    public String getTarget() {
        return target;
    }

    public ArrayList<MemAsm> getLoadWords() {
        return loadWords;
    }

    public void setLoadWords(ArrayList<MemAsm> loadWords) {
        this.loadWords = new ArrayList<>(loadWords);
    }

    public ArrayList<MemAsm> getStoreWords() {
        return storeWords;
    }

    public void setStoreWords(ArrayList<MemAsm> storeWords) {
        this.storeWords = new ArrayList<>(storeWords);
    }

    @Override
    public String toString() {
        if (op == AsmOp.JR) {
            return op.name().toLowerCase() + " " + rd;
        } else {
            return op.name().toLowerCase() + " " + target;
        }
    }
}
