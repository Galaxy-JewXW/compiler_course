.data:
s0: .asciiz " "
s1: .asciiz "\n"
s2: .asciiz "END\n"
global_length: .word 0
global_buffer: .word 0:100

.text:
func_main:
main_bb0:
    # %v1 = alloca [13 x i8]
    addiu $k0, $sp, -208
    sw $k0, -4($sp)
    # %v2 = getelementptr inbounds [13 x i8], [13 x i8]* %v1, i32 0, i32 0
    lw $k0, -4($sp)
    addiu $k0, $k0, 0
    sw $k0, -8($sp)
    # store i8 72, i8* %v2
    lw $k0, -8($sp)
    li $k1, 72
    sw $k1, 0($k0)
    # %v3 = getelementptr inbounds [13 x i8], [13 x i8]* %v1, i32 0, i32 1
    lw $k0, -4($sp)
    addiu $k0, $k0, 4
    sw $k0, -12($sp)
    # store i8 101, i8* %v3
    lw $k0, -12($sp)
    li $k1, 101
    sw $k1, 0($k0)
    # %v4 = getelementptr inbounds [13 x i8], [13 x i8]* %v1, i32 0, i32 2
    lw $k0, -4($sp)
    addiu $k0, $k0, 8
    sw $k0, -16($sp)
    # store i8 108, i8* %v4
    lw $k0, -16($sp)
    li $k1, 108
    sw $k1, 0($k0)
    # %v5 = getelementptr inbounds [13 x i8], [13 x i8]* %v1, i32 0, i32 3
    lw $k0, -4($sp)
    addiu $k0, $k0, 12
    sw $k0, -20($sp)
    # store i8 108, i8* %v5
    lw $k0, -20($sp)
    li $k1, 108
    sw $k1, 0($k0)
    # %v6 = getelementptr inbounds [13 x i8], [13 x i8]* %v1, i32 0, i32 4
    lw $k0, -4($sp)
    addiu $k0, $k0, 16
    sw $k0, -24($sp)
    # store i8 111, i8* %v6
    lw $k0, -24($sp)
    li $k1, 111
    sw $k1, 0($k0)
    # %v7 = getelementptr inbounds [13 x i8], [13 x i8]* %v1, i32 0, i32 5
    lw $k0, -4($sp)
    addiu $k0, $k0, 20
    sw $k0, -28($sp)
    # store i8 0, i8* %v7
    lw $k0, -28($sp)
    li $k1, 0
    sw $k1, 0($k0)
    # %v8 = getelementptr inbounds [13 x i8], [13 x i8]* %v1, i32 0, i32 6
    lw $k0, -4($sp)
    addiu $k0, $k0, 24
    sw $k0, -32($sp)
    # store i8 0, i8* %v8
    lw $k0, -32($sp)
    li $k1, 0
    sw $k1, 0($k0)
    # %v9 = getelementptr inbounds [13 x i8], [13 x i8]* %v1, i32 0, i32 7
    lw $k0, -4($sp)
    addiu $k0, $k0, 28
    sw $k0, -36($sp)
    # store i8 0, i8* %v9
    lw $k0, -36($sp)
    li $k1, 0
    sw $k1, 0($k0)
    # %v10 = getelementptr inbounds [13 x i8], [13 x i8]* %v1, i32 0, i32 8
    lw $k0, -4($sp)
    addiu $k0, $k0, 32
    sw $k0, -40($sp)
    # store i8 0, i8* %v10
    lw $k0, -40($sp)
    li $k1, 0
    sw $k1, 0($k0)
    # %v11 = getelementptr inbounds [13 x i8], [13 x i8]* %v1, i32 0, i32 9
    lw $k0, -4($sp)
    addiu $k0, $k0, 36
    sw $k0, -44($sp)
    # store i8 0, i8* %v11
    lw $k0, -44($sp)
    li $k1, 0
    sw $k1, 0($k0)
    # %v12 = getelementptr inbounds [13 x i8], [13 x i8]* %v1, i32 0, i32 10
    lw $k0, -4($sp)
    addiu $k0, $k0, 40
    sw $k0, -48($sp)
    # store i8 0, i8* %v12
    lw $k0, -48($sp)
    li $k1, 0
    sw $k1, 0($k0)
    # %v13 = getelementptr inbounds [13 x i8], [13 x i8]* %v1, i32 0, i32 11
    lw $k0, -4($sp)
    addiu $k0, $k0, 44
    sw $k0, -52($sp)
    # store i8 0, i8* %v13
    lw $k0, -52($sp)
    li $k1, 0
    sw $k1, 0($k0)
    # %v14 = getelementptr inbounds [13 x i8], [13 x i8]* %v1, i32 0, i32 12
    lw $k0, -4($sp)
    addiu $k0, $k0, 48
    sw $k0, -56($sp)
    # store i8 0, i8* %v14
    lw $k0, -56($sp)
    li $k1, 0
    sw $k1, 0($k0)
    # %v15 = alloca [13 x i8]
    addiu $k0, $sp, -260
    sw $k0, -60($sp)
    # %v16 = getelementptr inbounds [13 x i8], [13 x i8]* %v15, i32 0, i32 0
    lw $k0, -60($sp)
    addiu $k0, $k0, 0
    sw $k0, -64($sp)
    # store i8 87, i8* %v16
    lw $k0, -64($sp)
    li $k1, 87
    sw $k1, 0($k0)
    # %v17 = getelementptr inbounds [13 x i8], [13 x i8]* %v15, i32 0, i32 1
    lw $k0, -60($sp)
    addiu $k0, $k0, 4
    sw $k0, -68($sp)
    # store i8 111, i8* %v17
    lw $k0, -68($sp)
    li $k1, 111
    sw $k1, 0($k0)
    # %v18 = getelementptr inbounds [13 x i8], [13 x i8]* %v15, i32 0, i32 2
    lw $k0, -60($sp)
    addiu $k0, $k0, 8
    sw $k0, -72($sp)
    # store i8 114, i8* %v18
    lw $k0, -72($sp)
    li $k1, 114
    sw $k1, 0($k0)
    # %v19 = getelementptr inbounds [13 x i8], [13 x i8]* %v15, i32 0, i32 3
    lw $k0, -60($sp)
    addiu $k0, $k0, 12
    sw $k0, -76($sp)
    # store i8 108, i8* %v19
    lw $k0, -76($sp)
    li $k1, 108
    sw $k1, 0($k0)
    # %v20 = getelementptr inbounds [13 x i8], [13 x i8]* %v15, i32 0, i32 4
    lw $k0, -60($sp)
    addiu $k0, $k0, 16
    sw $k0, -80($sp)
    # store i8 100, i8* %v20
    lw $k0, -80($sp)
    li $k1, 100
    sw $k1, 0($k0)
    # %v21 = getelementptr inbounds [13 x i8], [13 x i8]* %v15, i32 0, i32 5
    lw $k0, -60($sp)
    addiu $k0, $k0, 20
    sw $k0, -84($sp)
    # store i8 0, i8* %v21
    lw $k0, -84($sp)
    li $k1, 0
    sw $k1, 0($k0)
    # %v22 = getelementptr inbounds [13 x i8], [13 x i8]* %v15, i32 0, i32 6
    lw $k0, -60($sp)
    addiu $k0, $k0, 24
    sw $k0, -88($sp)
    # store i8 0, i8* %v22
    lw $k0, -88($sp)
    li $k1, 0
    sw $k1, 0($k0)
    # %v23 = getelementptr inbounds [13 x i8], [13 x i8]* %v15, i32 0, i32 7
    lw $k0, -60($sp)
    addiu $k0, $k0, 28
    sw $k0, -92($sp)
    # store i8 0, i8* %v23
    lw $k0, -92($sp)
    li $k1, 0
    sw $k1, 0($k0)
    # %v24 = getelementptr inbounds [13 x i8], [13 x i8]* %v15, i32 0, i32 8
    lw $k0, -60($sp)
    addiu $k0, $k0, 32
    sw $k0, -96($sp)
    # store i8 0, i8* %v24
    lw $k0, -96($sp)
    li $k1, 0
    sw $k1, 0($k0)
    # %v25 = getelementptr inbounds [13 x i8], [13 x i8]* %v15, i32 0, i32 9
    lw $k0, -60($sp)
    addiu $k0, $k0, 36
    sw $k0, -100($sp)
    # store i8 0, i8* %v25
    lw $k0, -100($sp)
    li $k1, 0
    sw $k1, 0($k0)
    # %v26 = getelementptr inbounds [13 x i8], [13 x i8]* %v15, i32 0, i32 10
    lw $k0, -60($sp)
    addiu $k0, $k0, 40
    sw $k0, -104($sp)
    # store i8 0, i8* %v26
    lw $k0, -104($sp)
    li $k1, 0
    sw $k1, 0($k0)
    # %v27 = getelementptr inbounds [13 x i8], [13 x i8]* %v15, i32 0, i32 11
    lw $k0, -60($sp)
    addiu $k0, $k0, 44
    sw $k0, -108($sp)
    # store i8 0, i8* %v27
    lw $k0, -108($sp)
    li $k1, 0
    sw $k1, 0($k0)
    # %v28 = getelementptr inbounds [13 x i8], [13 x i8]* %v15, i32 0, i32 12
    lw $k0, -60($sp)
    addiu $k0, $k0, 48
    sw $k0, -112($sp)
    # store i8 0, i8* %v28
    lw $k0, -112($sp)
    li $k1, 0
    sw $k1, 0($k0)
    # %v29 = alloca [13 x i8]
    addiu $k0, $sp, -312
    sw $k0, -116($sp)
    # %v30 = alloca i32
    addiu $k0, $sp, -316
    sw $k0, -120($sp)
    # %v31 = getelementptr inbounds [13 x i8], [13 x i8]* %v29, i32 0, i32 0
    lw $k0, -116($sp)
    addiu $k0, $k0, 0
    sw $k0, -124($sp)
    # %v32 = call i32 @my_strlen(i8* %v31)
    sw $ra, -320($sp)
    lw $a1, -124($sp)
    addiu $sp, $sp, -320
    jal func_my_strlen
    lw $ra, 0($sp)
    addiu $sp, $sp, 320
    sw $v0, -128($sp)
    # call void @putint(i32 %v32)
    lw $a0, -128($sp)
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.1, i64 0, i64 0))
    li $v0, 11
    li $a0, 10
    syscall
    # %v33 = getelementptr inbounds [13 x i8], [13 x i8]* %v29, i32 0, i32 0
    lw $k0, -116($sp)
    addiu $k0, $k0, 0
    sw $k0, -132($sp)
    # %v34 = getelementptr inbounds [13 x i8], [13 x i8]* %v1, i32 0, i32 0
    lw $k0, -4($sp)
    addiu $k0, $k0, 0
    sw $k0, -136($sp)
    # call void @my_strcpy(i8* %v33, i8* %v34)
    sw $ra, -320($sp)
    lw $a1, -132($sp)
    lw $a2, -136($sp)
    addiu $sp, $sp, -320
    jal func_my_strcpy
    lw $ra, 0($sp)
    addiu $sp, $sp, 320
    # %v35 = getelementptr inbounds [13 x i8], [13 x i8]* %v29, i32 0, i32 2
    lw $k0, -116($sp)
    addiu $k0, $k0, 8
    sw $k0, -140($sp)
    # %v36 = load i8, i8* %v35
    lw $k0, -140($sp)
    lw $k0, 0($k0)
    sw $k0, -144($sp)
    # %v37 = zext i8 %v36 to i32
    lw $k0, -144($sp)
    sw $k0, -148($sp)
    # call void @putint(i32 %v37)
    lw $a0, -148($sp)
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.1, i64 0, i64 0))
    li $v0, 11
    li $a0, 10
    syscall
    # %v38 = getelementptr inbounds [13 x i8], [13 x i8]* %v29, i32 0, i32 0
    lw $k0, -116($sp)
    addiu $k0, $k0, 0
    sw $k0, -152($sp)
    # %v39 = call i32 @my_strlen(i8* %v38)
    sw $ra, -320($sp)
    lw $a1, -152($sp)
    addiu $sp, $sp, -320
    jal func_my_strlen
    lw $ra, 0($sp)
    addiu $sp, $sp, 320
    sw $v0, -156($sp)
    # call void @putint(i32 %v39)
    lw $a0, -156($sp)
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.1, i64 0, i64 0))
    li $v0, 11
    li $a0, 10
    syscall
    # ret i32 0
    li $v0, 10
    syscall

