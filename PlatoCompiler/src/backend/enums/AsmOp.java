package backend.enums;

public enum AsmOp {
    // 计算型R型指令
    ADD,
    SUB,
    ADDU,
    SUBU,
    AND,
    OR,
    NOR,
    XOR,
    MUL,
    SLTU,
    SLLV,
    SRAV,
    SRLV,
    // I型指令
    ADDI,
    ADDIU,
    ANDI,
    ORI,
    XORI,
    SLTI,
    SLTIU,
    SLL,
    SRA,
    SRL,
    // 乘除法指令
    MULT,
    MADD,
    DIV,
    // 有条件跳转类指令
    BEQ,
    BNE,
    BGE,
    BLE,
    BGT,
    BLT,
    // 无条件跳转
    J,
    JAL,
    JR,
    // 比较类指令
    SLT,
    SLE,
    SGT,
    SGE,
    SEQ,
    SNE,
    // hi lo寄存器指令
    MFHI,
    MFLO,
    MTHI,
    MTLO,
    // 地址与立即数加载
    LA,
    LI,
    MOVE,
    // 内催指令
    LW,
    SW,
    LB,
    SB,
    SYSCALL
}
