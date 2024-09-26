package optimize;

import middle.component.Module;
import tools.Printer;

import java.io.FileNotFoundException;

public class Optimizer {
    public static void build(Module module) throws FileNotFoundException {
        UnusedBasicBlock.run(module);
        UnusedFunction.run(module);
        Mem2Reg.run(module);
        DeadCode.run(module);
        InlinedFunction.run(module);
        DeadCode.run(module);
        module.updateId();
        Printer.printIr(module, "ir_phi.txt");
        GVN.run(module);
        DeadCode.run(module);
        CertainBranch.run(module);
        OptimizePhi.run(module);
        InlinedFunction.run(module);
        UnusedBasicBlock.run(module);
        UnusedFunction.run(module);
        DeadCode.run(module);
        // 序号重命名必须是最后一条
        module.updateId();
    }
}
