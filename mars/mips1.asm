.data:
s0: .asciiz "Sum: "
s1: .asciiz "\n"
s2: .asciiz "Product: "
s3: .asciiz "Case 1\n"
s4: .asciiz "Case 2\n"
s5: .asciiz "Case 3\n"
s6: .asciiz "Case 4\n"
s7: .asciiz "Case 5\n"
s8: .asciiz "Case 6\n"
global_int: .word 0
global_char: .byte 0
arr: .word 0:10
str: .byte 0:100

.text:
func_main:
main_bb0:
    # %v1 = alloca i32
    addiu $k0, $sp, -200
    sw $k0, -4($sp)
    # %v2 = alloca i32
    addiu $k0, $sp, -204
    sw $k0, -8($sp)
    # %v3 = alloca i8
    addiu $k0, $sp, -208
    sw $k0, -12($sp)
    # %v4 = call i32 @getint()
    li $v0, 5
    syscall
    sw $v0, -16($sp)
    # store i32 %v4, i32* %v1
    lw $k0, -4($sp)
    lw $k1, -16($sp)
    sw $k1, 0($k0)
    # %v5 = call i32 @getint()
    li $v0, 5
    syscall
    sw $v0, -20($sp)
    # store i32 %v5, i32* %v2
    lw $k0, -8($sp)
    lw $k1, -20($sp)
    sw $k1, 0($k0)
    # %v6 = alloca i32
    addiu $k0, $sp, -212
    sw $k0, -24($sp)
    # %v7 = load i32, i32* %v1
    lw $k0, -4($sp)
    lw $k0, 0($k0)
    sw $k0, -28($sp)
    # %v8 = load i32, i32* %v2
    lw $k0, -8($sp)
    lw $k0, 0($k0)
    sw $k0, -32($sp)
    # %v9 = call i32 @add(i32 %v7, i32 %v8)
    sw $ra, -216($sp)
    lw $a1, -28($sp)
    lw $a2, -32($sp)
    addiu $sp, $sp, -216
    jal func_add
    lw $ra, 0($sp)
    addiu $sp, $sp, 216
    sw $v0, -36($sp)
    # store i32 %v9, i32* %v6
    lw $k0, -24($sp)
    lw $k1, -36($sp)
    sw $k1, 0($k0)
    # %v10 = alloca i32
    addiu $k0, $sp, -216
    sw $k0, -40($sp)
    # %v11 = load i32, i32* %v1
    lw $k0, -4($sp)
    lw $k0, 0($k0)
    sw $k0, -44($sp)
    # %v12 = load i32, i32* %v2
    lw $k0, -8($sp)
    lw $k0, 0($k0)
    sw $k0, -48($sp)
    # %v13 = call i32 @multiply(i32 %v11, i32 %v12)
    sw $ra, -220($sp)
    lw $a1, -44($sp)
    lw $a2, -48($sp)
    addiu $sp, $sp, -220
    jal func_multiply
    lw $ra, 0($sp)
    addiu $sp, $sp, 220
    sw $v0, -52($sp)
    # store i32 %v13, i32* %v10
    lw $k0, -40($sp)
    lw $k1, -52($sp)
    sw $k1, 0($k0)
    # %v14 = load i32, i32* %v6
    lw $k0, -24($sp)
    lw $k0, 0($k0)
    sw $k0, -56($sp)
    # call void @putstr(i8* getelementptr inbounds ([6 x i8], [6 x i8]* @.s.0, i64 0, i64 0))
    la $a0, s0
    li $v0, 4
    syscall
    # call void @putint(i32 %v14)
    lw $a0, -56($sp)
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.1, i64 0, i64 0))
    li $v0, 11
    li $a0, 10
    syscall
    # %v15 = load i32, i32* %v10
    lw $k0, -40($sp)
    lw $k0, 0($k0)
    sw $k0, -60($sp)
    # call void @putstr(i8* getelementptr inbounds ([10 x i8], [10 x i8]* @.s.2, i64 0, i64 0))
    la $a0, s2
    li $v0, 4
    syscall
    # call void @putint(i32 %v15)
    lw $a0, -60($sp)
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.1, i64 0, i64 0))
    li $v0, 11
    li $a0, 10
    syscall
    # %v16 = getelementptr inbounds [10 x i32], [10 x i32]* @arr, i32 0, i32 0
    la $k0, arr
    addiu $k0, $k0, 0
    sw $k0, -64($sp)
    # call void @process_array(i32* %v16)
    sw $ra, -220($sp)
    lw $a1, -64($sp)
    addiu $sp, $sp, -220
    jal func_process_array
    lw $ra, 0($sp)
    addiu $sp, $sp, 220
    # %v17 = alloca i32
    addiu $k0, $sp, -220
    sw $k0, -68($sp)
    # store i32 0, i32* %v17
    lw $k0, -68($sp)
    li $k1, 0
    sw $k1, 0($k0)
    # %v18 = load i32, i32* %v6
    lw $k0, -24($sp)
    lw $k0, 0($k0)
    sw $k0, -72($sp)
    # %v19 = icmp eq i32 %v18, 0
    # br i1 %v19, label %b20, label %b23
    lw $k0, -72($sp)
    beq $k0, 0, main_bb20
    j main_bb23

