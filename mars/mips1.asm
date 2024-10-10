.data:
s0: .asciiz ": "
s1: .asciiz " "
s2: .asciiz "\n"
s3: .asciiz "global init: "
ga: .word 0
gb: .word 0
gc: .word 0

.text:
func_main:
main_b0:
    # %1 = load i32, i32* @ga
    la $k0, ga
    lw $k0, 0($k0)
    sw $k0, -4($sp)
    # %2 = load i32, i32* @gb
    la $k0, gb
    lw $k0, 0($k0)
    sw $k0, -8($sp)
    # %3 = load i32, i32* @gc
    la $k0, gc
    lw $k0, 0($k0)
    sw $k0, -12($sp)
    # call void @putstr(i8* getelementptr inbounds ([14 x i8], [14 x i8]* @.s.3, i64 0, i64 0))
    la $a0, s3
    li $v0, 4
    syscall
    # call void @putint(i32 %1)
    lw $a0, -4($sp)
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.1, i64 0, i64 0))
    la $a0, s1
    li $v0, 4
    syscall
    # call void @putint(i32 %2)
    lw $a0, -8($sp)
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.1, i64 0, i64 0))
    la $a0, s1
    li $v0, 4
    syscall
    # call void @putint(i32 %3)
    lw $a0, -12($sp)
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.2, i64 0, i64 0))
    la $a0, s2
    li $v0, 4
    syscall
    # %4 = alloca i32
    addiu $k0, $sp, -120
    sw $k0, -16($sp)
    # store i32 13, i32* %4
    lw $k0, -16($sp)
    li $k1, 13
    sw $k1, 0($k0)
    # %5 = alloca i32
    addiu $k0, $sp, -124
    sw $k0, -20($sp)
    # %6 = load i32, i32* %4
    lw $k0, -16($sp)
    lw $k0, 0($k0)
    sw $k0, -24($sp)
    # %7 = add i32 %6, 7
    lw $k0, -24($sp)
    addiu $k0, $k0, 7
    sw $k0, -28($sp)
    # store i32 %7, i32* %5
    lw $k0, -20($sp)
    lw $k1, -28($sp)
    sw $k1, 0($k0)
    # %8 = load i32, i32* %5
    lw $k0, -20($sp)
    lw $k0, 0($k0)
    sw $k0, -32($sp)
    # call void @addGaGb(i32 %8)
    sw $ra, -128($sp)
    lw $a1, -32($sp)
    addiu $sp, $sp, -128
    jal func_addGaGb
    lw $ra, 0($sp)
    addiu $sp, $sp, 128
    # call void @publish(i32 1)
    sw $ra, -128($sp)
    li $a1, 1
    addiu $sp, $sp, -128
    jal func_publish
    lw $ra, 0($sp)
    addiu $sp, $sp, 128
    # %9 = load i32, i32* %5
    lw $k0, -20($sp)
    lw $k0, 0($k0)
    sw $k0, -36($sp)
    # %10 = sub i32 %9, 20
    lw $k0, -36($sp)
    addiu $k0, $k0, -20
    sw $k0, -40($sp)
    # %11 = load i32, i32* %5
    lw $k0, -20($sp)
    lw $k0, 0($k0)
    sw $k0, -44($sp)
    # %12 = sub i32 %11, 10
    lw $k0, -44($sp)
    addiu $k0, $k0, -10
    sw $k0, -48($sp)
    # %13 = mul i32 %12, 3
    lw $k0, -48($sp)
    addu $v0, $k0, $k0
    addu $k0, $v0, $k0
    sw $k0, -52($sp)
    # %14 = add i32 %10, %13
    lw $k0, -40($sp)
    lw $k1, -52($sp)
    addu $k0, $k0, $k1
    sw $k0, -56($sp)
    # call void @addGaGbGc(i32 %14)
    sw $ra, -128($sp)
    lw $a1, -56($sp)
    addiu $sp, $sp, -128
    jal func_addGaGbGc
    lw $ra, 0($sp)
    addiu $sp, $sp, 128
    # call void @publish(i32 2)
    sw $ra, -128($sp)
    li $a1, 2
    addiu $sp, $sp, -128
    jal func_publish
    lw $ra, 0($sp)
    addiu $sp, $sp, 128
    # %15 = load i32, i32* %5
    lw $k0, -20($sp)
    lw $k0, 0($k0)
    sw $k0, -60($sp)
    # %16 = call i32 @mult(i32 10, i32 %15)
    sw $ra, -128($sp)
    li $a1, 10
    lw $a2, -60($sp)
    addiu $sp, $sp, -128
    jal func_mult
    lw $ra, 0($sp)
    addiu $sp, $sp, 128
    sw $v0, -64($sp)
    # call void @addGaGbGc(i32 %16)
    sw $ra, -128($sp)
    lw $a1, -64($sp)
    addiu $sp, $sp, -128
    jal func_addGaGbGc
    lw $ra, 0($sp)
    addiu $sp, $sp, 128
    # call void @publish(i32 3)
    sw $ra, -128($sp)
    li $a1, 3
    addiu $sp, $sp, -128
    jal func_publish
    lw $ra, 0($sp)
    addiu $sp, $sp, 128
    # %17 = load i32, i32* %5
    lw $k0, -20($sp)
    lw $k0, 0($k0)
    sw $k0, -68($sp)
    # %18 = load i32, i32* %5
    lw $k0, -20($sp)
    lw $k0, 0($k0)
    sw $k0, -72($sp)
    # %19 = call i32 @add(i32 %18, i32 50)
    sw $ra, -128($sp)
    lw $a1, -72($sp)
    li $a2, 50
    addiu $sp, $sp, -128
    jal func_add
    lw $ra, 0($sp)
    addiu $sp, $sp, 128
    sw $v0, -76($sp)
    # %20 = call i32 @sub(i32 %17, i32 %19)
    sw $ra, -128($sp)
    lw $a1, -68($sp)
    lw $a2, -76($sp)
    addiu $sp, $sp, -128
    jal func_sub
    lw $ra, 0($sp)
    addiu $sp, $sp, 128
    sw $v0, -80($sp)
    # %21 = sub i32 0, %20
    lw $k0, -80($sp)
    li $k1, 0
    subu $k0, $k1, $k0
    sw $k0, -84($sp)
    # %22 = sub i32 0, 1
    li $k0, 0
    addiu $k0, $k0, -1
    sw $k0, -88($sp)
    # %23 = load i32, i32* %5
    lw $k0, -20($sp)
    lw $k0, 0($k0)
    sw $k0, -92($sp)
    # %24 = call i32 @mult(i32 %22, i32 %23)
    sw $ra, -128($sp)
    lw $a1, -88($sp)
    lw $a2, -92($sp)
    addiu $sp, $sp, -128
    jal func_mult
    lw $ra, 0($sp)
    addiu $sp, $sp, 128
    sw $v0, -96($sp)
    # %25 = mul i32 %21, %24
    lw $k0, -84($sp)
    lw $k1, -96($sp)
    mul $k0, $k0, $k1
    sw $k0, -100($sp)
    # %26 = load i32, i32* %5
    lw $k0, -20($sp)
    lw $k0, 0($k0)
    sw $k0, -104($sp)
    # %27 = call i32 @add(i32 %26, i32 3)
    sw $ra, -128($sp)
    lw $a1, -104($sp)
    li $a2, 3
    addiu $sp, $sp, -128
    jal func_add
    lw $ra, 0($sp)
    addiu $sp, $sp, 128
    sw $v0, -108($sp)
    # %28 = call i32 @mult(i32 %27, i32 2)
    sw $ra, -128($sp)
    lw $a1, -108($sp)
    li $a2, 2
    addiu $sp, $sp, -128
    jal func_mult
    lw $ra, 0($sp)
    addiu $sp, $sp, 128
    sw $v0, -112($sp)
    # %29 = srem i32 %25, %28
    lw $k0, -100($sp)
    lw $k1, -112($sp)
    div $k0, $k1
    mfhi $k0
    sw $k0, -116($sp)
    # call void @addGaGbGc(i32 %29)
    sw $ra, -128($sp)
    lw $a1, -116($sp)
    addiu $sp, $sp, -128
    jal func_addGaGbGc
    lw $ra, 0($sp)
    addiu $sp, $sp, 128
    # call void @publish(i32 4)
    sw $ra, -128($sp)
    li $a1, 4
    addiu $sp, $sp, -128
    jal func_publish
    lw $ra, 0($sp)
    addiu $sp, $sp, 128
    # ret i32 0
    li $v0, 10
    syscall

