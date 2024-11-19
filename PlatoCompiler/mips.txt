.data:
s0: .asciiz ", "
s1: .asciiz "\n"
s2: .asciiz ", -6\n"
a: .word 0, 1, 2, 3, 4, 5, 6, 7, 8, 9

.text:
func_main:
main_bb0:
    # %v1 = call i32 @getint()
    li $v0, 5
    syscall
    move $t0, $v0
    # %v2 = call i32 @getint()
    li $v0, 5
    syscall
    move $t2, $v0
    # %v3 = getelementptr inbounds [10 x i32], [10 x i32]* @a, i32 0, i32 1
    la $k0, a
    addiu $s0, $k0, 4
    # %v4 = load i32, i32* %v3
    lw $t1, 0($s0)
    # %v5 = mul i32 %v1, %v2
    mul $t0, $t0, $t2
    # %v6 = sub i32 0, %v5
    neg $t0, $t0
    # %v7 = call i32 @fib(i32 4)
    sw $t7, -4($sp)
    sw $t1, -8($sp)
    sw $s0, -12($sp)
    sw $t0, -16($sp)
    sw $t2, -20($sp)
    sw $ra, -24($sp)
    li $a1, 4
    addiu $sp, $sp, -24
    jal func_fib
    lw $ra, 0($sp)
    addiu $sp, $sp, 24
    lw $t7, -4($sp)
    lw $t1, -8($sp)
    lw $s0, -12($sp)
    lw $t0, -16($sp)
    lw $t2, -20($sp)
    move $t2, $v0
    # br label %b8

main_bb8:
    # %v9 = mul i32 %v6, %v7
    mul $t0, $t0, $t2
    # %v10 = add i32 %v9, %v4
    addu $t0, $t0, $t1
    # %v11 = sdiv i32 %v10, 5
    li $v0, 858993460
    mult $t0, $v0
    mfhi $v1
    sra $v0, $v1, 0
    srl $a0, $t0, 31
    addu $t0, $v0, $a0
    # %v12 = icmp slt i32 %v11, 10
    # %v13 = getelementptr inbounds [10 x i32], [10 x i32]* @a, i32 0, i32 0
    la $k0, a
    move $s4, $k0
    # %v14 = mul i32 %v11, %v11
    mul $t6, $t0, $t0
    # %v15 = getelementptr inbounds [10 x i32], [10 x i32]* @a, i32 0, i32 2
    addiu $t3, $k0, 8
    # %v16 = getelementptr inbounds [10 x i32], [10 x i32]* @a, i32 0, i32 3
    addiu $s3, $k0, 12
    # %v17 = getelementptr inbounds [10 x i32], [10 x i32]* @a, i32 0, i32 4
    addiu $s5, $k0, 16
    # %v18 = getelementptr inbounds [10 x i32], [10 x i32]* @a, i32 0, i32 5
    addiu $s2, $k0, 20
    # %v19 = getelementptr inbounds [10 x i32], [10 x i32]* @a, i32 0, i32 6
    addiu $t0, $k0, 24
    # %v20 = getelementptr inbounds [10 x i32], [10 x i32]* @a, i32 0, i32 7
    addiu $t1, $k0, 28
    # %v21 = getelementptr inbounds [10 x i32], [10 x i32]* @a, i32 0, i32 8
    addiu $t5, $k0, 32
    # %v22 = getelementptr inbounds [10 x i32], [10 x i32]* @a, i32 0, i32 9
    addiu $t2, $k0, 36
    # br label %b49
    j main_bb49

main_bb23:
    # %v24 = getelementptr inbounds [10 x i32], [10 x i32]* @a, i32 0, i32 %v51
    la $k0, a
    sll $k1, $t0, 2
    addu $t1, $k0, $k1
    # %v25 = load i32, i32* %v24
    lw $t1, 0($t1)
    # call void @putint(i32 %v25)
    move $a0, $t1
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.s.0, i64 0, i64 0))
    la $a0, s0
    li $v0, 4
    syscall
    # %v26 = add i32 %v51, 1
    addiu $t7, $t0, 1
    # move %v26 -> %v51
    move $t0, $t7
    # br label %b52
    j main_bb52

