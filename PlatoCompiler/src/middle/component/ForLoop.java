package middle.component;

public class ForLoop {
    /* 举例
     * 对这样的程序片段：
     * int a = 0;
     * for(i = 0; i < 10; i = i + 1) {
     *  a = a + i;
     * }
     * int b = a;
     *
     * 'i = 0'与a = 0位于同一个block
     * i < 10单独位于一个conditionBlock
     * a = a + 1和i = i + 1位于loopBodyBlock
     * int b = a位于followBlock
     */
    private final BasicBlock conditionBlock;
    private final BasicBlock loopBodyBlock;
    private final BasicBlock followBlock;

    public ForLoop(BasicBlock conditionBlock, BasicBlock loopBodyBlock,
                   BasicBlock followBlock) {
        this.conditionBlock = conditionBlock;
        this.loopBodyBlock = loopBodyBlock;
        this.followBlock = followBlock;
    }

    public BasicBlock getConditionBlock() {
        return conditionBlock;
    }

    public BasicBlock getLoopBodyBlock() {
        return loopBodyBlock;
    }

    public BasicBlock getFollowBlock() {
        return followBlock;
    }
}