func_add:
add_b2:
    # %3 = alloca i32
    addiu $k0, $sp, -32
    sw $k0, -12($sp)
    # store i32 %0, i32* %3
    lw $k0, -12($sp)
    sw $a1, 0($k0)
    # %4 = alloca i32
    addiu $k0, $sp, -36
    sw $k0, -16($sp)
    # store i32 %1, i32* %4
    lw $k0, -16($sp)
    sw $a2, 0($k0)
    # %5 = load i32, i32* %3
    lw $k0, -12($sp)
    lw $k0, 0($k0)
    sw $k0, -20($sp)
    # %6 = load i32, i32* %4
    lw $k0, -16($sp)
    lw $k0, 0($k0)
    sw $k0, -24($sp)
    # %7 = add i32 %5, %6
    lw $k0, -20($sp)
    lw $k1, -24($sp)
    addu $k0, $k0, $k1
    sw $k0, -28($sp)
    # ret i32 %7
    lw $v0, -28($sp)
    jr $ra

func_sub:
sub_b2:
    # %3 = alloca i32
    addiu $k0, $sp, -36
    sw $k0, -12($sp)
    # store i32 %0, i32* %3
    lw $k0, -12($sp)
    sw $a1, 0($k0)
    # %4 = alloca i32
    addiu $k0, $sp, -40
    sw $k0, -16($sp)
    # store i32 %1, i32* %4
    lw $k0, -16($sp)
    sw $a2, 0($k0)
    # %5 = load i32, i32* %3
    lw $k0, -12($sp)
    lw $k0, 0($k0)
    sw $k0, -20($sp)
    # %6 = load i32, i32* %4
    lw $k0, -16($sp)
    lw $k0, 0($k0)
    sw $k0, -24($sp)
    # %7 = sub i32 %5, %6
    lw $k0, -20($sp)
    lw $k1, -24($sp)
    subu $k0, $k0, $k1
    sw $k0, -28($sp)
    # store i32 %7, i32* %4
    lw $k0, -16($sp)
    lw $k1, -28($sp)
    sw $k1, 0($k0)
    # %8 = load i32, i32* %4
    lw $k0, -16($sp)
    lw $k0, 0($k0)
    sw $k0, -32($sp)
    # ret i32 %8
    lw $v0, -32($sp)
    jr $ra

