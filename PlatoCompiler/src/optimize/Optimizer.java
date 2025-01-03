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
        UnusedLocalArray.run(module);
        for (int i = 0; i < 10; i++) {
            SurplusBlock.build(module);
            Mem2Reg.run(module, true);
            FunctionSideEffect.run(module);
            InlinedFunction.run(module);
            UnusedFunction.run(module);
            GlobalVarLocalize.build(module);
            LocalConstArrayToValue.run(module);
            GVN.run(module);
            // GCM.run(module);
            IcmpOptimize.run(module);
            BlockSimplify.run(module);
            PrintOptimize.run(module);
            PickGep.run(module);
        }
        DivideCall.run(module);
    }
}