func_my_strlen:
my_strlen_bb1:
    # %v2 = alloca i32
    addiu $k0, $sp, -44
    sw $k0, -8($sp)
    # store i32 0, i32* %v2
    lw $k0, -8($sp)
    li $k1, 0
    sw $k1, 0($k0)
    # store i32 0, i32* %v2
    lw $k0, -8($sp)
    li $k1, 0
    sw $k1, 0($k0)
    # br label %b3
    j my_strlen_bb3

my_strlen_bb3:
    # %v4 = load i32, i32* %v2
    lw $k0, -8($sp)
    lw $k0, 0($k0)
    sw $k0, -12($sp)
    # %v5 = getelementptr inbounds i8, i8* %v0, i32 %v4
    lw $k1, -12($sp)
    sll $k1, $k1, 2
    addu $k0, $a1, $k1
    sw $k0, -16($sp)
    # %v6 = load i8, i8* %v5
    lw $k0, -16($sp)
    lw $k0, 0($k0)
    sw $k0, -20($sp)
    # %v7 = zext i8 %v6 to i32
    lw $k0, -20($sp)
    sw $k0, -24($sp)
    # %v8 = icmp ne i32 %v7, 0
    # br i1 %v8, label %b9, label %b13
    lw $k0, -24($sp)
    bne $k0, 0, my_strlen_bb9
    j my_strlen_bb13