func_mult:
mult_b2:
    # %3 = alloca i32
    addiu $k0, $sp, -40
    sw $k0, -12($sp)
    # store i32 %0, i32* %3
    lw $k0, -12($sp)
    sw $a1, 0($k0)
    # %4 = alloca i32
    addiu $k0, $sp, -44
    sw $k0, -16($sp)
    # store i32 %1, i32* %4
    lw $k0, -16($sp)
    sw $a2, 0($k0)
    # %5 = alloca i32
    addiu $k0, $sp, -48
    sw $k0, -20($sp)
    # %6 = load i32, i32* %3
    lw $k0, -12($sp)
    lw $k0, 0($k0)
    sw $k0, -24($sp)
    # %7 = load i32, i32* %4
    lw $k0, -16($sp)
    lw $k0, 0($k0)
    sw $k0, -28($sp)
    # %8 = mul i32 %6, %7
    lw $k0, -24($sp)
    lw $k1, -28($sp)
    mul $k0, $k0, $k1
    sw $k0, -32($sp)
    # store i32 %8, i32* %5
    lw $k0, -20($sp)
    lw $k1, -32($sp)
    sw $k1, 0($k0)
    # %9 = load i32, i32* %5
    lw $k0, -20($sp)
    lw $k0, 0($k0)
    sw $k0, -36($sp)
    # ret i32 %9
    lw $v0, -36($sp)
    jr $ra

