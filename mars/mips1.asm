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
    bge $a1, $t0, FullArray_b4
    j FullArray_b6

FullArray_b4:
    # move 0 -> %7
    li $t1, 0
    # br label %10
    j FullArray_b10

FullArray_b6:
    # move 0 -> %17
    li $t1, 0
    # br label %21
    j FullArray_b21

FullArray_b8:
    # move %14 -> %7
    move $t1, $t1
    # br label %10
    j FullArray_b10

FullArray_b10:
    # %11 = getelementptr inbounds [7 x i32], [7 x i32]* @array, i32 0, i32 %7
    la $k0, array
    sll $k1, $t1, 2
    addu $t0, $k0, $k1
    # %12 = load i32, i32* %11
    lw $t0, 0($t0)
    # call void @putint(i32 %12)
    move $a0, $t0
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.0, i64 0, i64 0))
    la $a0, s0
    li $v0, 4
    syscall
    # br label %13
    j FullArray_b13

FullArray_b13:
    # %14 = add i32 %7, 1
    addiu $t1, $t1, 1
    # br label %15
    j FullArray_b15

FullArray_b15:
    # %16 = load i32, i32* @n
    la $k0, n
    lw $t0, 0($k0)
    # %17 = icmp slt i32 %14, %16
    # br i1 %17, label %8, label %18
    blt $t1, $t0, FullArray_b8
    j FullArray_b18

FullArray_b18:
    # call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.1, i64 0, i64 0))
    la $a0, s1
    li $v0, 4
    syscall
    # ret void
    jr $ra

FullArray_b19:
    # move %26 -> %17
    move $t1, $t1
    # br label %21
    j FullArray_b21

FullArray_b21:
    # %22 = getelementptr inbounds [7 x i32], [7 x i32]* @symbol, i32 0, i32 %17
    la $k0, symbol
    sll $k1, $t1, 2
    addu $t0, $k0, $k1
    # %23 = load i32, i32* %22
    lw $t0, 0($t0)
    # %24 = icmp eq i32 %23, 0
    # br i1 %24, label %31, label %37
    beq $t0, 0, FullArray_b31
    j FullArray_b37

FullArray_b25:
    # %26 = add i32 %17, 1
    addiu $t1, $t1, 1
    # br label %27
    j FullArray_b27

FullArray_b27:
    # %28 = load i32, i32* @n
    la $k0, n
    lw $t0, 0($k0)
    # %29 = icmp slt i32 %26, %28
    # br i1 %29, label %19, label %30
    blt $t1, $t0, FullArray_b19
    j FullArray_b30

FullArray_b30:
    # ret void
    jr $ra

FullArray_b31:
    # %32 = getelementptr inbounds [7 x i32], [7 x i32]* @array, i32 0, i32 %0
    la $k0, array
    sll $k1, $a1, 2
    addu $t0, $k0, $k1
    # %33 = add i32 %17, 1
    addiu $t3, $t1, 1
    # store i32 %33, i32* %32
    sw $t3, 0($t0)
    # %34 = getelementptr inbounds [7 x i32], [7 x i32]* @symbol, i32 0, i32 %17
    la $k0, symbol
    sll $k1, $t1, 2
    addu $t0, $k0, $k1
    # store i32 1, i32* %34
    li $k1, 1
    sw $k1, 0($t0)
    # %35 = add i32 %0, 1
    addiu $t0, $a1, 1
    # call void @FullArray(i32 %35)
    sw $a1, -8($sp)
    sw $ra, -12($sp)
    move $a1, $t0
    addiu $sp, $sp, -12
    jal func_FullArray
    lw $ra, 0($sp)
    addiu $sp, $sp, 12
    lw $a1, -8($sp)
    # %36 = getelementptr inbounds [7 x i32], [7 x i32]* @symbol, i32 0, i32 %17
    la $k0, symbol
    sll $k1, $t1, 2
    addu $t0, $k0, $k1
    # store i32 0, i32* %36
    li $k1, 0
    sw $k1, 0($t0)
    # br label %37
    j FullArray_b37

FullArray_b37:
    # br label %25
    j FullArray_b25