main_bb20:
    # %v21 = load i32, i32* %v10
    lw $k0, -40($sp)
    lw $k0, 0($k0)
    sw $k0, -80($sp)
    # %v22 = icmp eq i32 %v21, 0
    # br i1 %v22, label %b29, label %b33
    lw $k0, -80($sp)
    beq $k0, 0, main_bb29
    j main_bb33

main_bb23:
    # %v24 = call i32 @getint()
    li $v0, 5
    syscall
    sw $v0, -88($sp)
    # store i32 %v24, i32* %v17
    lw $k0, -68($sp)
    lw $k1, -88($sp)
    sw $k1, 0($k0)
    # %v25 = load i32, i32* %v17
    lw $k0, -68($sp)
    lw $k0, 0($k0)
    sw $k0, -92($sp)
    # %v26 = icmp eq i32 %v25, 4
    # br i1 %v26, label %b50, label %b53
    lw $k0, -92($sp)
    beq $k0, 4, main_bb50
    j main_bb53

main_bb27:
    # %v28 = alloca i32
    addiu $k0, $sp, -224
    sw $k0, -100($sp)
    # store i32 0, i32* %v28
    lw $k0, -100($sp)
    li $k1, 0
    sw $k1, 0($k0)
    # br label %b56
    j main_bb56

main_bb29:
    # %v30 = call i32 @getint()
    li $v0, 5
    syscall
    sw $v0, -104($sp)
    # store i32 %v30, i32* %v17
    lw $k0, -68($sp)
    lw $k1, -104($sp)
    sw $k1, 0($k0)
    # %v31 = load i32, i32* %v17
    lw $k0, -68($sp)
    lw $k0, 0($k0)
    sw $k0, -108($sp)
    # %v32 = icmp eq i32 %v31, 0
    # br i1 %v32, label %b38, label %b41
    lw $k0, -108($sp)
    beq $k0, 0, main_bb38
    j main_bb41

main_bb33:
    # %v34 = call i32 @getint()
    li $v0, 5
    syscall
    sw $v0, -116($sp)
    # store i32 %v34, i32* %v17
    lw $k0, -68($sp)
    lw $k1, -116($sp)
    sw $k1, 0($k0)
    # %v35 = load i32, i32* %v17
    lw $k0, -68($sp)
    lw $k0, 0($k0)
    sw $k0, -120($sp)
    # %v36 = icmp eq i32 %v35, 2
    # br i1 %v36, label %b44, label %b47
    lw $k0, -120($sp)
    beq $k0, 2, main_bb44
    j main_bb47

main_bb37:
    # br label %b27
    j main_bb27

main_bb38:
    # call void @putstr(i8* getelementptr inbounds ([8 x i8], [8 x i8]* @.s.3, i64 0, i64 0))
    la $a0, s3
    li $v0, 4
    syscall
    # br label %b40
    j main_bb40

main_bb39:
    # call void @putstr(i8* getelementptr inbounds ([8 x i8], [8 x i8]* @.s.4, i64 0, i64 0))
    la $a0, s4
    li $v0, 4
    syscall
    # br label %b40
    j main_bb40

