package middle.component.instructions;

import middle.component.BasicBlock;
import middle.component.types.ValueType;

/* 终结指令类
 * 包括了返回指令ret和跳转指令br
 * 终结指令一定位于某个基本块的末尾
 * 相对应的，每个基本块的末尾一定是一条基本指令
 */
public class TerminatorInst extends Instruction {
    public TerminatorInst(ValueType valueType, OperatorType operatorType, BasicBlock basicBlock) {
        super(valueType, operatorType, basicBlock);
    }
}
