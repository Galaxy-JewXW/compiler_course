package optimize;

import middle.Module;

public class Optimizer {
    private final Module module;

    public Optimizer(Module module) {
        this.module = module;
    }

    public void optimize() {
        UnusedBasicBlock.build(module);
        Mem2Reg.build(module);
        SlotTracker.build(module);
    }
}