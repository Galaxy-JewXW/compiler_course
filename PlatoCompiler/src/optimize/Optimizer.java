package optimize;

import middle.component.Module;

public class Optimizer {
    public static void build(Module module) {
        UnusedBasicBlock.run(module);
        UnusedFunction.run(module);
        Mem2Reg.run(module);
        DeadCode.run(module);
        LVN.run(module);
        DeadCode.run(module);
        // 序号重命名必须是最后一条
        module.updateId();
    }
}
