.data:
s0: .asciiz "\n"
s1: .asciiz "Your Num is too Big!!!\n"
s2: .asciiz "19373022\n"
s3: .asciiz "b:"
s4: .asciiz "Bool1 is false!\n"
s5: .asciiz "Good!,Num is "
s6: .asciiz "Oh!?\n"
p: .word 88
q: .word 66
r: .word 155
s: .word 15

.text:
func_main:
main_bb0:
    # %v1 = load i32, i32* @p
    la $k0, p
    lw $t0, 0($k0)
    # %v2 = load i32, i32* @q
    la $k0, q
    lw $t1, 0($k0)
    # %v3 = call i32 @min(i32 %v1, i32 %v2)
    sw $t0, -4($sp)
    sw $t1, -8($sp)
    sw $t2, -12($sp)
    sw $ra, -16($sp)
    move $a1, $t0
    move $a2, $t1
    addiu $sp, $sp, -16
    jal func_min
    lw $ra, 0($sp)
    addiu $sp, $sp, 16
    lw $t0, -4($sp)
    lw $t1, -8($sp)
    lw $t2, -12($sp)
    move $t2, $v0
    # %v4 = load i32, i32* @s
    la $k0, s
    lw $t1, 0($k0)
    # %v5 = call i32 @scan()
    sw $t0, -4($sp)
    sw $t1, -8($sp)
    sw $t2, -12($sp)
    sw $ra, -16($sp)
    addiu $sp, $sp, -16
    jal func_scan
    lw $ra, 0($sp)
    addiu $sp, $sp, 16
    lw $t0, -4($sp)
    lw $t1, -8($sp)
    lw $t2, -12($sp)
    move $t0, $v0
    # %v6 = call i32 @max(i32 %v4, i32 %v5)
    sw $t0, -4($sp)
    sw $t1, -8($sp)
    sw $t2, -12($sp)
    sw $ra, -16($sp)
    move $a1, $t1
    move $a2, $t0
    addiu $sp, $sp, -16
    jal func_max
    lw $ra, 0($sp)
    addiu $sp, $sp, 16
    lw $t0, -4($sp)
    lw $t1, -8($sp)
    lw $t2, -12($sp)
    move $t1, $v0
    # %v7 = call i32 @max(i32 %v3, i32 %v6)
    sw $t0, -4($sp)
    sw $t1, -8($sp)
    sw $t2, -12($sp)
    sw $ra, -16($sp)
    move $a1, $t2
    move $a2, $t1
    addiu $sp, $sp, -16
    jal func_max
    lw $ra, 0($sp)
    addiu $sp, $sp, 16
    lw $t0, -4($sp)
    lw $t1, -8($sp)
    lw $t2, -12($sp)
    move $t2, $v0
    # %v8 = load i32, i32* @r
    la $k0, r
    lw $t1, 0($k0)
    # %v9 = call i32 @min(i32 %v8, i32 %v5)
    sw $t0, -4($sp)
    sw $t1, -8($sp)
    sw $t2, -12($sp)
    sw $ra, -16($sp)
    move $a1, $t1
    move $a2, $t0
    addiu $sp, $sp, -16
    jal func_min
    lw $ra, 0($sp)
    addiu $sp, $sp, 16
    lw $t0, -4($sp)
    lw $t1, -8($sp)
    lw $t2, -12($sp)
    move $t1, $v0
    # call void @putstr(i8* getelementptr inbounds ([10 x i8], [10 x i8]* @.s.2, i64 0, i64 0))
    la $a0, s2
    li $v0, 4
    syscall
    # %v10 = add i32 %v9, 58
    addiu $t1, $t1, 58
    # %v11 = sub i32 %v10, %v7
    subu $t1, $t1, $t2
    # call void @putstr(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.s.3, i64 0, i64 0))
    la $a0, s3
    syscall
    # call void @putint(i32 %v11)
    move $a0, $t1
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.0, i64 0, i64 0))
    la $a0, s0
    li $v0, 4
    syscall
    # %v12 = icmp eq i32 0, 0
    li $k0, 0
    li $k1, 0
    seq $t1, $k0, $k1
    # %v13 = icmp ne i32 %v12, 0
    # br i1 %v13, label %b14, label %b15
    beq $t1, 0, main_bb15

main_bb14:
    # call void @putstr(i8* getelementptr inbounds ([17 x i8], [17 x i8]* @.s.4, i64 0, i64 0))
    la $a0, s4
    li $v0, 4
    syscall
    # br label %b15

