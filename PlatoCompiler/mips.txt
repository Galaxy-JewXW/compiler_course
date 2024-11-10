.data:
s0: .asciiz "I got\n: "
s1: .asciiz "\n"

.text:
func_main:
main_bb0:
    # %v1 = call i32 @getint()
    li $v0, 5
    syscall
    move $t0, $v0
    # call void @putstr(i8* getelementptr inbounds ([9 x i8], [9 x i8]* @.s.0, i64 0, i64 0))
    la $a0, s0
    li $v0, 4
    syscall
    # call void @putint(i32 %v1)
    move $a0, $t0
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.1, i64 0, i64 0))
    li $v0, 11
    li $a0, 10
    syscall
    # ret i32 0
    li $v0, 10
    syscall

