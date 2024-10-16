.data:
s0: .asciiz "\n"

.text:
func_main:
main_b0:
    # %1 = call i32 @sum(i32 1, i32 9, i32 7, i32 -3, i32 2, i32 3, i32 4)
    sw $t0, -4($sp)
    sw $ra, -8($sp)
    li $a1, 1
    li $a2, 9
    li $a3, 7
    li $k0, -3
    sw $k0, -24($sp)
    li $k0, 2
    sw $k0, -28($sp)
    li $k0, 3
    sw $k0, -32($sp)
    li $k0, 4
    sw $k0, -36($sp)
    addiu $sp, $sp, -8
    jal func_sum
    lw $ra, 0($sp)
    addiu $sp, $sp, 8
    lw $t0, -4($sp)
    move $t0, $v0
    # call void @putint(i32 %1)
    move $a0, $t0
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.0, i64 0, i64 0))
    la $a0, s0
    li $v0, 4
    syscall
    # ret i32 0
    li $v0, 10
    syscall

func_sum:
sum_b7:
    # %8 = add i32 %0, %1
    addu $t0, $a1, $a2
    # %9 = add i32 %8, %2
    addu $t0, $t0, $a3
    # %10 = add i32 %9, %3
    lw $k1, -16($sp)
    addu $t0, $t0, $k1
    # %11 = add i32 %10, %4
    lw $k1, -20($sp)
    addu $t0, $t0, $k1
    # %12 = add i32 %11, %5
    lw $k1, -24($sp)
    addu $t0, $t0, $k1
    # %13 = add i32 %12, %6
    lw $k1, -28($sp)
    addu $t0, $t0, $k1
    # ret i32 %13
    move $v0, $t0
    jr $ra