main_bb40:
    # br label %b37
    j main_bb37

main_bb41:
    # %v42 = load i32, i32* %v17
    lw $k0, -68($sp)
    lw $k0, 0($k0)
    sw $k0, -128($sp)
    # %v43 = icmp eq i32 %v42, 1
    # br i1 %v43, label %b38, label %b39
    lw $k0, -128($sp)
    beq $k0, 1, main_bb38
    j main_bb39

main_bb44:
    # call void @putstr(i8* getelementptr inbounds ([8 x i8], [8 x i8]* @.s.5, i64 0, i64 0))
    la $a0, s5
    li $v0, 4
    syscall
    # br label %b46
    j main_bb46

main_bb45:
    # call void @putstr(i8* getelementptr inbounds ([8 x i8], [8 x i8]* @.s.6, i64 0, i64 0))
    la $a0, s6
    li $v0, 4
    syscall
    # br label %b46
    j main_bb46

main_bb46:
    # br label %b37
    j main_bb37

main_bb47:
    # %v48 = load i32, i32* %v17
    lw $k0, -68($sp)
    lw $k0, 0($k0)
    sw $k0, -136($sp)
    # %v49 = icmp eq i32 %v48, 3
    # br i1 %v49, label %b44, label %b45
    lw $k0, -136($sp)
    beq $k0, 3, main_bb44
    j main_bb45

main_bb50:
    # call void @putstr(i8* getelementptr inbounds ([8 x i8], [8 x i8]* @.s.7, i64 0, i64 0))
    la $a0, s7
    li $v0, 4
    syscall
    # br label %b52
    j main_bb52

main_bb51:
    # call void @putstr(i8* getelementptr inbounds ([8 x i8], [8 x i8]* @.s.8, i64 0, i64 0))
    la $a0, s8
    li $v0, 4
    syscall
    # br label %b52
    j main_bb52

main_bb52:
    # br label %b27
    j main_bb27

main_bb53:
    # %v54 = load i32, i32* %v17
    lw $k0, -68($sp)
    lw $k0, 0($k0)
    sw $k0, -144($sp)
    # %v55 = icmp eq i32 %v54, 5
    # br i1 %v55, label %b50, label %b51
    lw $k0, -144($sp)
    beq $k0, 5, main_bb50
    j main_bb51

main_bb56:
    # %v57 = load i32, i32* %v28
    lw $k0, -100($sp)
    lw $k0, 0($k0)
    sw $k0, -152($sp)
    # %v58 = icmp slt i32 %v57, 5
    # br i1 %v58, label %b59, label %b65
    lw $k0, -152($sp)
    blt $k0, 5, main_bb59
    j main_bb65

main_bb59:
    # %v60 = load i32, i32* %v28
    lw $k0, -100($sp)
    lw $k0, 0($k0)
    sw $k0, -160($sp)
    # %v61 = icmp eq i32 %v60, 0
    # br i1 %v61, label %b66, label %b70
    lw $k0, -160($sp)
    beq $k0, 0, main_bb66
    j main_bb70

main_bb62:
    # %v63 = load i32, i32* %v28
    lw $k0, -100($sp)
    lw $k0, 0($k0)
    sw $k0, -168($sp)
    # %v64 = add i32 %v63, 1
    lw $k0, -168($sp)
    addiu $k0, $k0, 1
    sw $k0, -172($sp)
    # store i32 %v64, i32* %v28
    lw $k0, -100($sp)
    lw $k1, -172($sp)
    sw $k1, 0($k0)
    # br label %b56
    j main_bb56

main_bb65:
    # ret i32 0
    li $v0, 10
    syscall

