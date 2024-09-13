package IR.values.instructions;

public enum Operator {
    //基本运算
    Add, Sub, Mul, SDiv, Mod,
    //逻辑运算,signed
    Eq, Ne, Sgt, Sge, Slt, Sle,
    //类型转换
    Zext,
    //函数调用
    Call,
    //内存操作
    Alloc, Store, Load, GEP, Phi,
    //跳转指令
    br, ret,
    PC, MOVE,
}
