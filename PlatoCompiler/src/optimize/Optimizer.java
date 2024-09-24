package optimize;

import middle.component.Module;

public class Optimizer {
    public static void build(Module module) {
        UnusedBasicBlock.run(module);
        UnusedFunction.run(module);
        Mem2Reg.build(module);
        // 必须是最后一条
        module.updateId();
    }
}
