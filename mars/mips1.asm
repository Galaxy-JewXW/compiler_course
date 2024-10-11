.data:
s0: .asciiz "\n"

.text:
func_main:
main_b0:
    # %1 = call i32 @getint()
    li $v0, 5
    syscall
    move $t0, $v0
    # %2 = call i32 @getint()
    li $v0, 5
    syscall
    move $t1, $v0
    # %3 = icmp sgt i32 %1, 0
    # br i1 %3, label %4, label %7
    blt $t0, 0, main_b7

main_b4:
    # %5 = mul i32 %1, -2
    addu $t0, $t0, $t0
    subu $t0, $zero, $t0
    # %6 = mul i32 -3, %5
    addu $v0, $t0, $t0
    addu $t0, $v0, $t0
    subu $t0, $zero, $t0
    # move %6 -> %10
    # br label %9
    j main_b9

main_b7:
    # %8 = sdiv i32 %2, -3
    li $v0, 1431655766
    mult $t1, $v0
    mfhi $v1
    sra $v0, $v1, 0
    srl $a0, $t1, 31
    addu $t0, $v0, $a0
    subu $t0, $zero, $t0
    # move %8 -> %10
    # br label %9

main_b9:
    # call void @putint(i32 %10)
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

