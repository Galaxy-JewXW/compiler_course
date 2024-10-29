package optimize;

import middle.IRData;
import middle.component.Module;

public class Optimizer {
    private final Module module;

    public Optimizer(Module module) {
        this.module = module;
        IRData.setInsect(false);
    }

    public void optimize() {
        SurplusBlock.build(module);
        Mem2Reg.run(module, true);
        InlinedFunction.run(module);
        UnusedFunction.run(module);
        GlobalVarLocalize.build(module);
        LocalConstArrayToValue.run(module);
        GVN.run(module);
        IcmpOptimize.run(module);
        GCM.run(module);
        BlockSimplify.run(module);
        PrintOptimize.run(module);
    }
}
