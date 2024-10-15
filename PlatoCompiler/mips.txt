.data:
s0: .asciiz " "
s1: .asciiz "\n"
symbol: .word 0:7
array: .word 0:7
n: .word 0

.text:
func_main:
main_b0:
    # %1 = call i32 @getint()
    li $v0, 5
    syscall
    move $t0, $v0
    # store i32 %1, i32* @n
    la $k0, n
    sw $t0, 0($k0)
    # call void @FullArray(i32 0)
    sw $ra, -4($sp)
    li $a1, 0
    addiu $sp, $sp, -4
    jal func_FullArray
    lw $ra, 0($sp)
    addiu $sp, $sp, 4
    # ret i32 0
    li $v0, 10
    syscall

func_FullArray:
FullArray_b1:
    # %2 = load i32, i32* @n
    la $k0, n
    lw $t0, 0($k0)
    # %3 = icmp sge i32 %0, %2
    # br i1 %3, label %4, label %6
    blt $a1, $t0, FullArray_b6

FullArray_b4:
    # move 0 -> %7
    li $t1, 0
    # br label %8
    j FullArray_b8

FullArray_b6:
    # move 0 -> %17
    li $t3, 0
    # br label %18
    j FullArray_b18

FullArray_b8:
    # %9 = load i32, i32* @n
    la $k0, n
    lw $t0, 0($k0)
    # %10 = icmp slt i32 %7, %9
    # br i1 %10, label %11, label %17
    bge $t1, $t0, FullArray_b17

FullArray_b11:
    # %12 = getelementptr inbounds [7 x i32], [7 x i32]* @array, i32 0, i32 %7
    la $k0, array
    sll $k1, $t1, 2
    addu $t0, $k0, $k1
    # %13 = load i32, i32* %12
    lw $t0, 0($t0)
    # call void @putint(i32 %13)
    move $a0, $t0
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.0, i64 0, i64 0))
    la $a0, s0
    li $v0, 4
    syscall
    # br label %14

FullArray_b14:
    # %15 = add i32 %7, 1
    addiu $t1, $t1, 1
    # move %15 -> %7
    # br label %8
    j FullArray_b8

FullArray_b17:
    # call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.1, i64 0, i64 0))
    la $a0, s1
    li $v0, 4
    syscall
    # ret void
    jr $ra

FullArray_b18:
    # %19 = load i32, i32* @n
    la $k0, n
    lw $t0, 0($k0)
    # %20 = icmp slt i32 %17, %19
    # br i1 %20, label %21, label %28
    bge $t3, $t0, FullArray_b28

FullArray_b21:
    # %22 = getelementptr inbounds [7 x i32], [7 x i32]* @symbol, i32 0, i32 %17
    la $k0, symbol
    sll $k1, $t3, 2
    addu $t1, $k0, $k1
    # %23 = load i32, i32* %22
    lw $t0, 0($t1)
    # %24 = icmp eq i32 %23, 0
    # br i1 %24, label %29, label %33
    beq $t0, 0, FullArray_b29
    j FullArray_b33

FullArray_b25:
    # %26 = add i32 %17, 1
    addiu $t1, $t3, 1
    # move %26 -> %17
    move $t3, $t1
    # br label %18
    j FullArray_b18

FullArray_b28:
    # ret void
    jr $ra

FullArray_b29:
    # %30 = getelementptr inbounds [7 x i32], [7 x i32]* @array, i32 0, i32 %0
    la $k0, array
    sll $k1, $a1, 2
    addu $t4, $k0, $k1
    # %31 = add i32 %17, 1
    addiu $t0, $t3, 1
    # store i32 %31, i32* %30
    sw $t0, 0($t4)
    # store i32 1, i32* %22
    li $k1, 1
    sw $k1, 0($t1)
    # %32 = add i32 %0, 1
    addiu $t0, $a1, 1
    # call void @FullArray(i32 %32)
    sw $t1, -8($sp)
    sw $t3, -12($sp)
    sw $t2, -16($sp)
    sw $a1, -20($sp)
    sw $ra, -24($sp)
    move $a1, $t0
    addiu $sp, $sp, -24
    jal func_FullArray
    lw $ra, 0($sp)
    addiu $sp, $sp, 24
    lw $t1, -8($sp)
    lw $t3, -12($sp)
    lw $t2, -16($sp)
    lw $a1, -20($sp)
    # store i32 0, i32* %22
    li $k1, 0
    sw $k1, 0($t1)
    # br label %33

FullArray_b33:
    # br label %25
    j FullArray_b25

