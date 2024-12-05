.data:
s0: .asciiz "\nthis is \n "
s1: .asciiz "\n\n"
str: .byte 7, 9, 39, 0 # existed definition
     .space 3 # zeroinitializer
str4: .byte 97, 98, 99, 0 # existed definition
      .space 41 # zeroinitializer
c: .byte 10
c1: .byte 10, 10, 10, 0
c2: .byte 10, 10, 0, 0 # existed definition
    .space 1 # zeroinitializer

.text:
func_main:
main_bb0:
    # %v1 = alloca i32
    addiu $k0, $sp, -108
    sw $k0, -4($sp)
    # store i32 4, i32* %v1
    lw $k0, -4($sp)
    li $k1, 4
    sw $k1, 0($k0)
    # %v2 = load i32, i32* %v1
    lw $k0, -4($sp)
    lw $k0, 0($k0)
    sw $k0, -8($sp)
    # %v3 = icmp eq i32 0, %v2
    li $k0, 0
    lw $k1, -8($sp)
    seq $k0, $k0, $k1
    sw $k0, -12($sp)
    # %v4 = zext i1 %v3 to i32
    lw $k0, -12($sp)
    sw $k0, -16($sp)
    # %v5 = icmp ne i32 %v4, 0
    # br i1 %v5, label %b6, label %b17
    lw $k0, -16($sp)
    bne $k0, 0, main_bb6
    j main_bb17

main_bb6:
    # %v7 = alloca [4 x i8]
    addiu $k0, $sp, -124
    sw $k0, -24($sp)
    # %v8 = getelementptr inbounds [4 x i8], [4 x i8]* %v7, i32 0, i32 0
    lw $k0, -24($sp)
    addiu $k0, $k0, 0
    sw $k0, -28($sp)
    # store i8 97, i8* %v8
    lw $k0, -28($sp)
    li $k1, 97
    sb $k1, 0($k0)
    # %v9 = getelementptr inbounds [4 x i8], [4 x i8]* %v7, i32 0, i32 1
    lw $k0, -24($sp)
    addiu $k0, $k0, 1
    sw $k0, -32($sp)
    # store i8 98, i8* %v9
    lw $k0, -32($sp)
    li $k1, 98
    sb $k1, 0($k0)
    # %v10 = getelementptr inbounds [4 x i8], [4 x i8]* %v7, i32 0, i32 2
    lw $k0, -24($sp)
    addiu $k0, $k0, 2
    sw $k0, -36($sp)
    # store i8 99, i8* %v10
    lw $k0, -36($sp)
    li $k1, 99
    sb $k1, 0($k0)
    # %v11 = getelementptr inbounds [4 x i8], [4 x i8]* %v7, i32 0, i32 3
    lw $k0, -24($sp)
    addiu $k0, $k0, 3
    sw $k0, -40($sp)
    # store i8 0, i8* %v11
    lw $k0, -40($sp)
    li $k1, 0
    sb $k1, 0($k0)
    # %v12 = alloca [95 x i8]
    addiu $k0, $sp, -504
    sw $k0, -44($sp)
    # %v13 = getelementptr inbounds [95 x i8], [95 x i8]* %v12, i32 0, i32 0
    lw $k0, -44($sp)
    addiu $k0, $k0, 0
    sw $k0, -48($sp)
    # store i8 97, i8* %v13
    lw $k0, -48($sp)
    li $k1, 97
    sb $k1, 0($k0)
    # %v14 = getelementptr inbounds [95 x i8], [95 x i8]* %v12, i32 0, i32 1
    lw $k0, -44($sp)
    addiu $k0, $k0, 1
    sw $k0, -52($sp)
    # store i8 98, i8* %v14
    lw $k0, -52($sp)
    li $k1, 98
    sb $k1, 0($k0)
    # %v15 = getelementptr inbounds [95 x i8], [95 x i8]* %v12, i32 0, i32 2
    lw $k0, -44($sp)
    addiu $k0, $k0, 2
    sw $k0, -56($sp)
    # store i8 99, i8* %v15
    lw $k0, -56($sp)
    li $k1, 99
    sb $k1, 0($k0)
    # %v16 = getelementptr inbounds [95 x i8], [95 x i8]* %v12, i32 0, i32 3
    lw $k0, -44($sp)
    addiu $k0, $k0, 3
    sw $k0, -60($sp)
    # store i8 0, i8* %v16
    lw $k0, -60($sp)
    li $k1, 0
    sb $k1, 0($k0)
    # br label %b22
    j main_bb22

main_bb17:
    # %v18 = call i32 @func()
    sw $ra, -508($sp)
    addiu $sp, $sp, -508
    jal func_func
    lw $ra, 0($sp)
    addiu $sp, $sp, 508
    sw $v0, -64($sp)
    # %v19 = icmp eq i32 0, %v18
    li $k0, 0
    lw $k1, -64($sp)
    seq $k0, $k0, $k1
    sw $k0, -68($sp)
    # %v20 = zext i1 %v19 to i32
    lw $k0, -68($sp)
    sw $k0, -72($sp)
    # %v21 = icmp ne i32 %v20, 0
    # br i1 %v21, label %b23, label %b31
    lw $k0, -72($sp)
    bne $k0, 0, main_bb23
    j main_bb31

main_bb22:
    # ret i32 0
    li $v0, 10
    syscall