my_strlen_bb9:
    # br label %b10
    j my_strlen_bb10

my_strlen_bb10:
    # %v11 = load i32, i32* %v2
    lw $k0, -8($sp)
    lw $k0, 0($k0)
    sw $k0, -32($sp)
    # %v12 = add i32 %v11, 1
    lw $k0, -32($sp)
    addiu $k0, $k0, 1
    sw $k0, -36($sp)
    # store i32 %v12, i32* %v2
    lw $k0, -8($sp)
    lw $k1, -36($sp)
    sw $k1, 0($k0)
    # br label %b3
    j my_strlen_bb3

my_strlen_bb13:
    # %v14 = load i32, i32* %v2
    lw $k0, -8($sp)
    lw $k0, 0($k0)
    sw $k0, -40($sp)
    # ret i32 %v14
    lw $v0, -40($sp)
    jr $ra

func_my_strcpy:
my_strcpy_bb2:
    # %v3 = alloca i32
    addiu $k0, $sp, -104
    sw $k0, -12($sp)
    # store i32 0, i32* %v3
    lw $k0, -12($sp)
    li $k1, 0
    sw $k1, 0($k0)
    # store i32 0, i32* %v3
    lw $k0, -12($sp)
    li $k1, 0
    sw $k1, 0($k0)
    # br label %b4
    j my_strcpy_bb4

