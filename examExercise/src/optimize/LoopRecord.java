package optimize;

import middle.component.BasicBlock;

import java.util.ArrayList;

public class LoopRecord {
    private int loopDepth;
    private LoopRecord parent;
    private ArrayList<BasicBlock> ends;
    private BasicBlock entry;

    public LoopRecord(BasicBlock entry, ArrayList<BasicBlock> ends) {
        this.entry = entry;
        this.ends = new ArrayList<>(ends);
        entry.setLoopRecord(this);
    }

    public int getLoopDepth() {
        return loopDepth;
    }

    public void setLoopDepth(int loopDepth) {
        this.loopDepth = loopDepth;
    }

    public BasicBlock getEntry() {
        return entry;
    }

    public LoopRecord getParent() {
        return parent;
    }

    public void setParent(LoopRecord parent) {
        this.parent = parent;
    }
}
