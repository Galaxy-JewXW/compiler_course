package pass;

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
        GlobalVarLocalize.build(module);
        ConstToValue.run(module);
        Mem2Reg.run(module, true);
        InlinedFunction.run(module);
        GVN.run(module);
        SurplusBlock.build(module);
        Mem2Reg.run(module, false);
        GVN.run(module);
        SurplusBlock.build(module);
        Mem2Reg.run(module, false);
        FunctionSideEffect.run(module);
        CodeRemoval.run(module);
        FixMD.run(module);
        OptimizePhi.run(module);
        BlockMerge.run(module);
        Mem2Reg.run(module, false);
        CodeRemoval.run(module);
        RegAlloc.run(module);
        RemovePhi.run(module);
    }
}