main_bb66:
    # %v67 = load i32, i32* @global_int
    la $k0, global_int
    lw $k0, 0($k0)
    sw $k0, -176($sp)
    # %v68 = load i32, i32* %v28
    lw $k0, -100($sp)
    lw $k0, 0($k0)
    sw $k0, -180($sp)
    # %v69 = call i32 @add(i32 %v67, i32 %v68)
    sw $ra, -228($sp)
    lw $a1, -176($sp)
    lw $a2, -180($sp)
    addiu $sp, $sp, -228
    jal func_add
    lw $ra, 0($sp)
    addiu $sp, $sp, 228
    sw $v0, -184($sp)
    # store i32 %v69, i32* @global_int
    la $k0, global_int
    lw $k1, -184($sp)
    sw $k1, 0($k0)
    # br label %b74
    j main_bb74

main_bb70:
    # %v71 = load i32, i32* @global_int
    la $k0, global_int
    lw $k0, 0($k0)
    sw $k0, -188($sp)
    # %v72 = load i32, i32* %v28
    lw $k0, -100($sp)
    lw $k0, 0($k0)
    sw $k0, -192($sp)
    # %v73 = call i32 @multiply(i32 %v71, i32 %v72)
    sw $ra, -228($sp)
    lw $a1, -188($sp)
    lw $a2, -192($sp)
    addiu $sp, $sp, -228
    jal func_multiply
    lw $ra, 0($sp)
    addiu $sp, $sp, 228
    sw $v0, -196($sp)
    # store i32 %v73, i32* @global_int
    la $k0, global_int
    lw $k1, -196($sp)
    sw $k1, 0($k0)
    # br label %b74
    j main_bb74

main_bb74:
    # br label %b62
    j main_bb62

func_add:
add_bb2:
    # %v3 = alloca i32
    addiu $k0, $sp, -72
    sw $k0, -12($sp)
    # store i32 %v0, i32* %v3
    lw $k0, -12($sp)
    sw $a1, 0($k0)
    # %v4 = alloca i32
    addiu $k0, $sp, -76
    sw $k0, -16($sp)
    # store i32 %v1, i32* %v4
    lw $k0, -16($sp)
    sw $a2, 0($k0)
    # %v5 = alloca i32
    addiu $k0, $sp, -80
    sw $k0, -20($sp)
    # %v6 = load i32, i32* %v3
    lw $k0, -12($sp)
    lw $k0, 0($k0)
    sw $k0, -24($sp)
    # %v7 = icmp eq i32 %v6, 0
    # br i1 %v7, label %b8, label %b11
    lw $k0, -24($sp)
    beq $k0, 0, add_bb8
    j add_bb11

add_bb8:
    # %v9 = load i32, i32* %v4
    lw $k0, -16($sp)
    lw $k0, 0($k0)
    sw $k0, -32($sp)
    # %v10 = icmp eq i32 %v9, 0
    # br i1 %v10, label %b16, label %b17
    lw $k0, -32($sp)
    beq $k0, 0, add_bb16
    j add_bb17

add_bb11:
    # %v12 = load i32, i32* %v4
    lw $k0, -16($sp)
    lw $k0, 0($k0)
    sw $k0, -40($sp)
    # %v13 = icmp eq i32 %v12, 0
    # br i1 %v13, label %b20, label %b22
    lw $k0, -40($sp)
    beq $k0, 0, add_bb20
    j add_bb22

add_bb14:
    # %v15 = load i32, i32* %v5
    lw $k0, -20($sp)
    lw $k0, 0($k0)
    sw $k0, -48($sp)
    # ret i32 %v15
    lw $v0, -48($sp)
    jr $ra

add_bb16:
    # store i32 0, i32* %v5
    lw $k0, -20($sp)
    li $k1, 0
    sw $k1, 0($k0)
    # br label %b19
    j add_bb19

add_bb17:
    # %v18 = load i32, i32* %v4
    lw $k0, -16($sp)
    lw $k0, 0($k0)
    sw $k0, -52($sp)
    # store i32 %v18, i32* %v5
    lw $k0, -20($sp)
    lw $k1, -52($sp)
    sw $k1, 0($k0)
    # br label %b19
    j add_bb19

add_bb19:
    # br label %b14
    j add_bb14

add_bb20:
    # %v21 = load i32, i32* %v3
    lw $k0, -12($sp)
    lw $k0, 0($k0)
    sw $k0, -56($sp)
    # store i32 %v21, i32* %v5
    lw $k0, -20($sp)
    lw $k1, -56($sp)
    sw $k1, 0($k0)
    # br label %b26
    j add_bb26