my_strcpy_bb4:
    # %v5 = load i32, i32* %v3
    lw $k0, -12($sp)
    lw $k0, 0($k0)
    sw $k0, -16($sp)
    # %v6 = getelementptr inbounds i8, i8* %v1, i32 %v5
    lw $k1, -16($sp)
    sll $k1, $k1, 2
    addu $k0, $a2, $k1
    sw $k0, -20($sp)
    # %v7 = load i8, i8* %v6
    lw $k0, -20($sp)
    lw $k0, 0($k0)
    sw $k0, -24($sp)
    # %v8 = zext i8 %v7 to i32
    lw $k0, -24($sp)
    sw $k0, -28($sp)
    # %v9 = icmp ne i32 %v8, 0
    # br i1 %v9, label %b10, label %b27
    lw $k0, -28($sp)
    bne $k0, 0, my_strcpy_bb10
    j my_strcpy_bb27

my_strcpy_bb10:
    # %v11 = load i32, i32* %v3
    lw $k0, -12($sp)
    lw $k0, 0($k0)
    sw $k0, -36($sp)
    # %v12 = getelementptr inbounds i8, i8* %v1, i32 %v11
    lw $k1, -36($sp)
    sll $k1, $k1, 2
    addu $k0, $a2, $k1
    sw $k0, -40($sp)
    # %v13 = load i8, i8* %v12
    lw $k0, -40($sp)
    lw $k0, 0($k0)
    sw $k0, -44($sp)
    # %v14 = load i32, i32* %v3
    lw $k0, -12($sp)
    lw $k0, 0($k0)
    sw $k0, -48($sp)
    # %v15 = getelementptr inbounds i8, i8* %v0, i32 %v14
    lw $k1, -48($sp)
    sll $k1, $k1, 2
    addu $k0, $a1, $k1
    sw $k0, -52($sp)
    # %v16 = load i8, i8* %v15
    lw $k0, -52($sp)
    lw $k0, 0($k0)
    sw $k0, -56($sp)
    # %v17 = zext i8 %v13 to i32
    lw $k0, -44($sp)
    sw $k0, -60($sp)
    # call void @putint(i32 %v17)
    lw $a0, -60($sp)
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.0, i64 0, i64 0))
    li $v0, 11
    li $a0, 32
    syscall
    # %v18 = zext i8 %v16 to i32
    lw $k0, -56($sp)
    sw $k0, -64($sp)
    # call void @putint(i32 %v18)
    lw $a0, -64($sp)
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.1, i64 0, i64 0))
    li $v0, 11
    li $a0, 10
    syscall
    # %v19 = load i32, i32* %v3
    lw $k0, -12($sp)
    lw $k0, 0($k0)
    sw $k0, -68($sp)
    # %v20 = getelementptr inbounds i8, i8* %v0, i32 %v19
    lw $k1, -68($sp)
    sll $k1, $k1, 2
    addu $k0, $a1, $k1
    sw $k0, -72($sp)
    # %v21 = load i32, i32* %v3
    lw $k0, -12($sp)
    lw $k0, 0($k0)
    sw $k0, -76($sp)
    # %v22 = getelementptr inbounds i8, i8* %v1, i32 %v21
    lw $k1, -76($sp)
    sll $k1, $k1, 2
    addu $k0, $a2, $k1
    sw $k0, -80($sp)
    # %v23 = load i8, i8* %v22
    lw $k0, -80($sp)
    lw $k0, 0($k0)
    sw $k0, -84($sp)
    # store i8 %v23, i8* %v20
    lw $k0, -72($sp)
    lw $k1, -84($sp)
    sw $k1, 0($k0)
    # br label %b24
    j my_strcpy_bb24

