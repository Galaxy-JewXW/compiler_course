package optimize;

import middle.component.Module;

import java.io.FileNotFoundException;

public class Optimizer {
    public static void build(Module module) throws FileNotFoundException {
        UnusedBasicBlock.run(module);
        UnusedFunction.run(module);
        Mem2Reg.run(module);
        DeadCode.run(module);
        InlinedFunction.run(module);
        DeadCode.run(module);
//        GVN.run(module);
//        DeadCode.run(module);
//        DeadCode.run(module);
//        module.updateId();
//        Printer.printIr(module, "ir_phi.txt");
//        CertainBranch.run(module);
//        OptimizePhi.run(module);
        // 序号重命名必须是最后一条
        module.updateId();
    }
}