add_bb22:
    # %v23 = load i32, i32* %v3
    lw $k0, -12($sp)
    lw $k0, 0($k0)
    sw $k0, -60($sp)
    # %v24 = load i32, i32* %v4
    lw $k0, -16($sp)
    lw $k0, 0($k0)
    sw $k0, -64($sp)
    # %v25 = add i32 %v23, %v24
    lw $k0, -60($sp)
    lw $k1, -64($sp)
    addu $k0, $k0, $k1
    sw $k0, -68($sp)
    # store i32 %v25, i32* %v5
    lw $k0, -20($sp)
    lw $k1, -68($sp)
    sw $k1, 0($k0)
    # br label %b26
    j add_bb26

add_bb26:
    # br label %b14
    j add_bb14

func_multiply:
multiply_bb2:
    # %v3 = alloca i32
    addiu $k0, $sp, -56
    sw $k0, -12($sp)
    # store i32 %v0, i32* %v3
    lw $k0, -12($sp)
    sw $a1, 0($k0)
    # %v4 = alloca i32
    addiu $k0, $sp, -60
    sw $k0, -16($sp)
    # store i32 %v1, i32* %v4
    lw $k0, -16($sp)
    sw $a2, 0($k0)
    # %v5 = alloca i32
    addiu $k0, $sp, -64
    sw $k0, -20($sp)
    # %v6 = load i32, i32* %v3
    lw $k0, -12($sp)
    lw $k0, 0($k0)
    sw $k0, -24($sp)
    # %v7 = icmp ne i32 %v6, 0
    # br i1 %v7, label %b8, label %b11
    lw $k0, -24($sp)
    bne $k0, 0, multiply_bb8
    j multiply_bb11

multiply_bb8:
    # %v9 = load i32, i32* %v4
    lw $k0, -16($sp)
    lw $k0, 0($k0)
    sw $k0, -32($sp)
    # %v10 = icmp ne i32 %v9, 0
    # br i1 %v10, label %b14, label %b18
    lw $k0, -32($sp)
    bne $k0, 0, multiply_bb14
    j multiply_bb18

multiply_bb11:
    # store i32 0, i32* %v5
    lw $k0, -20($sp)
    li $k1, 0
    sw $k1, 0($k0)
    # br label %b12
    j multiply_bb12

multiply_bb12:
    # %v13 = load i32, i32* %v5
    lw $k0, -20($sp)
    lw $k0, 0($k0)
    sw $k0, -40($sp)
    # ret i32 %v13
    lw $v0, -40($sp)
    jr $ra

multiply_bb14:
    # %v15 = load i32, i32* %v3
    lw $k0, -12($sp)
    lw $k0, 0($k0)
    sw $k0, -44($sp)
    # %v16 = load i32, i32* %v4
    lw $k0, -16($sp)
    lw $k0, 0($k0)
    sw $k0, -48($sp)
    # %v17 = mul i32 %v15, %v16
    lw $k0, -44($sp)
    lw $k1, -48($sp)
    mul $k0, $k0, $k1
    sw $k0, -52($sp)
    # store i32 %v17, i32* %v5
    lw $k0, -20($sp)
    lw $k1, -52($sp)
    sw $k1, 0($k0)
    # br label %b19
    j multiply_bb19

multiply_bb18:
    # store i32 0, i32* %v5
    lw $k0, -20($sp)
    li $k1, 0
    sw $k1, 0($k0)
    # br label %b19
    j multiply_bb19

multiply_bb19:
    # br label %b12
    j multiply_bb12

func_process_array:
process_array_bb1:
    # %v2 = alloca i32
    addiu $k0, $sp, -92
    sw $k0, -8($sp)
    # store i32 0, i32* %v2
    lw $k0, -8($sp)
    li $k1, 0
    sw $k1, 0($k0)
    # br label %b3
    j process_array_bb3

process_array_bb3:
    # %v4 = load i32, i32* %v2
    lw $k0, -8($sp)
    lw $k0, 0($k0)
    sw $k0, -12($sp)
    # %v5 = icmp slt i32 %v4, 10
    # br i1 %v5, label %b6, label %b14
    lw $k0, -12($sp)
    blt $k0, 10, process_array_bb6
    j process_array_bb14