main_bb15:
    # %v16 = add i32 %v5, 10
    addiu $t1, $t0, 10
    # call void @print(i32 %v16)
    sw $t0, -4($sp)
    sw $t1, -8($sp)
    sw $t2, -12($sp)
    sw $ra, -16($sp)
    move $a1, $t1
    addiu $sp, $sp, -16
    jal func_print
    lw $ra, 0($sp)
    addiu $sp, $sp, 16
    lw $t0, -4($sp)
    lw $t1, -8($sp)
    lw $t2, -12($sp)
    # %v17 = call i32 @mid(i32 %v5, i32 %v5, i32 %v5)
    sw $t0, -4($sp)
    sw $t1, -8($sp)
    sw $t2, -12($sp)
    sw $ra, -16($sp)
    move $a1, $t0
    move $a2, $t0
    move $a3, $t0
    addiu $sp, $sp, -16
    jal func_mid
    lw $ra, 0($sp)
    addiu $sp, $sp, 16
    lw $t0, -4($sp)
    lw $t1, -8($sp)
    lw $t2, -12($sp)
    move $t2, $v0
    # %v18 = icmp sle i32 %v17, %v5
    # br i1 %v18, label %b19, label %b26
    bgt $t2, $t0, main_bb26

main_bb19:
    # %v20 = sdiv i32 %v17, 6
    li $v0, 715827883
    mult $t2, $v0
    mfhi $v1
    sra $v0, $v1, 0
    srl $a0, $t2, 31
    addu $t1, $v0, $a0
    # %v21 = mul i32 %v20, %v5
    mul $t2, $t1, $t0
    # %v22 = sdiv i32 %v21, 2
    sra $v0, $t2, 31
    srl $v0, $v0, 31
    addu $v1, $t2, $v0
    sra $t1, $v1, 1
    # %v23 = mul i32 %v22, 2
    addu $t1, $t1, $t1
    # %v24 = sub i32 %v21, %v23
    subu $t1, $t2, $t1
    # %v25 = add i32 %v5, %v24
    addu $t1, $t0, $t1
    # call void @putstr(i8* getelementptr inbounds ([14 x i8], [14 x i8]* @.s.5, i64 0, i64 0))
    la $a0, s5
    li $v0, 4
    syscall
    # call void @putint(i32 %v25)
    move $a0, $t1
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.0, i64 0, i64 0))
    la $a0, s0
    li $v0, 4
    syscall
    # br label %b28
    j main_bb28

main_bb26:
    # %v27 = icmp slt i32 %v17, %v5
    # br i1 %v27, label %b29, label %b30
    blt $t2, $t0, main_bb29
    j main_bb30

main_bb28:
    # call void @noUse(i32 %v5)
    sw $ra, -4($sp)
    move $a1, $t0
    addiu $sp, $sp, -4
    jal func_noUse
    lw $ra, 0($sp)
    addiu $sp, $sp, 4
    # ret i32 0
    li $v0, 10
    syscall

main_bb29:
    # call void @putstr(i8* getelementptr inbounds ([6 x i8], [6 x i8]* @.s.6, i64 0, i64 0))
    la $a0, s6
    li $v0, 4
    syscall
    # br label %b35
    j main_bb35

main_bb30:
    # %v31 = sdiv i32 %v17, 65535
    li $v0, -2147450879
    mthi $t2
    madd $t2, $v0
    mfhi $v1
    sra $v0, $v1, 15
    srl $a0, $t2, 31
    addu $t1, $v0, $a0
    # %v32 = mul i32 %v31, 65535
    li $v0, 65535
    mul $t1, $t1, $v0
    # %v33 = sub i32 %v17, %v32
    subu $t1, $t2, $t1
    # %v34 = call i32 @factorial(i32 %v33)
    sw $t0, -4($sp)
    sw $t1, -8($sp)
    sw $ra, -12($sp)
    move $a1, $t1
    addiu $sp, $sp, -12
    jal func_factorial
    lw $ra, 0($sp)
    addiu $sp, $sp, 12
    lw $t0, -4($sp)
    lw $t1, -8($sp)
    move $t1, $v0
    # call void @putint(i32 %v34)
    move $a0, $t1
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.0, i64 0, i64 0))
    la $a0, s0
    li $v0, 4
    syscall
    # br label %b35

main_bb35:
    # br label %b28
    j main_bb28

func_max:
max_bb2:
    # %v3 = icmp sgt i32 %v0, %v1
    # br i1 %v3, label %b4, label %b5
    ble $a1, $a2, max_bb5

max_bb4:
    # ret i32 %v0
    move $v0, $a1
    jr $ra

max_bb5:
    # ret i32 %v1
    move $v0, $a2
    jr $ra

func_min:
min_bb2:
    # %v3 = icmp slt i32 %v0, %v1
    # br i1 %v3, label %b4, label %b5
    bge $a1, $a2, min_bb5

