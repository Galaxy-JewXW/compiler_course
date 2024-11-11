.data:
s0: .asciiz "\n"

.text:
func_main:
main_bb0:
    # %v1 = call i32 @getint()
    li $v0, 5
    syscall
    move $t0, $v0
    # %v2 = sdiv i32 %v1, 2
    sra $t0, $t0, 1
    # call void @putint(i32 %v2)
    move $a0, $t0
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.0, i64 0, i64 0))
    li $v0, 11
    li $a0, 10
    syscall
    # ret i32 0
    li $v0, 10
    syscall