my_strcpy_bb24:
    # %v25 = load i32, i32* %v3
    lw $k0, -12($sp)
    lw $k0, 0($k0)
    sw $k0, -88($sp)
    # %v26 = add i32 %v25, 1
    lw $k0, -88($sp)
    addiu $k0, $k0, 1
    sw $k0, -92($sp)
    # store i32 %v26, i32* %v3
    lw $k0, -12($sp)
    lw $k1, -92($sp)
    sw $k1, 0($k0)
    # br label %b4
    j my_strcpy_bb4

my_strcpy_bb27:
    # %v28 = load i32, i32* %v3
    lw $k0, -12($sp)
    lw $k0, 0($k0)
    sw $k0, -96($sp)
    # %v29 = getelementptr inbounds i8, i8* %v0, i32 %v28
    lw $k1, -96($sp)
    sll $k1, $k1, 2
    addu $k0, $a1, $k1
    sw $k0, -100($sp)
    # store i8 0, i8* %v29
    lw $k0, -100($sp)
    li $k1, 0
    sw $k1, 0($k0)
    # call void @putstr(i8* getelementptr inbounds ([5 x i8], [5 x i8]* @.s.2, i64 0, i64 0))
    la $a0, s2
    li $v0, 4
    syscall
    # ret void
    jr $ra