main_bb28:
    # %v29 = load i32, i32* %v13
    lw $s1, 0($s4)
    # %v30 = add i32 %v29, %v14
    addu $s1, $s1, $t6
    # store i32 %v30, i32* %v13
    sw $s1, 0($s4)
    # %v31 = load i32, i32* %v3
    lw $s1, 0($s0)
    # %v32 = add i32 %v31, %v14
    addu $s1, $s1, $t6
    # store i32 %v32, i32* %v3
    sw $s1, 0($s0)
    # %v33 = load i32, i32* %v15
    lw $s1, 0($t3)
    # %v34 = add i32 %v33, %v14
    addu $s1, $s1, $t6
    # store i32 %v34, i32* %v15
    sw $s1, 0($t3)
    # %v35 = load i32, i32* %v16
    lw $s1, 0($s3)
    # %v36 = add i32 %v35, %v14
    addu $s1, $s1, $t6
    # store i32 %v36, i32* %v16
    sw $s1, 0($s3)
    # %v37 = load i32, i32* %v17
    lw $s1, 0($s5)
    # %v38 = add i32 %v37, %v14
    addu $s1, $s1, $t6
    # store i32 %v38, i32* %v17
    sw $s1, 0($s5)
    # %v39 = load i32, i32* %v18
    lw $s1, 0($s2)
    # %v40 = add i32 %v39, %v14
    addu $s1, $s1, $t6
    # store i32 %v40, i32* %v18
    sw $s1, 0($s2)
    # %v41 = load i32, i32* %v19
    lw $s1, 0($t0)
    # %v42 = add i32 %v41, %v14
    addu $s1, $s1, $t6
    # store i32 %v42, i32* %v19
    sw $s1, 0($t0)
    # %v43 = load i32, i32* %v20
    lw $s1, 0($t1)
    # %v44 = add i32 %v43, %v14
    addu $s1, $s1, $t6
    # store i32 %v44, i32* %v20
    sw $s1, 0($t1)
    # %v45 = load i32, i32* %v21
    lw $s1, 0($t5)
    # %v46 = add i32 %v45, %v14
    addu $s1, $s1, $t6
    # store i32 %v46, i32* %v21
    sw $s1, 0($t5)
    # %v47 = load i32, i32* %v22
    lw $s1, 0($t2)
    # %v48 = add i32 %v47, %v14
    addu $s1, $s1, $t6
    # store i32 %v48, i32* %v22
    sw $s1, 0($t2)
    # br label %b49

main_bb49:
    # br i1 %v12, label %b28, label %b50
    blt $t0, 10, main_bb28

main_bb50:
    # move 0 -> %v51
    li $t0, 0
    # br label %b52

main_bb52:
    # %v53 = icmp slt i32 %v51, 10
    # br i1 %v53, label %b23, label %b54
    blt $t0, 10, main_bb23

main_bb54:
    # call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.1, i64 0, i64 0))
    li $v0, 11
    li $a0, 10
    syscall
    # call void @putint(i32 %v51)
    move $a0, $t0
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.s.0, i64 0, i64 0))
    la $a0, s0
    li $v0, 4
    syscall
    # %v55 = call i32 @fib(i32 5)
    sw $t0, -4($sp)
    sw $ra, -8($sp)
    li $a1, 5
    addiu $sp, $sp, -8
    jal func_fib
    lw $ra, 0($sp)
    addiu $sp, $sp, 8
    lw $t0, -4($sp)
    move $t0, $v0
    # br label %b56

main_bb56:
    # %v57 = add i32 %v55, 2
    addiu $t0, $t0, 2
    # %v58 = call i32 @fib(i32 %v57)
    sw $t0, -4($sp)
    sw $ra, -8($sp)
    move $a1, $t0
    addiu $sp, $sp, -8
    jal func_fib
    lw $ra, 0($sp)
    addiu $sp, $sp, 8
    lw $t0, -4($sp)
    move $t0, $v0
    # br label %b59

main_bb59:
    # %v60 = sub i32 1197, %v58
    li $k1, 1197
    subu $t0, $k1, $t0
    # %v61 = add i32 %v60, -10091
    addiu $t0, $t0, -10091
    # call void @putint(i32 %v61)
    move $a0, $t0
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([6 x i8], [6 x i8]* @.s.2, i64 0, i64 0))
    la $a0, s2
    li $v0, 4
    syscall
    # ret i32 0
    li $v0, 10
    syscall

func_fib:
fib_bb1:
    # %v2 = icmp eq i32 %v0, 1
    # br i1 %v2, label %b3, label %b4
    bne $a1, 1, fib_bb4

fib_bb3:
    # ret i32 1
    li $v0, 1
    jr $ra

fib_bb4:
    # %v5 = icmp eq i32 %v0, 2
    # br i1 %v5, label %b14, label %b6
    beq $a1, 2, fib_bb14

fib_bb6:
    # %v7 = sub i32 %v0, 1
    addiu $t0, $a1, -1
    # %v8 = call i32 @fib(i32 %v7)
    sw $t0, -8($sp)
    sw $a1, -12($sp)
    sw $ra, -16($sp)
    move $a1, $t0
    addiu $sp, $sp, -16
    jal func_fib
    lw $ra, 0($sp)
    addiu $sp, $sp, 16
    lw $t0, -8($sp)
    lw $a1, -12($sp)
    move $t0, $v0
    # br label %b9

fib_bb9:
    # %v10 = sub i32 %v0, 2
    addiu $t1, $a1, -2
    # %v11 = call i32 @fib(i32 %v10)
    sw $t1, -8($sp)
    sw $t0, -12($sp)
    sw $a1, -16($sp)
    sw $ra, -20($sp)
    move $a1, $t1
    addiu $sp, $sp, -20
    jal func_fib
    lw $ra, 0($sp)
    addiu $sp, $sp, 20
    lw $t1, -8($sp)
    lw $t0, -12($sp)
    lw $a1, -16($sp)
    move $t1, $v0
    # br label %b12

fib_bb12:
    # %v13 = add i32 %v8, %v11
    addu $t0, $t0, $t1
    # ret i32 %v13
    move $v0, $t0
    jr $ra

fib_bb14:
    # ret i32 2
    li $v0, 2
    jr $ra