min_bb4:
    # ret i32 %v0
    move $v0, $a1
    jr $ra

min_bb5:
    # ret i32 %v1
    move $v0, $a2
    jr $ra

func_scan:
scan_bb0:
    # %v1 = call i32 @getint()
    li $v0, 5
    syscall
    move $t0, $v0
    # ret i32 %v1
    move $v0, $t0
    jr $ra

func_print:
print_bb1:
    # call void @putint(i32 %v0)
    move $a0, $a1
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.0, i64 0, i64 0))
    la $a0, s0
    li $v0, 4
    syscall
    # ret void
    jr $ra

func_noUse:
noUse_bb1:
    # ret void
    jr $ra

func_mid:
mid_bb3:
    # %v4 = call i32 @max(i32 %v0, i32 %v1)
    sw $t0, -16($sp)
    sw $t1, -20($sp)
    sw $a3, -24($sp)
    sw $a1, -28($sp)
    sw $a2, -32($sp)
    sw $ra, -36($sp)
    lw $a1, -28($sp)
    lw $a2, -32($sp)
    addiu $sp, $sp, -36
    jal func_max
    lw $ra, 0($sp)
    addiu $sp, $sp, 36
    lw $t0, -16($sp)
    lw $t1, -20($sp)
    lw $a3, -24($sp)
    lw $a1, -28($sp)
    lw $a2, -32($sp)
    move $t1, $v0
    # %v5 = call i32 @min(i32 %v1, i32 %v2)
    sw $t0, -16($sp)
    sw $t1, -20($sp)
    sw $a3, -24($sp)
    sw $a1, -28($sp)
    sw $a2, -32($sp)
    sw $ra, -36($sp)
    lw $a1, -32($sp)
    lw $a2, -24($sp)
    addiu $sp, $sp, -36
    jal func_min
    lw $ra, 0($sp)
    addiu $sp, $sp, 36
    lw $t0, -16($sp)
    lw $t1, -20($sp)
    lw $a3, -24($sp)
    lw $a1, -28($sp)
    lw $a2, -32($sp)
    move $t0, $v0
    # %v6 = icmp eq i32 %v4, %v5
    # br i1 %v6, label %b7, label %b8
    bne $t1, $t0, mid_bb8

mid_bb7:
    # ret i32 %v1
    move $v0, $a2
    jr $ra

mid_bb8:
    # %v9 = call i32 @min(i32 %v0, i32 %v2)
    sw $t0, -16($sp)
    sw $t1, -20($sp)
    sw $a3, -24($sp)
    sw $a1, -28($sp)
    sw $a2, -32($sp)
    sw $ra, -36($sp)
    lw $a1, -28($sp)
    lw $a2, -24($sp)
    addiu $sp, $sp, -36
    jal func_min
    lw $ra, 0($sp)
    addiu $sp, $sp, 36
    lw $t0, -16($sp)
    lw $t1, -20($sp)
    lw $a3, -24($sp)
    lw $a1, -28($sp)
    lw $a2, -32($sp)
    move $t0, $v0
    # %v10 = icmp ne i32 %v4, %v9
    # br i1 %v10, label %b11, label %b12
    beq $t1, $t0, mid_bb12

mid_bb11:
    # ret i32 %v2
    move $v0, $a3
    jr $ra

mid_bb12:
    # ret i32 %v0
    move $v0, $a1
    jr $ra

func_factorial:
factorial_bb1:
    # %v2 = icmp sgt i32 %v0, 20
    # br i1 %v2, label %b3, label %b4
    ble $a1, 20, factorial_bb4

factorial_bb3:
    # call void @putstr(i8* getelementptr inbounds ([24 x i8], [24 x i8]* @.s.1, i64 0, i64 0))
    la $a0, s1
    li $v0, 4
    syscall
    # ret i32 -1
    li $v0, -1
    jr $ra

factorial_bb4:
    # move 1 -> %v6
    li $t1, 1
    # move %v0 -> %v7
    move $t0, $a1
    # br label %b6

factorial_bb6:
    # %v7 = icmp ne i32 %v7, 0
    # br i1 %v7, label %b8, label %b13
    beq $t0, 0, factorial_bb13

factorial_bb8:
    # %v9 = mul i32 %v6, %v7
    mul $t1, $t1, $t0
    # %v10 = sub i32 %v7, 1
    addiu $t2, $t0, -1
    # br label %b11

factorial_bb11:
    # move %v9 -> %v6
    # move %v10 -> %v7
    move $t0, $t2
    # br label %b6
    j factorial_bb6

factorial_bb13:
    # ret i32 %v6
    move $v0, $t1
    jr $ra