main_bb23:
    # %v24 = alloca [4 x i8]
    addiu $k0, $sp, -520
    sw $k0, -80($sp)
    # %v25 = getelementptr inbounds [4 x i8], [4 x i8]* %v24, i32 0, i32 0
    lw $k0, -80($sp)
    addiu $k0, $k0, 0
    sw $k0, -84($sp)
    # store i8 37, i8* %v25
    lw $k0, -84($sp)
    li $k1, 37
    sb $k1, 0($k0)
    # %v26 = getelementptr inbounds [4 x i8], [4 x i8]* %v24, i32 0, i32 1
    lw $k0, -80($sp)
    addiu $k0, $k0, 1
    sw $k0, -88($sp)
    # store i8 99, i8* %v26
    lw $k0, -88($sp)
    li $k1, 99
    sb $k1, 0($k0)
    # %v27 = getelementptr inbounds [4 x i8], [4 x i8]* %v24, i32 0, i32 2
    lw $k0, -80($sp)
    addiu $k0, $k0, 2
    sw $k0, -92($sp)
    # store i8 10, i8* %v27
    lw $k0, -92($sp)
    li $k1, 10
    sb $k1, 0($k0)
    # %v28 = getelementptr inbounds [4 x i8], [4 x i8]* %v24, i32 0, i32 3
    lw $k0, -80($sp)
    addiu $k0, $k0, 3
    sw $k0, -96($sp)
    # store i8 0, i8* %v28
    lw $k0, -96($sp)
    li $k1, 0
    sb $k1, 0($k0)
    # %v29 = getelementptr inbounds [4 x i8], [4 x i8]* %v24, i32 0, i32 2
    lw $k0, -80($sp)
    addiu $k0, $k0, 2
    sw $k0, -100($sp)
    # %v30 = load i8, i8* %v29
    lw $k0, -100($sp)
    lb $k0, 0($k0)
    sb $k0, -104($sp)
    # call void @putstr(i8* getelementptr inbounds ([12 x i8], [12 x i8]* @.s.0, i64 0, i64 0))
    la $a0, s0
    li $v0, 4
    syscall
    # call void @putch(i8 %v30)
    lw $a0, -104($sp)
    li $v0, 11
    syscall
    # call void @putstr(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.s.1, i64 0, i64 0))
    la $a0, s1
    li $v0, 4
    syscall
    # ret i32 6
    li $v0, 10
    syscall
    # br label %b31
    j main_bb31

main_bb31:
    # br label %b22
    j main_bb22

func_func:
func_bb0:
    # %v1 = alloca [10 x i8]
    addiu $k0, $sp, -92
    sw $k0, -4($sp)
    # %v2 = getelementptr inbounds [10 x i8], [10 x i8]* %v1, i32 0, i32 0
    lw $k0, -4($sp)
    addiu $k0, $k0, 0
    sw $k0, -8($sp)
    # store i8 97, i8* %v2
    lw $k0, -8($sp)
    li $k1, 97
    sb $k1, 0($k0)
    # %v3 = getelementptr inbounds [10 x i8], [10 x i8]* %v1, i32 0, i32 1
    lw $k0, -4($sp)
    addiu $k0, $k0, 1
    sw $k0, -12($sp)
    # store i8 98, i8* %v3
    lw $k0, -12($sp)
    li $k1, 98
    sb $k1, 0($k0)
    # %v4 = getelementptr inbounds [10 x i8], [10 x i8]* %v1, i32 0, i32 2
    lw $k0, -4($sp)
    addiu $k0, $k0, 2
    sw $k0, -16($sp)
    # store i8 99, i8* %v4
    lw $k0, -16($sp)
    li $k1, 99
    sb $k1, 0($k0)
    # %v5 = getelementptr inbounds [10 x i8], [10 x i8]* %v1, i32 0, i32 3
    lw $k0, -4($sp)
    addiu $k0, $k0, 3
    sw $k0, -20($sp)
    # store i8 0, i8* %v5
    lw $k0, -20($sp)
    li $k1, 0
    sb $k1, 0($k0)
    # %v6 = alloca [4 x i8]
    addiu $k0, $sp, -108
    sw $k0, -24($sp)
    # %v7 = getelementptr inbounds [4 x i8], [4 x i8]* %v6, i32 0, i32 0
    lw $k0, -24($sp)
    addiu $k0, $k0, 0
    sw $k0, -28($sp)
    # store i8 97, i8* %v7
    lw $k0, -28($sp)
    li $k1, 97
    sb $k1, 0($k0)
    # %v8 = getelementptr inbounds [4 x i8], [4 x i8]* %v6, i32 0, i32 1
    lw $k0, -24($sp)
    addiu $k0, $k0, 1
    sw $k0, -32($sp)
    # store i8 98, i8* %v8
    lw $k0, -32($sp)
    li $k1, 98
    sb $k1, 0($k0)
    # %v9 = getelementptr inbounds [4 x i8], [4 x i8]* %v6, i32 0, i32 2
    lw $k0, -24($sp)
    addiu $k0, $k0, 2
    sw $k0, -36($sp)
    # store i8 99, i8* %v9
    lw $k0, -36($sp)
    li $k1, 99
    sb $k1, 0($k0)
    # %v10 = getelementptr inbounds [4 x i8], [4 x i8]* %v6, i32 0, i32 3
    lw $k0, -24($sp)
    addiu $k0, $k0, 3
    sw $k0, -40($sp)
    # store i8 0, i8* %v10
    lw $k0, -40($sp)
    li $k1, 0
    sb $k1, 0($k0)
    # %v11 = getelementptr inbounds [10 x i8], [10 x i8]* %v1, i32 0, i32 6
    lw $k0, -4($sp)
    addiu $k0, $k0, 6
    sw $k0, -44($sp)
    # %v12 = load i8, i8* %v11
    lw $k0, -44($sp)
    lb $k0, 0($k0)
    sb $k0, -48($sp)
    # %v13 = zext i8 %v12 to i32
    lw $k0, -48($sp)
    sw $k0, -52($sp)
    # ret i32 %v13
    lw $v0, -52($sp)
    jr $ra