func_addGaGb:
addGaGb_b1:
    # %2 = alloca i32
    addiu $k0, $sp, -40
    sw $k0, -8($sp)
    # store i32 %0, i32* %2
    lw $k0, -8($sp)
    sw $a1, 0($k0)
    # %3 = load i32, i32* @ga
    la $k0, ga
    lw $k0, 0($k0)
    sw $k0, -12($sp)
    # %4 = load i32, i32* %2
    lw $k0, -8($sp)
    lw $k0, 0($k0)
    sw $k0, -16($sp)
    # %5 = call i32 @add(i32 %3, i32 %4)
    sw $a1, -44($sp)
    sw $ra, -48($sp)
    lw $a1, -12($sp)
    lw $a2, -16($sp)
    addiu $sp, $sp, -48
    jal func_add
    lw $ra, 0($sp)
    addiu $sp, $sp, 48
    lw $a1, -44($sp)
    sw $v0, -20($sp)
    # store i32 %5, i32* @ga
    la $k0, ga
    lw $k1, -20($sp)
    sw $k1, 0($k0)
    # %6 = load i32, i32* @gb
    la $k0, gb
    lw $k0, 0($k0)
    sw $k0, -24($sp)
    # %7 = load i32, i32* %2
    lw $k0, -8($sp)
    lw $k0, 0($k0)
    sw $k0, -28($sp)
    # %8 = sub i32 0, %7
    lw $k0, -28($sp)
    li $k1, 0
    subu $k0, $k1, $k0
    sw $k0, -32($sp)
    # %9 = call i32 @sub(i32 %6, i32 %8)
    sw $a1, -44($sp)
    sw $ra, -48($sp)
    lw $a1, -24($sp)
    lw $a2, -32($sp)
    addiu $sp, $sp, -48
    jal func_sub
    lw $ra, 0($sp)
    addiu $sp, $sp, 48
    lw $a1, -44($sp)
    sw $v0, -36($sp)
    # store i32 %9, i32* @gb
    la $k0, gb
    lw $k1, -36($sp)
    sw $k1, 0($k0)
    # ret void
    jr $ra

