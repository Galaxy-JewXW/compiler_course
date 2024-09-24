package optimize;

import middle.component.Module;

public class Optimizer {
    public static void build(Module module) {
        UnusedBasicBlock.run(module);
        module.updateId();
    }
}
