package pass;

import IR.IRModule;
import pass.Analysis.FuncAnalysis;

public class PassModule {
    private static final PassModule passModule = new PassModule();

    public static PassModule getInstance() {
        return passModule;
    }

    public void run(IRModule irModule) {
        new FuncAnalysis(irModule).run();
        new DelUnreachedBB(irModule).run();
        new Mem2Reg(irModule).run();
        new DelDeadCode(irModule).run();
        // new LVN(irModule).run();
        // new DelDeadCode(irModule).run();
        irModule.refreshName();
    }
}