func_addGaGbGc:
addGaGbGc_b1:
    # %2 = alloca i32
    addiu $k0, $sp, -88
    sw $k0, -8($sp)
    # store i32 %0, i32* %2
    lw $k0, -8($sp)
    sw $a1, 0($k0)
    # %3 = load i32, i32* %2
    lw $k0, -8($sp)
    lw $k0, 0($k0)
    sw $k0, -12($sp)
    # %4 = load i32, i32* %2
    lw $k0, -8($sp)
    lw $k0, 0($k0)
    sw $k0, -16($sp)
    # %5 = call i32 @sub(i32 %3, i32 %4)
    sw $a1, -92($sp)
    sw $ra, -96($sp)
    lw $a1, -12($sp)
    lw $a2, -16($sp)
    addiu $sp, $sp, -96
    jal func_sub
    lw $ra, 0($sp)
    addiu $sp, $sp, 96
    lw $a1, -92($sp)
    sw $v0, -20($sp)
    # %6 = load i32, i32* %2
    lw $k0, -8($sp)
    lw $k0, 0($k0)
    sw $k0, -24($sp)
    # %7 = call i32 @add(i32 0, i32 1)
    sw $a1, -92($sp)
    sw $ra, -96($sp)
    li $a1, 0
    li $a2, 1
    addiu $sp, $sp, -96
    jal func_add
    lw $ra, 0($sp)
    addiu $sp, $sp, 96
    lw $a1, -92($sp)
    sw $v0, -28($sp)
    # %8 = call i32 @mult(i32 %6, i32 %7)
    sw $a1, -92($sp)
    sw $ra, -96($sp)
    lw $a1, -24($sp)
    lw $a2, -28($sp)
    addiu $sp, $sp, -96
    jal func_mult
    lw $ra, 0($sp)
    addiu $sp, $sp, 96
    lw $a1, -92($sp)
    sw $v0, -32($sp)
    # %9 = add i32 %5, %8
    lw $k0, -20($sp)
    lw $k1, -32($sp)
    addu $k0, $k0, $k1
    sw $k0, -36($sp)
    # call void @addGaGb(i32 %9)
    sw $a1, -92($sp)
    sw $ra, -96($sp)
    lw $a1, -36($sp)
    addiu $sp, $sp, -96
    jal func_addGaGb
    lw $ra, 0($sp)
    addiu $sp, $sp, 96
    lw $a1, -92($sp)
    # %10 = load i32, i32* @gc
    la $k0, gc
    lw $k0, 0($k0)
    sw $k0, -40($sp)
    # %11 = load i32, i32* %2
    lw $k0, -8($sp)
    lw $k0, 0($k0)
    sw $k0, -44($sp)
    # %12 = call i32 @mult(i32 1, i32 %11)
    sw $a1, -92($sp)
    sw $ra, -96($sp)
    li $a1, 1
    lw $a2, -44($sp)
    addiu $sp, $sp, -96
    jal func_mult
    lw $ra, 0($sp)
    addiu $sp, $sp, 96
    lw $a1, -92($sp)
    sw $v0, -48($sp)
    # %13 = load i32, i32* %2
    lw $k0, -8($sp)
    lw $k0, 0($k0)
    sw $k0, -52($sp)
    # %14 = load i32, i32* %2
    lw $k0, -8($sp)
    lw $k0, 0($k0)
    sw $k0, -56($sp)
    # %15 = call i32 @add(i32 %13, i32 %14)
    sw $a1, -92($sp)
    sw $ra, -96($sp)
    lw $a1, -52($sp)
    lw $a2, -56($sp)
    addiu $sp, $sp, -96
    jal func_add
    lw $ra, 0($sp)
    addiu $sp, $sp, 96
    lw $a1, -92($sp)
    sw $v0, -60($sp)
    # %16 = add i32 %12, %15
    lw $k0, -48($sp)
    lw $k1, -60($sp)
    addu $k0, $k0, $k1
    sw $k0, -64($sp)
    # %17 = load i32, i32* %2
    lw $k0, -8($sp)
    lw $k0, 0($k0)
    sw $k0, -68($sp)
    # %18 = mul i32 2, %17
    lw $k0, -68($sp)
    addu $k0, $k0, $k0
    sw $k0, -72($sp)
    # %19 = call i32 @sub(i32 0, i32 %18)
    sw $a1, -92($sp)
    sw $ra, -96($sp)
    li $a1, 0
    lw $a2, -72($sp)
    addiu $sp, $sp, -96
    jal func_sub
    lw $ra, 0($sp)
    addiu $sp, $sp, 96
    lw $a1, -92($sp)
    sw $v0, -76($sp)
    # %20 = add i32 %16, %19
    lw $k0, -64($sp)
    lw $k1, -76($sp)
    addu $k0, $k0, $k1
    sw $k0, -80($sp)
    # %21 = call i32 @add(i32 %10, i32 %20)
    sw $a1, -92($sp)
    sw $ra, -96($sp)
    lw $a1, -40($sp)
    lw $a2, -80($sp)
    addiu $sp, $sp, -96
    jal func_add
    lw $ra, 0($sp)
    addiu $sp, $sp, 96
    lw $a1, -92($sp)
    sw $v0, -84($sp)
    # store i32 %21, i32* @gc
    la $k0, gc
    lw $k1, -84($sp)
    sw $k1, 0($k0)
    # ret void
    jr $ra

func_publish:
publish_b1:
    # %2 = alloca i32
    addiu $k0, $sp, -28
    sw $k0, -8($sp)
    # store i32 %0, i32* %2
    lw $k0, -8($sp)
    sw $a1, 0($k0)
    # %3 = load i32, i32* %2
    lw $k0, -8($sp)
    lw $k0, 0($k0)
    sw $k0, -12($sp)
    # %4 = load i32, i32* @ga
    la $k0, ga
    lw $k0, 0($k0)
    sw $k0, -16($sp)
    # %5 = load i32, i32* @gb
    la $k0, gb
    lw $k0, 0($k0)
    sw $k0, -20($sp)
    # %6 = load i32, i32* @gc
    la $k0, gc
    lw $k0, 0($k0)
    sw $k0, -24($sp)
    # call void @putint(i32 %3)
    lw $a0, -12($sp)
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.s.0, i64 0, i64 0))
    la $a0, s0
    li $v0, 4
    syscall
    # call void @putint(i32 %4)
    lw $a0, -16($sp)
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.1, i64 0, i64 0))
    la $a0, s1
    li $v0, 4
    syscall
    # call void @putint(i32 %5)
    lw $a0, -20($sp)
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.1, i64 0, i64 0))
    la $a0, s1
    li $v0, 4
    syscall
    # call void @putint(i32 %6)
    lw $a0, -24($sp)
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.2, i64 0, i64 0))
    la $a0, s2
    li $v0, 4
    syscall
    # ret void
    jr $ra