process_array_bb6:
    # %v7 = load i32, i32* %v2
    lw $k0, -8($sp)
    lw $k0, 0($k0)
    sw $k0, -20($sp)
    # %v8 = getelementptr inbounds i32, i32* %v0, i32 %v7
    lw $k1, -20($sp)
    sll $k1, $k1, 2
    addu $k0, $a1, $k1
    sw $k0, -24($sp)
    # %v9 = load i32, i32* %v8
    lw $k0, -24($sp)
    lw $k0, 0($k0)
    sw $k0, -28($sp)
    # %v10 = icmp sgt i32 %v9, 0
    # br i1 %v10, label %b15, label %b22
    lw $k0, -28($sp)
    bgt $k0, 0, process_array_bb15
    j process_array_bb22

process_array_bb11:
    # %v12 = load i32, i32* %v2
    lw $k0, -8($sp)
    lw $k0, 0($k0)
    sw $k0, -36($sp)
    # %v13 = add i32 %v12, 1
    lw $k0, -36($sp)
    addiu $k0, $k0, 1
    sw $k0, -40($sp)
    # store i32 %v13, i32* %v2
    lw $k0, -8($sp)
    lw $k1, -40($sp)
    sw $k1, 0($k0)
    # br label %b3
    j process_array_bb3

process_array_bb14:
    # ret void
    jr $ra

process_array_bb15:
    # %v16 = load i32, i32* %v2
    lw $k0, -8($sp)
    lw $k0, 0($k0)
    sw $k0, -44($sp)
    # %v17 = getelementptr inbounds i32, i32* %v0, i32 %v16
    lw $k1, -44($sp)
    sll $k1, $k1, 2
    addu $k0, $a1, $k1
    sw $k0, -48($sp)
    # %v18 = load i32, i32* %v2
    lw $k0, -8($sp)
    lw $k0, 0($k0)
    sw $k0, -52($sp)
    # %v19 = getelementptr inbounds i32, i32* %v0, i32 %v18
    lw $k1, -52($sp)
    sll $k1, $k1, 2
    addu $k0, $a1, $k1
    sw $k0, -56($sp)
    # %v20 = load i32, i32* %v19
    lw $k0, -56($sp)
    lw $k0, 0($k0)
    sw $k0, -60($sp)
    # %v21 = add i32 %v20, 1
    lw $k0, -60($sp)
    addiu $k0, $k0, 1
    sw $k0, -64($sp)
    # store i32 %v21, i32* %v17
    lw $k0, -48($sp)
    lw $k1, -64($sp)
    sw $k1, 0($k0)
    # br label %b29
    j process_array_bb29

process_array_bb22:
    # %v23 = load i32, i32* %v2
    lw $k0, -8($sp)
    lw $k0, 0($k0)
    sw $k0, -68($sp)
    # %v24 = getelementptr inbounds i32, i32* %v0, i32 %v23
    lw $k1, -68($sp)
    sll $k1, $k1, 2
    addu $k0, $a1, $k1
    sw $k0, -72($sp)
    # %v25 = load i32, i32* %v2
    lw $k0, -8($sp)
    lw $k0, 0($k0)
    sw $k0, -76($sp)
    # %v26 = getelementptr inbounds i32, i32* %v0, i32 %v25
    lw $k1, -76($sp)
    sll $k1, $k1, 2
    addu $k0, $a1, $k1
    sw $k0, -80($sp)
    # %v27 = load i32, i32* %v26
    lw $k0, -80($sp)
    lw $k0, 0($k0)
    sw $k0, -84($sp)
    # %v28 = sub i32 %v27, 1
    lw $k0, -84($sp)
    addiu $k0, $k0, -1
    sw $k0, -88($sp)
    # store i32 %v28, i32* %v24
    lw $k0, -72($sp)
    lw $k1, -88($sp)
    sw $k1, 0($k0)
    # br label %b29
    j process_array_bb29

process_array_bb29:
    # br label %b11
    j process_array_bb11

