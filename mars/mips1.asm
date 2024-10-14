.data:
s0: .asciiz ", "
s1: .asciiz "\n"
a: .word 0, 1, 2, 3, 4, 5, 6, 7, 8, 9

.text:
func_main:
main_b0:
    # move 0 -> %2
    li $t0, 0
    # br label %1

main_b49:
    # move %33 -> %2
    # br label %1

main_b1:
    # %3 = getelementptr inbounds [10 x i32], [10 x i32]* @a, i32 0, i32 0
    la $k0, a
    move $t3, $k0
    # %4 = load i32, i32* %3
    lw $t2, 0($t3)
    # %5 = add i32 %4, 36
    addiu $t2, $t2, 36
    # store i32 %5, i32* %3
    sw $t2, 0($t3)
    # %6 = getelementptr inbounds [10 x i32], [10 x i32]* @a, i32 0, i32 1
    addiu $t3, $k0, 4
    # %7 = load i32, i32* %6
    lw $t2, 0($t3)
    # %8 = add i32 %7, 36
    addiu $t2, $t2, 36
    # store i32 %8, i32* %6
    sw $t2, 0($t3)
    # %9 = getelementptr inbounds [10 x i32], [10 x i32]* @a, i32 0, i32 2
    addiu $t3, $k0, 8
    # %10 = load i32, i32* %9
    lw $t2, 0($t3)
    # %11 = add i32 %10, 36
    addiu $t2, $t2, 36
    # store i32 %11, i32* %9
    sw $t2, 0($t3)
    # %12 = getelementptr inbounds [10 x i32], [10 x i32]* @a, i32 0, i32 3
    addiu $t3, $k0, 12
    # %13 = load i32, i32* %12
    lw $t2, 0($t3)
    # %14 = add i32 %13, 36
    addiu $t2, $t2, 36
    # store i32 %14, i32* %12
    sw $t2, 0($t3)
    # %15 = getelementptr inbounds [10 x i32], [10 x i32]* @a, i32 0, i32 4
    addiu $t2, $k0, 16
    # %16 = load i32, i32* %15
    lw $t3, 0($t2)
    # %17 = add i32 %16, 36
    addiu $t3, $t3, 36
    # store i32 %17, i32* %15
    sw $t3, 0($t2)
    # %18 = getelementptr inbounds [10 x i32], [10 x i32]* @a, i32 0, i32 5
    addiu $t2, $k0, 20
    # %19 = load i32, i32* %18
    lw $t3, 0($t2)
    # %20 = add i32 %19, 36
    addiu $t3, $t3, 36
    # store i32 %20, i32* %18
    sw $t3, 0($t2)
    # %21 = getelementptr inbounds [10 x i32], [10 x i32]* @a, i32 0, i32 6
    addiu $t3, $k0, 24
    # %22 = load i32, i32* %21
    lw $t2, 0($t3)
    # %23 = add i32 %22, 36
    addiu $t2, $t2, 36
    # store i32 %23, i32* %21
    sw $t2, 0($t3)
    # %24 = getelementptr inbounds [10 x i32], [10 x i32]* @a, i32 0, i32 7
    addiu $t3, $k0, 28
    # %25 = load i32, i32* %24
    lw $t2, 0($t3)
    # %26 = add i32 %25, 36
    addiu $t2, $t2, 36
    # store i32 %26, i32* %24
    sw $t2, 0($t3)
    # %27 = getelementptr inbounds [10 x i32], [10 x i32]* @a, i32 0, i32 8
    addiu $t3, $k0, 32
    # %28 = load i32, i32* %27
    lw $t2, 0($t3)
    # %29 = add i32 %28, 36
    addiu $t2, $t2, 36
    # store i32 %29, i32* %27
    sw $t2, 0($t3)
    # %30 = getelementptr inbounds [10 x i32], [10 x i32]* @a, i32 0, i32 9
    addiu $t3, $k0, 36
    # %31 = load i32, i32* %30
    lw $t2, 0($t3)
    # %32 = add i32 %31, 36
    addiu $t2, $t2, 36
    # store i32 %32, i32* %30
    sw $t2, 0($t3)
    # %33 = add i32 %2, 1
    addiu $t0, $t0, 1
    # br label %34

main_b34:
    # br label %35

main_b35:
    # %36 = icmp sle i32 %33, 100
    # br i1 %36, label %49, label %37
    ble $t0, 100, main_b49

main_b37:
    # move 0 -> %39
    li $t1, 0
    # br label %38

main_b52:
    # move %42 -> %39
    # br label %38

main_b38:
    # %40 = getelementptr inbounds [10 x i32], [10 x i32]* @a, i32 0, i32 %39
    la $k0, a
    sll $k1, $t1, 2
    addu $t0, $k0, $k1
    # %41 = load i32, i32* %40
    lw $t0, 0($t0)
    # call void @putint(i32 %41)
    move $a0, $t0
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.s.0, i64 0, i64 0))
    la $a0, s0
    li $v0, 4
    syscall
    # %42 = add i32 %39, 1
    addiu $t1, $t1, 1
    # br label %43

main_b43:
    # br label %44

main_b44:
    # %45 = icmp slt i32 %42, 10
    # br i1 %45, label %52, label %46
    blt $t1, 10, main_b52

main_b46:
    # call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.1, i64 0, i64 0))
    la $a0, s1
    li $v0, 4
    syscall
    # call void @putint(i32 %42)
    move $a0, $t1
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.s.0, i64 0, i64 0))
    la $a0, s0
    li $v0, 4
    syscall
    # call void @putint(i32 -8894)
    li $a0, -8894
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.s.0, i64 0, i64 0))
    la $a0, s0
    li $v0, 4
    syscall
    # call void @putint(i32 -6)
    li $a0, -6
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.1, i64 0, i64 0))
    la $a0, s1
    li $v0, 4
    syscall
    # ret i32 0
    li $v0, 10
    syscall

